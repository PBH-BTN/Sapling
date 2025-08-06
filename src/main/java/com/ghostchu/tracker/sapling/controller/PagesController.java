package com.ghostchu.tracker.sapling.controller;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.ghostchu.tracker.sapling.entity.Pages;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.IPagesService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Controller
@RequestMapping("/pages")
@SaCheckDisable
public class PagesController {

    @Autowired
    private IPagesService pagesService;

    @GetMapping("/{slug}")
    @SaCheckPermission(Permission.PAGES_VIEW)
    public String pages(@PathVariable String slug, Model model, HttpServletResponse response) {
        Pages pages = pagesService.getPages(slug);
        if (pages == null) {
            response.setStatus(404);
            model.addAttribute("err", "请求的页面不存在");
            return "error";
        }
        model.addAttribute("pages", pagesService.toVO(pages, true));
        return "pages/html";
    }
}
