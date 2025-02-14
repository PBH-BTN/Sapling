package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Categories;
import com.ghostchu.tracker.sapling.mapper.CategoriesMapper;
import com.ghostchu.tracker.sapling.service.ICategoriesService;
import com.ghostchu.tracker.sapling.vo.CategoryVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.apache.commons.lang3.NotImplementedException;
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
    @Cacheable(value = "categories", key = "'id:' + #id")
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

    @Override
    public void deleteCategory(Long id) {
        throw new NotImplementedException("分类删除功能未完成，需要关联删除种子，但这会引起循环依赖，仍在研究");
    }

    @Override
    public void saveOrUpdateCategory(Categories category) {
        saveOrUpdate(category);
    }

    @Override
    public IPage<Categories> pageCategories(int page, int size, String search) {
        QueryWrapper<Categories> wrapper = new QueryWrapper<>();
        wrapper = wrapper.like(search != null, "name", search);
        IPage<Categories> categoriesIPage = new Page<>(page, size);
        return page(categoriesIPage, wrapper);
    }
}
