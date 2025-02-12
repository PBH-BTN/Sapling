package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Promotions;
import com.ghostchu.tracker.sapling.mapper.PromotionsMapper;
import com.ghostchu.tracker.sapling.service.IPromotionsService;
import com.ghostchu.tracker.sapling.vo.PromotionsVO;
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
public class PromotionsServiceImpl extends MPJBaseServiceImpl<PromotionsMapper, Promotions> implements IPromotionsService {

    @Override
    @Cacheable(value = "promotions", key = "#id")
    public Promotions getPromotionsById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Promotions getPromotionsByIdAndPromotionStatus(Long id) {
        if (id == null) {
            return null;
        }
        return baseMapper.selectById(id);
    }

    @Override
    public PromotionsVO toVO(Promotions promotionsById) {
        PromotionsVO vo = new PromotionsVO();
        vo.setId(promotionsById.getId());
        vo.setName(promotionsById.getName());
        vo.setCondition(promotionsById.getCondition());
        vo.setUploadModifier(promotionsById.getUploadModifier());
        vo.setUploadModifier(promotionsById.getUploadModifier());
        vo.setColor(promotionsById.getColor());
        return vo;
    }

    @Override
    public List<Promotions> getPromotions() {
        return list();
    }

    @Override
    public void deletePromotion(Long id) {
        removeById(id);
    }

    @Override
    public void saveOrUpdatePromotions(Promotions promotions) {
        saveOrUpdate(promotions);
    }

}
