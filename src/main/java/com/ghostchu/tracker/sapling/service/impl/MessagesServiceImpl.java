package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Messages;
import com.ghostchu.tracker.sapling.mapper.MessagesMapper;
import com.ghostchu.tracker.sapling.service.IMessagesService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class MessagesServiceImpl extends MPJBaseServiceImpl<MessagesMapper, Messages> implements IMessagesService {
    @Autowired
    private IUsersService usersService;

    public IPage<Messages> getMessageByUser(long userId, int page, int size, boolean includeUnread) {
        IPage<Messages> p = new Page<>(page, size);
        return baseMapper.selectPage(p, new QueryWrapper<Messages>()
                .eq("owner", userId)
                .isNull(!includeUnread, "read_at")
                .orderByDesc("id"));
    }

    public Messages sendMessage(long sender, long receiver, String title, String description) {
        Messages message = new Messages();
        message.setOwner(receiver);
        message.setSender(sender);
        message.setTitle(title);
        message.setDescription(description);
        baseMapper.insert(message);
        return message;
    }

    @Transactional
    public void sendAllMessage(long sender, String title, String description) {
    }

    public void markMessageRead(long userId, long messageId) {
        baseMapper.update(null, new QueryWrapper<Messages>()
                .eq("owner", userId)
                .eq("id", messageId)
                .isNull("read_at"));
    }
}
