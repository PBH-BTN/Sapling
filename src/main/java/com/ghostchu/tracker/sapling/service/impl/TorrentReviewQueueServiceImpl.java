package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.TorrentReviewQueue;
import com.ghostchu.tracker.sapling.mapper.TorrentReviewQueueMapper;
import com.ghostchu.tracker.sapling.service.ITorrentReviewQueueService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class TorrentReviewQueueServiceImpl extends MPJBaseServiceImpl<TorrentReviewQueueMapper, TorrentReviewQueue> implements ITorrentReviewQueueService {

    @Override
    public void queueTorrent(long torrentId) {
        TorrentReviewQueue queue = new TorrentReviewQueue();
        queue.setTorrent(torrentId);
        queue.setSubmitAt(OffsetDateTime.now());
        save(queue);
    }
}
