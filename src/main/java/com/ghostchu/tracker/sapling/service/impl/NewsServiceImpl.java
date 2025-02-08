package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.News;
import com.ghostchu.tracker.sapling.mapper.NewsMapper;
import com.ghostchu.tracker.sapling.service.INewsService;
import com.ghostchu.tracker.sapling.vo.NewsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class NewsServiceImpl extends MPJBaseServiceImpl<NewsMapper, News> implements INewsService {

    @Override
    public List<News> getActiveNews() {
        return list(new QueryWrapper<News>()
                .isNull("deleted_at")
                .ge("expired_at", OffsetDateTime.now())
                .orderByDesc("created_at"));
    }

    @Override
    public NewsVO toVO(News news) {
        NewsVO vo = new NewsVO();
        vo.setId(news.getId());
        vo.setTitle(news.getTitle());
        vo.setDescription(news.getDescription());
        vo.setCreatedAt(news.getCreatedAt());
        return vo;
    }
}
