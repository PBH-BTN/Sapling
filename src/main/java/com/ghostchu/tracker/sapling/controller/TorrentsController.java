package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.TorrentUploadFormDTO;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.service.ICategoriesService;
import com.ghostchu.tracker.sapling.service.IThanksService;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.ThanksVO;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Controller
@RequestMapping("/torrents")
@Data
public class TorrentsController {
    @Autowired
    private ITorrentsService torrentsService;
    @Autowired
    private ICategoriesService categoriesService;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private IThanksService thanksService;

    @GetMapping
    public String torrentList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {
        // 获取分页数据
        IPage<Torrents> pageResult = torrentsService.getTorrentsByPage(page, size, false, false);

        // 准备模型数据
        model.addAttribute("torrents", pageResult.getRecords().stream().map(torrentsService::toVO).toList());
        model.addAttribute("pagination", pageResult);

        return "torrents";
    }

    @GetMapping("/{id}")
    public String torrentDetail(@PathVariable long id, Model model) {
        // 获取种子详情
        Torrents torrent = torrentsService.getTorrentById(id);
        if (torrent == null) {
            return "redirect:/torrents";
        }
        // 准备模型数据
        model.addAttribute("torrent", torrentsService.toDetailsVO(torrent));
        var recentThanks = thanksService.getThanksByPageByTorrent(id, 1, 20);
        model.addAttribute("totalThanks", recentThanks.getTotal());
        List<ThanksVO> thanksVOList = recentThanks.getRecords().stream().map(thanksService::toVO).toList();
        model.addAttribute("thanks", thanksVOList);
        model.addAttribute("thankedTorrent", thanksService.isUserThankedTorrent(StpUtil.getLoginIdAsLong(), id));
        return "torrents/detail";
    }


    @GetMapping("/upload")
    public String uploadForm(Model model) {
        // 初始化表单对象
        model.addAttribute("torrent", new TorrentUploadFormDTO());
        // 获取所有分类供下拉选择
        model.addAttribute("categories", categoriesService.getAllCategories());
        return "torrents/upload";
    }

    @PostMapping("/upload")
    public String handleUpload(
            @ModelAttribute("torrent") @Valid TorrentUploadFormDTO form,
            BindingResult result,
            @RequestParam("torrentFile") MultipartFile file,
            Model model) throws IOException {

        // 验证文件类型
        if (file.isEmpty() || !Objects.equals(file.getContentType(), "application/x-bittorrent")) {
            result.rejectValue("torrentFile", "error.invalid", "请上传有效的 .torrent 文件");
            return "torrents/upload";
        }

        if (result.hasErrors()) {
            model.addAttribute("categories", categoriesService.getAllCategories());
            return "torrents/upload";
        }

        // 处理文件上传
        Torrents newTorrent = torrentsService.createTorrent(StpUtil.getLoginIdAsLong(), file, form.getCategoryId(), form.getTitle(), form.getSubtitle(), form.getDescription(), form.isAnonymous());
        return "redirect:/torrents/" + newTorrent.getId();
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadTorrent(@PathVariable long id) throws IOException {
        Torrents torrent = torrentsService.getTorrentById(id);
        if (torrent == null) {
            return ResponseEntity.notFound().build();
        }
        Users user = usersService.getUserById(StpUtil.getLoginIdAsLong());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + torrent.getTitle() + ".torrent\"")
                .body(new InputStreamResource(new ByteArrayInputStream(torrentsService.downloadTorrentForUser(torrent, user))));
    }
}
