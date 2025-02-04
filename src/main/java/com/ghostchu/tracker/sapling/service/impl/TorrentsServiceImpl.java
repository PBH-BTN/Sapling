package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghostchu.tracker.sapling.entity.Torrents;
import com.ghostchu.tracker.sapling.mapper.TorrentsMapper;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
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
public class TorrentsServiceImpl extends ServiceImpl<TorrentsMapper, Torrents> implements ITorrentsService {
@Override
    public IPage<Torrents> getTorrentsByPage(long page, int size) {
        IPage<Torrents> iPage = new Page<>(page, size);
        return baseMapper.selectPage(iPage, null);
    }

}
