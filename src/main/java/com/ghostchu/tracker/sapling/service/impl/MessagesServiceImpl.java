package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Messages;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.mapper.MessagesMapper;
import com.ghostchu.tracker.sapling.mapper.UsersMapper;
import com.ghostchu.tracker.sapling.service.IMessagesService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    @Autowired
    private UsersMapper usersMapper;

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
        int batchSize = 1000;
        Long lastId = 0L;
        List<Long> userIds;
        Messages message = new Messages();
        message.setSender(sender);
        message.setTitle(title);
        message.setDescription(description);
        do {
            // 查询本批次的 ID 列表
            userIds = usersMapper.selectObjs(
                            new LambdaQueryWrapper<Users>()
                                    .select(Users::getId)      // 只查询 ID 字段
                                    .gt(Users::getId, lastId)  // 从上一批的最后一个 ID 开始
                                    .orderByAsc(Users::getId)  // 按 ID 升序
                                    .last("LIMIT " + batchSize) // 直接拼接 LIMIT（注意 SQL 注入风险）
                    ).stream()
                    .map(id -> (Long) id)
                    .toList();
            if (!userIds.isEmpty()) {
                // 处理本批次的 ID（例如发送站内信）
                for (Long userId : userIds) {
                    message.setId(null);
                    message.setOwner(userId);
                    baseMapper.insert(message);
                }
                // 更新下一批的起始 ID
                lastId = userIds.getLast();
            }
        } while (!userIds.isEmpty());
    }

    public void markMessageRead(long userId, long messageId) {
        baseMapper.update(null, new QueryWrapper<Messages>()
                .eq("owner", userId)
                .eq("id", messageId)
                .isNull("read_at"));
    }
}
