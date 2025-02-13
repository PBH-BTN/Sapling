package com.ghostchu.tracker.sapling.controller.admin.torrents;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.TagsFormDTO;
import com.ghostchu.tracker.sapling.entity.Tags;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.ITagsService;
import com.ghostchu.tracker.sapling.service.ITorrentTagsService;
import com.ghostchu.tracker.sapling.vo.TagsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/torrents/tags")
@SaCheckPermission(Permission.ADMIN_TORRENTS_TAGS)
@SaCheckDisable
public class AdminTorrentsTagsController {
    @Autowired
    private ITagsService tagService;
    @Autowired
    private ITorrentTagsService torrentTagsService;

    @GetMapping
    public String tagList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {
        IPage<Tags> tags = tagService.fetchTags(page, size, search);
        IPage<TagsVO> tagPage = tags.convert(tagService::toVO);
        model.addAttribute("tags", tagPage);
        return "admin/torrents/tags";
    }

    @PostMapping
    public String saveTag(@ModelAttribute TagsFormDTO tag) {
        Tags tags = null;
        if (tag.getId() != null)
            tags = tagService.getTags(tag.getId());
        if (tags == null) {
            tags = tagService.getTagByString(tag.getNamespace() + ":" + tag.getTagname());
        }
        if (tags == null) {
            tags = new Tags();
            tags.setNamespace(tag.getNamespace());
            tags.setTagname(tag.getTagname());
        }
        tagService.saveTags(tags);
        return "redirect:/admin/torrents/tags";
    }

    @DeleteMapping("/{id}")
    @Transactional
    public String deleteTag(@PathVariable Long id) {
        torrentTagsService.removeSpecificTagsFromAllTorrents(id);
        tagService.removeTagsById(id);
        return "redirect:/admin/torrents/tags";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public TagsVO getTag(@PathVariable Long id) {
        return tagService.toVO(tagService.getTags(id));
    }
}