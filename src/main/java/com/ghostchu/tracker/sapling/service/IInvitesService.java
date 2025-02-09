package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Invites;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.vo.InvitesVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IInvitesService extends MPJBaseService<Invites> {

    Invites getInviteByCode(String inviteCode);

    boolean markInviteAsUsed(Invites invite, Users users);

    IPage<Invites> listInvites(int page, int size, String search);

    InvitesVO toVO(Invites invites);
}
