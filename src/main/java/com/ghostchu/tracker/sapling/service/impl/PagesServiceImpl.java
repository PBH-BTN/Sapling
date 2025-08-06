package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Pages;
import com.ghostchu.tracker.sapling.mapper.PagesMapper;
import com.ghostchu.tracker.sapling.service.IPagesService;
import com.ghostchu.tracker.sapling.vo.PagesVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
    public PagesVO toVO(Pages pages, boolean render) {
        PagesVO pagesVO = new PagesVO();
        pagesVO.setId(pages.getId() == null ? 0 : pages.getId());
        pagesVO.setTitle(pages.getTitle());
        pagesVO.setContent(render ? renderHtml(pages) : pages.getContent());
        pagesVO.setSlug(pages.getSlug());
        pagesVO.setType(pages.getType());
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

    @Override
    public IPage<Pages> pagePages(int page, int size, String search) {
        IPage<Pages> pager = new Page<>(page, size);
        QueryWrapper<Pages> query = new QueryWrapper<>();
        if (StringUtils.isNotBlank(search)) {
            query = query.like("title", "%" + search + "%");
        }
        return page(pager, query);
    }

    @Override
    @CacheEvict(value = "pages", key = "'slug:'+#pages.slug")
    public void savePage(Pages pages) {
        var exists = getOne(new QueryWrapper<Pages>().eq("slug", pages.getSlug()));
        if (exists != null && !Objects.equals(pages.getId(), exists.getId())) {
            throw new IllegalArgumentException("Slug 已存在且不可重复");
        }
        saveOrUpdate(pages);
    }

    @Override
    public void deletePage(Long id) {
        removeById(id);
    }
}
