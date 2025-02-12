package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.ghostchu.tracker.sapling.dto.ConfirmFormDTO;
import com.ghostchu.tracker.sapling.dto.TorrentEditFormDTO;
import com.ghostchu.tracker.sapling.dto.TorrentUploadFormDTO;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.entity.projection.PeerStats;
import com.ghostchu.tracker.sapling.exception.TorrentNotExistsException;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.service.*;
import com.ghostchu.tracker.sapling.util.HtmlSanitizer;
import com.ghostchu.tracker.sapling.util.MsgUtil;
import com.ghostchu.tracker.sapling.vo.*;
import com.google.common.html.HtmlEscapers;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
@SaCheckDisable
public class TorrentsController {
    @Autowired
    private ITorrentsService torrentsService;
    @Autowired
    private IPeersService peersService;
    @Autowired
    private ICategoriesService categoriesService;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private IThanksService thanksService;
    @Autowired
    private ITorrentReviewQueueService torrentReviewQueueService;
    @Autowired
    private ICommentsService commentsService;
    @Autowired
    private ITorrentTagsService torrentTagsService;
    @Autowired
    private ISettingsService settingsService;

    @GetMapping
    @SaCheckPermission(value = {Permission.TORRENT_VIEW})
    public String torrentList(
            @RequestParam(defaultValue = "1") long page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String keyword,
            Model model) {
        // 获取分页数据
        IPage<Torrents> pageResult = torrentsService.getTorrentsByPage(page,
                size,
                keyword,
                StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE),
                StpUtil.hasPermission(Permission.TORRENT_VIEW_DELETED));
        // 准备模型数据
        Map<Long, TorrentPreviewVO> torrentPreviewMap = new HashMap<>();
        for (Torrents torrent : pageResult.getRecords()) {
            long comments = commentsService.getCommentsCount(torrent.getId());
            PeerStats peerStats = peersService.countPeersByTorrent(torrent.getId());
            TorrentPreviewVO previewVO = new TorrentPreviewVO();
            previewVO.setTorrentId(torrent.getId());
            previewVO.setComments(comments);
            previewVO.setSeeds(peerStats.getSeeds());
            previewVO.setLeeches(peerStats.getLeeches());
            torrentPreviewMap.put(torrent.getId(), previewVO);
        }
        model.addAttribute("torrents", pageResult.getRecords().stream().map(torrentsService::toVO).toList());
        model.addAttribute("torrentPreviewMap", torrentPreviewMap);
        model.addAttribute("pagination", pageResult);
        model.addAttribute("keyword", keyword);
        return "torrents";
    }

    @GetMapping("/{id}")
    @SaCheckPermission(value = {Permission.TORRENT_VIEW})
    public String torrentDetail(@PathVariable long id, Model model, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "50") int size) {
        // 获取种子详情
        Torrents torrent = torrentsService.getTorrentById(id);
        if (torrent == null
                || (!torrent.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))  // 处于不可见状态
                || (torrent.isDeleted() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_DELETED))) { // 已被删除
            throw new TorrentNotExistsException(null, id, "种子不存在或已被删除");
        }
        // 准备模型数据
        model.addAttribute("torrent", torrentsService.toVO(torrent));
        var recentThanks = thanksService.getThanksByPageByTorrent(id, 1, 20);
        model.addAttribute("totalThanks", recentThanks.getTotal());
        List<ThanksVO> thanksVOList = recentThanks.getRecords().stream().map(thanksService::toVO).filter(Objects::nonNull).toList();
        model.addAttribute("thanks", thanksVOList);
        model.addAttribute("thankedTorrent", thanksService.isUserThankedTorrent(StpUtil.getLoginIdAsLong(), id));
        var comments = commentsService.getComments(id, page, size);
        IPage<CommentsVO> commentsPaged = new PageDTO<>(page, size, comments.getTotal(), comments.searchCount());
        commentsPaged.setRecords(comments.getRecords().stream().map(c -> commentsService.toVO(c, false)).toList());
        model.addAttribute("comments", commentsPaged);
        var torrentTags = torrentTagsService.getTorrentTags(torrent.getId()).stream().map(t -> torrentTagsService.toVO(t)).toList();
        Map<String, List<TorrentTagsVO>> groupedTags = torrentTags.stream()
                .collect(Collectors.groupingBy(t -> t.getTag().getNamespace()));
        model.addAttribute("torrentTags", groupedTags);
        var peers = peersService.fetchPeers(0, torrent.getId(), Short.MAX_VALUE, false, null);
        IPage<PeersVO> peersVOIPage = new Page<>(peers.getCurrent(), peers.getSize(), peers.getTotal(), peers.searchCount());
        peersVOIPage.setRecords(peers.getRecords().stream().map(peersService::toVO).toList());
        model.addAttribute("peers", peersVOIPage);
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
                    HtmlSanitizer.sanitize(form.getDescription()), form.isAnonymous(), true);
        } else {
            newTorrent = torrentsService.createTorrent(StpUtil.getLoginIdAsLong(), file,
                    form.getCategoryId(), form.getTitle(), form.getSubtitle(),
                    HtmlSanitizer.sanitize(form.getDescription()), form.isAnonymous(), false);
            torrentReviewQueueService.queueTorrent(newTorrent.getId());
        }
        torrentTagsService.applyTagString(newTorrent.getId(), form.getTags());
        return "redirect:/torrents/" + newTorrent.getId();
    }

    @GetMapping("/{id}/download")
    @SaCheckPermission(value = {Permission.TORRENT_VIEW, Permission.TORRENT_DOWNLOAD})
    public ResponseEntity<InputStreamResource> downloadTorrent(@PathVariable long id) throws IOException {
        Torrents torrent = torrentsService.getTorrentById(id);
        if (torrent == null
                || (!torrent.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))  // 处于不可见状态
                || (torrent.isDeleted() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_DELETED))) { // 已被删除
            throw new TorrentNotExistsException(null, id, "种子不存在或已被删除");
        }
        String siteName = settingsService.getValue(Setting.SITE_NAME).orElseThrow();
        Users user = usersService.getUserById(StpUtil.getLoginIdAsLong());
        String fileName = "[" + siteName + "]" + torrent.getTitle() + ".torrent";
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8" + fileName)
                .body(new InputStreamResource(new ByteArrayInputStream(torrentsService.downloadTorrentForUser(torrent, user))));
    }

    // 显示编辑页面
    @GetMapping("/{id}/edit")
    @SaCheckPermission(value = {Permission.TORRENT_EDIT, Permission.TORRENT_EDIT_OTHER}, mode = SaMode.OR)
    public String showEditForm(@PathVariable Long id, Model model) {
        // 获取种子详细信息（示例代码）
        TorrentsVO torrent = torrentsService.toVO(torrentsService.getTorrentById(id));
        if (torrent == null
                || (!torrent.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))  // 处于不可见状态
                || (torrent.isDeleted() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_DELETED))) { // 已被删除
            throw new TorrentNotExistsException(null, id, "种子不存在或已被删除");
        }
        if (StpUtil.getLoginIdAsLong() != torrent.getOwner().getId()) {
            return "redirect:/torrents/" + id; // 权限不足
        }
        var form = new TorrentEditFormDTO();
        form.setCategoryId(torrent.getCategory().getId());
        form.setTitle(torrent.getTitle());
        form.setSubtitle(torrent.getSubtitle());
        form.setDescription(torrent.getDescription());
        form.setAnonymous(torrent.isAnonymous());
        form.setTags(torrentTagsService.createTagString(torrentTagsService.getTorrentTags(torrent.getId())));
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
        torrentsService.updateTorrent(id, StpUtil.getLoginIdAsLong(), form.getCategoryId(), form.getTitle(), form.getSubtitle(), HtmlSanitizer.sanitize(form.getDescription()));
        torrentTagsService.applyTagString(id, form.getTags());
        return "redirect:/torrents/" + id;
    }

    @GetMapping("/{id}/delete")
    @SaCheckPermission(value = {Permission.TORRENT_DELETE, Permission.TORRENT_DELETE_OTHER}, mode = SaMode.OR)
    public String deleteTorrent(@PathVariable Long id, Model model) {
        Torrents torrents = torrentsService.getTorrentById(id);
        if (torrents == null || (!torrents.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))) {
            throw new TorrentNotExistsException(null, id, "种子不存在或已被删除");
        }
        if (StpUtil.getLoginIdAsLong() != torrents.getOwner()) {
            StpUtil.checkPermission(Permission.TORRENT_DELETE_OTHER);
        }
        var template = """
                您正在删除种子: <b>{}</b>。
                <br>
                确定要继续吗？
                """;
        ConfirmFormDTO form = new ConfirmFormDTO();
        form.setActionUrl("/torrents/" + id + "/delete");
        form.setTitle("删除种子");
        form.setHeaderClass("bg-danger text-white");
        form.setDescription(MsgUtil.fillArgs(template, HtmlEscapers.htmlEscaper().escape(torrents.getTitle())));
        form.setConfirmButtonText("删除");
        model.addAttribute("form", form);
        return "confirm";
    }

    @PostMapping("/{id}/delete")
    @SaCheckPermission(value = {Permission.TORRENT_DELETE, Permission.TORRENT_DELETE_OTHER}, mode = SaMode.OR)
    public String deleteTorrent(@PathVariable Long id) {
        Torrents torrents = torrentsService.getTorrentById(id);
        if (torrents == null || (!torrents.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))) {
            throw new TorrentNotExistsException(null, id, "种子不存在或已被删除");
        }
        if (StpUtil.getLoginIdAsLong() != torrents.getOwner()) {
            StpUtil.checkPermission(Permission.TORRENT_DELETE_OTHER);
        }

        torrentsService.deleteTorrent(id, StpUtil.getLoginIdAsLong());
        return "redirect:/torrents";
    }

    @GetMapping("/{id}/undelete")
    @SaCheckPermission(value = {Permission.TORRENT_VIEW_DELETED, Permission.TORRENT_UNDELETE})
    public String unDeleteTorrent(@PathVariable Long id, Model model) {
        Torrents torrents = torrentsService.getTorrentById(id);
        if (torrents == null || (!torrents.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))) {
            throw new TorrentNotExistsException(null, id, "种子不存在");
        }
        if (!torrents.isDeleted()) {
            throw new IllegalStateException("种子未被删除，不能执行反删除");
        }
        Users users = usersService.getUserById(torrents.getDeletedBy());
        var template = """
                您正在反删除种子: <b>{}</b>。
                <br>
                该种子的删除时间：{}<br>
                删除操作人：{}<br>
                <br>
                确定要继续吗？
                """;
        ConfirmFormDTO form = new ConfirmFormDTO();
        form.setActionUrl("/torrents/" + id + "/undelete");
        form.setTitle("种子反删除");
        form.setConfirmButtonText("还原");
        form.setDescription(MsgUtil.fillArgs(template, HtmlEscapers.htmlEscaper().escape(torrents.getTitle()), torrents.getDeletedAt().toString(), users.getName()));
        model.addAttribute("form", form);
        return "confirm";
    }

    @PostMapping("/{id}/undelete")
    @SaCheckPermission(value = {Permission.TORRENT_VIEW_DELETED, Permission.TORRENT_UNDELETE})
    public String unDeleteTorrent(@PathVariable Long id) {
        Torrents torrents = torrentsService.getTorrentById(id);
        if (torrents == null || (!torrents.isVisible() && !StpUtil.hasPermission(Permission.TORRENT_VIEW_INVISIBLE))) {
            throw new TorrentNotExistsException(null, id, "种子不存在");
        }
        if (torrents.getDeletedAt() == null) {
            throw new IllegalStateException("种子未被删除，不能执行反删除");
        }
        torrentsService.unDeleteTorrent(id);
        return "redirect:/torrents/" + id;
    }
}
