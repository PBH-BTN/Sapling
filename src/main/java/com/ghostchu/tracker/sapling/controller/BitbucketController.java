package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.util.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Slf4j
@Controller
@Data
@RequestMapping("/bitbucket")
public class BitbucketController {
    @Autowired
    private ISettingsService settingsService;
    @Autowired
    private IBitbucketService bitbucketService;
    @Autowired
    private HttpServletRequest request;
    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;
    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    private static final String[] safeMime = new String[]{"image", "audio", "video", "font", "text"};

    @GetMapping("/upload")
    @SaCheckPermission(Permission.BITBUCKET_UPLOAD)
    public String upload(Model model) {
        long maxFileSizeInBytes = DataSize.parse(maxFileSize).toBytes();
        long maxRequestSizeInBytes = DataSize.parse(maxRequestSize).toBytes();
        model.addAttribute("maxFileSize", Math.min(maxFileSizeInBytes, maxRequestSizeInBytes));
        var allowedExtensions = settingsService.getValue(Setting.BITBUCKET_ALLOWED_FILE_EXTENSION).split(";");
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (String allowedExtension : allowedExtensions) {
            joiner.add("'" + allowedExtension + "'");
        }
        model.addAttribute("allowedFileExtensions", joiner.toString());
        return "bitbucket/upload";
    }

    @PostMapping("/upload")
    @ResponseBody
    @SaCheckPermission(Permission.BITBUCKET_UPLOAD)
    public Map<String, String> upload(@RequestParam MultipartFile file, @RequestParam String name, @RequestParam String type) throws IOException {
        Bitbucket bitbucket = bitbucketService.uploadToBitbucket(file, StpUtil.getLoginIdAsLong(), type, true, false);
        return Map.of("url", request.getContextPath() + "/bitbucket/file/" + bitbucket.getId());
    }


    @GetMapping("/file/{id}")
    public ResponseEntity<?> file(@PathVariable Long id) throws IOException {
        Bitbucket bitbucket = bitbucketService.getBitBucket(id);
        if (bitbucket == null) {
            return ResponseEntity.notFound().build();
        }
        if (!bitbucket.isDirectAccess()) {
            throw new IllegalStateException("指定的 BitBucket 资源不允许通过 BitBucket Access File 端点直接访问");
        }
        InputStream inputStream = bitbucketService.readBitBucket(bitbucket);
        var mimeType = FileUtil.mime(bitbucket.getDisplayName(), null);
        if (mimeType == null) {
            // 未知文件类型
            try {
                mimeType = MimeType.valueOf(bitbucket.getMime());
            } catch (InvalidMimeTypeException e) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM;
            }
        }
        String fileName = URLEncoder.encode(bitbucket.getDisplayName(), StandardCharsets.UTF_8);
        for (String s : safeMime) {
            if (mimeType.getType().equals(s)) {
                return ResponseEntity.ok()
                        .contentType(MediaType.asMediaType(mimeType))
                        .header("Content-Disposition", "inline;filename=" + fileName + ";filename*=UTF-8" + fileName)
                        .body(new InputStreamResource(inputStream));
            }
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8" + fileName)
                .body(inputStream);

    }

}
