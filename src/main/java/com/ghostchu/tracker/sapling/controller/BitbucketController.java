package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Bitbucket;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.model.StdMsg;
import com.ghostchu.tracker.sapling.service.IBitbucketService;
import com.ghostchu.tracker.sapling.service.ISettingsService;
import com.ghostchu.tracker.sapling.util.FileUtil;
import com.ghostchu.tracker.sapling.vo.BitbucketVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> upload(@RequestParam MultipartFile file, @RequestParam(required = false) String name, @RequestParam(required = false) String type) throws IOException {
        if (type == null) {
            if (name == null) {
                name = file.getOriginalFilename();
            }
            if (name != null) {
                type = FileUtil.mime(name, null).getType();
            }
        }
        Bitbucket bitbucket = bitbucketService.uploadToBitbucket(file, StpUtil.getLoginIdAsLong(), type, true, false);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new StdMsg<>(true, null, Map.of("url", request.getContextPath() + "/bitbucket/file/" + bitbucket.getId())));
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
        String fileName = bitbucket.getDisplayName();
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

    @GetMapping
    public String listFiles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {
        Page<Bitbucket> pageQuery = new Page<>(page, size);
        IPage<Bitbucket> files = bitbucketService.listUserFiles(StpUtil.getLoginIdAsLong(), pageQuery);
        Page<BitbucketVO> filesVO = new Page<>(page, size, files.getTotal(), files.searchCount());
        filesVO.setRecords(files.getRecords().stream().map(bitbucketService::toVO).toList());
        model.addAttribute("files", files);
        return "bitbucket/index";
    }

    @PostMapping("/delete/{id}")
    @SaCheckPermission(value = {Permission.BITBUCKET_DELETE, Permission.BITBUCKET_DELETE_OTHER}, mode = SaMode.OR)
    public String deleteFile(@PathVariable Long id) {
        Bitbucket bitbucket = bitbucketService.getBitBucket(id);
        if (bitbucket == null) {
            return "redirect:/bitbucket/index";
        }
        if (bitbucket.getOwner() == StpUtil.getLoginIdAsLong()) {
            StpUtil.checkPermission(Permission.BITBUCKET_DELETE);
        } else {
            StpUtil.checkPermission(Permission.BITBUCKET_DELETE_OTHER);
        }
        bitbucketService.deleteById(id);
        return "redirect:/bitbucket/index";
    }

}
