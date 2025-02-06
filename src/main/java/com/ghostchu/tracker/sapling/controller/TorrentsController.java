package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.TorrentEditFormDTO;
import com.ghostchu.tracker.sapling.dto.TorrentUploadFormDTO;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.exception.TorrentNotExistsException;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.*;
import com.ghostchu.tracker.sapling.vo.CategoryVO;
import com.ghostchu.tracker.sapling.vo.ThanksVO;
import com.ghostchu.tracker.sapling.vo.TorrentDetailsVO;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
    @Autowired
    private ITorrentReviewQueueService torrentReviewQueueService;

    @GetMapping
    @SaCheckPermission(value = {Permission.TORRENT_VIEW})
    public String torrentList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") int size,
            Model model) {
        // 获取分页数据
        IPage<Torrents> pageResult = torrentsService.getTorrentsByPage(page,
                size,
                StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE),
                StpUtil.hasPermission(Permission.TORRENT_VIEW_DELETED));
        // 准备模型数据
        model.addAttribute("torrents", pageResult.getRecords().stream().map(torrentsService::toVO).toList());
        model.addAttribute("pagination", pageResult);
        return "torrents";
    }

    @GetMapping("/{id}")
    @SaCheckPermission(value = {Permission.TORRENT_VIEW})
    public String torrentDetail(@PathVariable long id, Model model) {
        // 获取种子详情
        Torrents torrent = torrentsService.getTorrentById(id);
        if (torrent == null
                || (!torrent.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))  // 处于不可见状态
                || (torrent.getDeletedAt() != null && !StpUtil.hasPermission(Permission.TORRENT_VIEW_DELETED))) { // 已被删除
            throw new TorrentNotExistsException(null, id, "种子不存在或已被删除");
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
    @SaCheckPermission(value = {Permission.TORRENT_SUBMIT, Permission.TORRENT_QUEUE}, mode = SaMode.OR)
    public String uploadForm(Model model) {
        // 初始化表单对象
        model.addAttribute("torrent", new TorrentUploadFormDTO());
        // 获取所有分类供下拉选择
        model.addAttribute("categories", categoriesService.getAllCategories());
        return "torrents/upload";
    }

    @PostMapping("/upload")
    @SaCheckPermission(value = {Permission.TORRENT_SUBMIT, Permission.TORRENT_QUEUE}, mode = SaMode.OR)
    @Transactional
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
        Torrents newTorrent;
        // 根据权限节点决定是提交到种子列表还是种子待审队列
        if (StpUtil.hasPermission(Permission.TORRENT_SUBMIT)) {
            newTorrent = torrentsService.createTorrent(StpUtil.getLoginIdAsLong(), file,
                    form.getCategoryId(), form.getTitle(), form.getSubtitle(),
                    form.getDescription(), form.isAnonymous(), true);
        } else {
            newTorrent = torrentsService.createTorrent(StpUtil.getLoginIdAsLong(), file,
                    form.getCategoryId(), form.getTitle(), form.getSubtitle(),
                    form.getDescription(), form.isAnonymous(), false);
            torrentReviewQueueService.queueTorrent(newTorrent.getId());
        }
        return "redirect:/torrents/" + newTorrent.getId();
    }

    @GetMapping("/{id}/download")
    @SaCheckPermission(value = {Permission.TORRENT_VIEW, Permission.TORRENT_DOWNLOAD})
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


    // 显示编辑页面
    @GetMapping("/{id}/edit")
    @SaCheckPermission(value = {Permission.TORRENT_EDIT, Permission.TORRENT_EDIT_OTHER}, mode = SaMode.OR)
    public String showEditForm(@PathVariable Long id, Model model) {
        // 获取种子详细信息（示例代码）
        TorrentDetailsVO torrent = torrentsService.toDetailsVO(torrentsService.getTorrentById(id));
        if (StpUtil.getLoginIdAsLong() != torrent.getOwner().getId()) {
            return "redirect:/torrents/" + id; // 权限不足
        }
        var form = new TorrentEditFormDTO();
        form.setCategoryId(torrent.getCategory().getId());
        form.setTitle(torrent.getTitle());
        form.setSubtitle(torrent.getSubtitle());
        form.setDescription(torrent.getDescription());
        form.setAnonymous(torrent.isAnonymous());
        // 获取分类列表
        List<CategoryVO> categories = categoriesService.getAllCategories().stream().map(categoriesService::toVO).toList();
        model.addAttribute("torrent", torrent);
        model.addAttribute("categories", categories);
        model.addAttribute("form", form);
        return "torrents/edit";
    }

    // 处理编辑提交
    @PostMapping("/{id}/edit")
    @SaCheckPermission(value = {Permission.TORRENT_EDIT, Permission.TORRENT_EDIT_OTHER}, mode = SaMode.OR)
    public String handleEdit(
            @PathVariable Long id,
            @ModelAttribute("form") @Valid TorrentEditFormDTO form,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            // 重新加载分类数据
            model.addAttribute("categories", categoriesService.getAllCategories());
            return "redirect:/torrents/" + id + "/edit";
        }
        torrentsService.updateTorrent(id, StpUtil.getLoginIdAsLong(), form.getCategoryId(), form.getTitle(), form.getSubtitle(), form.getDescription());
        return "redirect:/torrents/" + id;
    }
}
