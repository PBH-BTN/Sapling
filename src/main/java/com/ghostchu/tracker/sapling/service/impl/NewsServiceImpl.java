package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.News;
import com.ghostchu.tracker.sapling.mapper.NewsMapper;
import com.ghostchu.tracker.sapling.service.INewsService;
import com.github.yulichang.base.MPJBaseServiceImpl;
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
public class NewsServiceImpl extends MPJBaseServiceImpl<NewsMapper, News> implements INewsService {

}
