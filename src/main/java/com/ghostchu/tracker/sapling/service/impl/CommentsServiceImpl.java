package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Comments;
import com.ghostchu.tracker.sapling.mapper.CommentsMapper;
import com.ghostchu.tracker.sapling.service.ICommentsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class CommentsServiceImpl extends ServiceImpl<CommentsMapper, Comments> implements ICommentsService {

}
