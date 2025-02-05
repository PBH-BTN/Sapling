package com.ghostchu.tracker.sapling.service;

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
}
