package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Thanks;
import com.ghostchu.tracker.sapling.mapper.ThanksMapper;
import com.ghostchu.tracker.sapling.service.IThanksService;
import com.ghostchu.tracker.sapling.service.ITorrentsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.ThanksVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ThanksServiceImpl extends MPJBaseServiceImpl<ThanksMapper, Thanks> implements IThanksService {
    @Autowired
    private ITorrentsService torrentsService;
    @Autowired
    private IUsersService usersService;

    @Override
    public ThanksVO toVO(Thanks thanks) {
        var users = usersService.getUserById(thanks.getOwner());
        var torrents = torrentsService.getTorrentById(thanks.getTorrent());
        if (users == null || torrents == null) return null;
        ThanksVO thanksVO = new ThanksVO();
        thanksVO.setId(thanks.getId() == null ? 0 : thanks.getId());
        thanksVO.setOwner(usersService.toVO(users));
        thanksVO.setTorrent(torrentsService.toVO(torrents));
        thanksVO.setCreateAt(thanks.getCreateAt());
        return thanksVO;
    }

    @Override
    public boolean isUserThankedTorrent(long userId, long torrentId) {
        return baseMapper.selectCount(new QueryWrapper<Thanks>().eq("owner", userId).eq("torrent", torrentId)) > 0;
    }

    @Override
    public IPage<Thanks> getThanksByPageByTorrent(long torrent, int page, int size) {
        IPage<Thanks> iPage = new Page<>(page, size);
        return baseMapper.selectPage(iPage, new QueryWrapper<Thanks>().eq("torrent", torrent).orderByDesc("create_at"));
    }

    @Override
    public boolean createThanks(long userId, long id) {
        if (isUserThankedTorrent(userId, id)) {
            return false;
        }
        Thanks thanks = new Thanks();
        thanks.setOwner(userId);
        thanks.setTorrent(id);
        thanks.setCreateAt(OffsetDateTime.now());
        return save(thanks);
    }
}
