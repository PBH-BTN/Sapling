package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Categories;
import com.ghostchu.tracker.sapling.vo.CategoryVO;
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
public interface ICategoriesService extends MPJBaseService<Categories> {

    List<Categories> getAllCategories();

    Categories getCategoryById(long category);

    CategoryVO toVO(Categories categoryById);

    Categories createCategory(String name, String icon, String color);

    void deleteCategory(Long id);

    void saveOrUpdateCategory(Categories category);

    IPage<Categories> pageCategories(int page, int size, String search);
}
