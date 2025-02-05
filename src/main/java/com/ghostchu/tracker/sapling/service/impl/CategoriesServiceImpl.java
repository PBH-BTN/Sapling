package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Categories;
import com.ghostchu.tracker.sapling.mapper.CategoriesMapper;
import com.ghostchu.tracker.sapling.service.ICategoriesService;
import com.github.yulichang.base.MPJBaseServiceImpl;
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
    public List<Categories> getAllCategories() {
        return baseMapper.selectList(null);
    }

    @Override
    public Categories getCategoryById(long categoryId) {
        return baseMapper.selectById(categoryId);
    }
}
