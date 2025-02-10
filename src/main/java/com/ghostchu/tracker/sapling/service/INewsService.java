package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.News;
import com.ghostchu.tracker.sapling.vo.NewsVO;
import com.github.yulichang.base.MPJBaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface INewsService extends MPJBaseService<News> {

    List<News> getActiveNews();

    NewsVO toVO(News news);

    News saveNews(News newsVO);

    News deleteNews(Long id);

    News getNews(Long id);

    IPage<News> pageNews(int page, int size);
}
