package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Thanks;
import com.ghostchu.tracker.sapling.vo.ThanksVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IThanksService extends MPJBaseService<Thanks> {

    ThanksVO toVO(Thanks thanks);

    boolean isUserThankedTorrent(long userId, long torrentId);

    IPage<Thanks> getThanksByPageByTorrent(long torrent, int page, int size);

    boolean createThanks(long loginIdAsLong, long id);
}
