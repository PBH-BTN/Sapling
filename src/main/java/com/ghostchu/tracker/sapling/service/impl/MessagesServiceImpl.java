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
import com.ghostchu.tracker.sapling.vo.MessagesVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 服务实现类
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


    public Messages sendMessage(long sender, long receiver, String title, String description) {
        Messages message = new Messages();
        message.setOwner(receiver);
        message.setSender(sender);
        message.setTitle(title);
        message.setDescription(description);
        baseMapper.insert(message);
        return message;
    }

    @Override
    public void markAsRead(long messageId) {
        Messages messages = getById(messageId);
        if (messages == null) {
            return;
        }
        messages.setReadAt(OffsetDateTime.now());
        updateById(messages);
    }

    @Override
    public IPage<Messages> pageMessages(int page, int size, Long userId, boolean includeRead, boolean includeDeleted) {
        IPage<Messages> pa = new Page<>(page, size);
        QueryWrapper<Messages> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper
                .eq(userId != null, "owner", userId)
                .isNull(!includeDeleted, "deleted_at")
                .isNull(!includeRead, "read_at")
                .orderByDesc("id");
        return page(pa, queryWrapper);
    }

    @Override
    public MessagesVO toVO(Messages messages) {
        MessagesVO messagesVO = new MessagesVO();
        messagesVO.setId(messages.getId());
        messagesVO.setOwner(usersService.toVO(usersService.getById(messages.getOwner())));
        messagesVO.setSender(usersService.toVO(usersService.getById(messages.getSender())));
        messagesVO.setTitle(messages.getTitle());
        messagesVO.setDescription(messages.getDescription());
        messagesVO.setCreatedAt(messages.getCreatedAt());
        messagesVO.setReadAt(messages.getReadAt());
        messagesVO.setDeletedAt(messages.getDeletedAt());
        return messagesVO;
    }

    @Override
    public Messages getMessage(Long id) {
        return getById(id);
    }

    @Override
    public void markAsDeleted(Long id) {
        Messages messages = getById(id);
        if (messages == null) {
            return;
        }
        messages.setDeletedAt(OffsetDateTime.now());
        updateById(messages);
    }

    @Override
    @Transactional
    public void sendMessage(String mode, long sender, List<Long> receivers, String title, String content) {
        switch (mode) {
            case "single" -> {
                for (long receiver : receivers) {
                    Messages baseMessage = new Messages();
                    baseMessage.setSender(sender);
                    baseMessage.setTitle(title);
                    baseMessage.setDescription(content);
                    baseMessage.setCreatedAt(OffsetDateTime.now());
                    baseMessage.setOwner(receiver);
                    save(baseMessage);
                }
            }
            case "batch" -> {
                int page = 1;
                int size = 1000;
                IPage<Users> usersIPage = usersMapper.selectPage(new Page<>(page, size), new LambdaQueryWrapper<Users>().select(Users::getId));
                do {
                    List<Messages> sendInBatch = new ArrayList<>(size);
                    for (Users record : usersIPage.getRecords()) {
                        Messages baseMessage = new Messages();
                        baseMessage.setSender(sender);
                        baseMessage.setTitle(title);
                        baseMessage.setDescription(content);
                        baseMessage.setCreatedAt(OffsetDateTime.now());
                        baseMessage.setOwner(record.getId());
                        sendInBatch.add(baseMessage);
                    }
                    if (!sendInBatch.isEmpty()) {
                        saveBatch(sendInBatch);
                    }
                    page++;
                } while (page <= usersIPage.getPages());
            }
            default -> throw new IllegalArgumentException("无效的站内信发送模式：" + mode);
        }
    }
}
