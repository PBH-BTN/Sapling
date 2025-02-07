package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Tags;
import com.ghostchu.tracker.sapling.vo.TagsVO;
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
public interface ITagsService extends MPJBaseService<Tags> {

    Tags getTags(long id);

    List<Tags> getTagsByNamespace(String namespace);

    TagsVO toVO(Tags tags);
}
