package com.ghostchu.tracker.sapling.controller.admin.site;

import cn.dev33.satoken.annotation.SaCheckDisable;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.dto.NewsFormDTO;
import com.ghostchu.tracker.sapling.entity.News;
import com.ghostchu.tracker.sapling.gvar.Permission;
import com.ghostchu.tracker.sapling.service.INewsService;
import com.ghostchu.tracker.sapling.vo.NewsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@Controller
@RequestMapping("/admin/site/news")
@SaCheckPermission(Permission.ADMIN_SITE_NEWS)
@SaCheckDisable
public class AdminSiteNewsController {
    @Autowired
    private INewsService newsService;

    @GetMapping
    public String newsList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        IPage<News> newsPage = newsService.pageNews(page, size);
        IPage<NewsVO> newsVOIPage = new Page<>(newsPage.getCurrent(), newsPage.getSize(), newsPage.getTotal(), newsPage.searchCount());
        newsVOIPage.setRecords(newsPage.getRecords().stream().map(newsService::toVO).toList());
        model.addAttribute("newsForm", new NewsFormDTO());
        model.addAttribute("news", newsVOIPage);
        return "admin/site/news";
    }

    @PostMapping
    public String saveNews(@ModelAttribute NewsFormDTO form) {
        News news = newsService.getNews(form.getId());
        if (news == null) {
            news = new News();
            news.setCreatedAt(OffsetDateTime.now());
            news.setOwner(StpUtil.getLoginIdAsLong());
        }
        news.setTitle(form.getTitle());
        news.setDescription(form.getDescription());
        news.setExpiredAt(OffsetDateTime.now().plusYears(100));
        newsService.saveNews(news);
        return "redirect:/admin/site/news";
    }

    @DeleteMapping("/{id}")
    public String deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
        return "redirect:/admin/site/news";
    }

    @GetMapping("/{id}")
    @ResponseBody
    public NewsVO getNews(@PathVariable Long id) {
        return newsService.toVO(newsService.getNews(id));
    }
}