package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Invites;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.mapper.InvitesMapper;
import com.ghostchu.tracker.sapling.service.IInvitesService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.InvitesVO;
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
public class InvitesServiceImpl extends MPJBaseServiceImpl<InvitesMapper, Invites> implements IInvitesService {
    @Autowired
    private IUsersService usersService;

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

    @Override
    public IPage<Invites> listInvites(int page, int size, String search) {
        IPage<Invites> p = new Page<>(page, size);
        return page(p, new QueryWrapper<Invites>()
                .orderByDesc("id"));
    }

    @Override
    public InvitesVO toVO(Invites invites) {
        InvitesVO vo = new InvitesVO();
        vo.setId(invites.getId());
        vo.setInviteBy(usersService.toVO(usersService.getById(invites.getInviteBy())));
        vo.setInviteEmail(invites.getInviteEmail());
        vo.setInviteUsername(invites.getInviteUsername());
        vo.setExpireAt(invites.getExpireAt());
        vo.setInvitedUser(invites.getInvitedUser() != null ? usersService.toVO(usersService.getById(invites.getInvitedUser())) : null);
        vo.setConsumeAt(invites.getConsumeAt());
        vo.setInviteCode(invites.getInviteCode());
        return vo;
    }
}
