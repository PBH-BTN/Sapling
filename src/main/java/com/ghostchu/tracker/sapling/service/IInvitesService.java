package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Invites;
import com.ghostchu.tracker.sapling.entity.Users;
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
}
