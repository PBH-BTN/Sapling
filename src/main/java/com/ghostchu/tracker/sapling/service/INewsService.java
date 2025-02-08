package com.ghostchu.tracker.sapling.service;

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
}
