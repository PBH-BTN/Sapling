package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Promotions;
import com.ghostchu.tracker.sapling.vo.PromotionsVO;
import com.ghostchu.tracker.sapling.vo.TorrentsVO;
import com.ghostchu.tracker.sapling.vo.UserVO;
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
public interface IPromotionsService extends MPJBaseService<Promotions> {
    Promotions getPromotionsById(Long id);

    Promotions getPromotionsByIdAndPromotionStatus(Long id);

    PromotionsVO toVO(Promotions promotionsById);

    List<Promotions> getPromotions();

    void deletePromotion(Long id);

    void saveOrUpdatePromotions(Promotions promotions);

    Promotions generatePromotion(TorrentsVO torrents, UserVO users);
}
