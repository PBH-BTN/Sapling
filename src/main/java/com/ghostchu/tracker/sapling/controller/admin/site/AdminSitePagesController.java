package com.ghostchu.tracker.sapling.controller.admin.site;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.dto.PagesFormDTO;
import com.ghostchu.tracker.sapling.entity.Pages;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IPagesService;
import com.ghostchu.tracker.sapling.vo.PagesVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/admin/site/pages")
@SaCheckPermission(Permission.ADMIN_SITE_PAGES)
@SaCheckDisable
public class AdminSitePagesController {
    @Autowired
    private IPagesService pageService;

    @GetMapping
    public String pageList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search,
            Model model) {

        IPage<Pages> pageList = pageService.pagePages(page, size, search);
        model.addAttribute("pages", pageList);
        return "admin/site/pages";
    }

    @PostMapping
    public String savePage(@ModelAttribute PagesFormDTO pageVO) {
        Pages page = pageService.getById(pageVO.getId());
        if (page == null) {
            page = new Pages();
            page.setCreatedAt(OffsetDateTime.now());
        } else {
            page.setEditedAt(OffsetDateTime.now());
        }
        page.setTitle(pageVO.getTitle());
        page.setContent(pageVO.getContent());
        page.setSlug(pageVO.getSlug());
        page.setType(pageVO.getType());
        pageService.savePage(page);
        return "redirect:/admin/site/pages";
    }

    @DeleteMapping("/{id}")
    public String deletePage(@PathVariable Long id) {
        pageService.deletePage(id);
        return "redirect:/admin/site/pages";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public PagesVO getPage(@PathVariable Long id) {
        return pageService.toVO(pageService.getById(id), false);
    }
}
