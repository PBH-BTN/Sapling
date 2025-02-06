package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Pages;
import com.ghostchu.tracker.sapling.mapper.PagesMapper;
import com.ghostchu.tracker.sapling.service.IPagesService;
import com.ghostchu.tracker.sapling.vo.PagesVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class PagesServiceImpl extends MPJBaseServiceImpl<PagesMapper, Pages> implements IPagesService {
    @Autowired
    private HtmlRenderer markdownHtmlRenderer;
    @Autowired
    private Parser markdownHtmlParser;

    @Override
    @Cacheable(value = "pages", key = "'slug:'+#slug")
    public Pages getPages(String slug) {
        return getOne(new QueryWrapper<Pages>().eq("slug", slug));
    }

    @Override
    public PagesVO toVO(Pages pages) {
        PagesVO pagesVO = new PagesVO();
        pagesVO.setId(pages.getId());
        pagesVO.setTitle(pages.getTitle());
        pagesVO.setHtmlContent(renderHtml(pages));
        pagesVO.setSlug(pages.getSlug());
        pagesVO.setCreatedAt(pages.getCreatedAt());
        pagesVO.setEditedAt(pages.getEditedAt());
        return pagesVO;
    }

    @Override
    public String renderHtml(Pages pages) {
        return switch (pages.getType()) {
            case "markdown" -> markdownHtmlRenderer.render(markdownHtmlParser.parse(pages.getContent()));
            case "html" -> pages.getContent();
            default -> throw new IllegalStateException("未知的页面解析器类型：" + pages.getType());
        };
    }
}
