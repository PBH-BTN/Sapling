package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Messages;
import com.ghostchu.tracker.sapling.vo.MessagesVO;
import com.github.yulichang.base.MPJBaseService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IMessagesService extends MPJBaseService<Messages> {

    void markAsRead(long messageId);

    IPage<Messages> pageMessages(int page, int size, Long userId, boolean includeRead, boolean includeDeleted);

    MessagesVO toVO(Messages messages);

    Messages getMessage(Long id);

    void markAsDeleted(Long id);

    void sendMessage(String mode, long sender, List<Long> receivers, String title, String content);
}
