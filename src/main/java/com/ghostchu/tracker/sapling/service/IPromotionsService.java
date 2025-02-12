package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Promotions;
import com.ghostchu.tracker.sapling.vo.PromotionsVO;
import com.github.yulichang.base.MPJBaseService;
import org.springframework.cache.annotation.Cacheable;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IPromotionsService extends MPJBaseService<Promotions> {

    @Cacheable(value = "promotions", key = "#id")
    Promotions getPromotionsById(Long id);

    @Cacheable(value = "promotions", key = "#id")
    Promotions getPromotionsByIdAndPromotionStatus(Long id);

    PromotionsVO toVO(Promotions promotionsById);
}
