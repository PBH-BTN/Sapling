package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ghostchu.tracker.sapling.entity.Invites;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.mapper.InvitesMapper;
import com.ghostchu.tracker.sapling.service.IInvitesService;
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
public class InvitesServiceImpl extends MPJBaseServiceImpl<InvitesMapper, Invites> implements IInvitesService {

    @Override
    public Invites getInviteByCode(String inviteCode) {
        return getOne(new QueryWrapper<Invites>()
                .eq("invite_code", inviteCode));
    }

    @Override
    public boolean markInviteAsUsed(Invites invite, Users users) {
        return update(new UpdateWrapper<Invites>()
                .eq("id", invite.getId())
                .isNull("consume_at")
                .isNull("invited_user")
                .set("consume_at", OffsetDateTime.now())
                .set("invited_user", users.getId())
        );
    }
}
