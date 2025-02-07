package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Tags;
import com.ghostchu.tracker.sapling.mapper.TagsMapper;
import com.ghostchu.tracker.sapling.service.ITagsService;
import com.ghostchu.tracker.sapling.vo.TagsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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
public class TagsServiceImpl extends MPJBaseServiceImpl<TagsMapper, Tags> implements ITagsService {

    @Override
    @Cacheable(value = "tags", key = "'id:' + #id")
    public Tags getTags(long id) {
        return getById(id);
    }

    @Override
    @Cacheable(value = "tags", key = "'namespace:' + #namespace")
    public List<Tags> getTagsByNamespace(String namespace) {
        return list(query().eq("namespace", namespace));
    }

    @Override
    public TagsVO toVO(Tags tags) {
        TagsVO tagsVO = new TagsVO();
        tagsVO.setId(tags.getId());
        tagsVO.setNamespace(tags.getNamespace());
        tagsVO.setTagname(tags.getTagname());
        return tagsVO;
    }
}
