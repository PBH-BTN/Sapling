package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Categories;
import com.ghostchu.tracker.sapling.mapper.CategoriesMapper;
import com.ghostchu.tracker.sapling.service.ICategoriesService;
import com.ghostchu.tracker.sapling.vo.CategoryVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
public class CategoriesServiceImpl extends MPJBaseServiceImpl<CategoriesMapper, Categories> implements ICategoriesService {

    @Override
    @Cacheable(value = "categories", key = "'all'")
    public List<Categories> getAllCategories() {
        return baseMapper.selectList(null);
    }

    @Override
    @Cacheable(value = "users", key = "'id:' + #id")
    public Categories getCategoryById(long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public CategoryVO toVO(Categories categoryById) {
        CategoryVO vo = new CategoryVO();
        vo.setId(categoryById.getId());
        vo.setName(categoryById.getName());
        vo.setIcon(categoryById.getIcon());
        vo.setColor(categoryById.getColor());
        return vo;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories", key = "'all'"),
            @CacheEvict(value = "categories", key = "'id:' + #result.getId()")
    })
    public Categories createCategory(String name, String icon, String color) {
        Categories category = new Categories();
        category.setName(name);
        category.setIcon(icon);
        category.setColor(color);
        save(category);
        return category;
    }
}
