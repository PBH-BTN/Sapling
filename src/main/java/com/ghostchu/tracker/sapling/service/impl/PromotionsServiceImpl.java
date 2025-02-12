package com.ghostchu.tracker.sapling.service.impl;

import com.ghostchu.tracker.sapling.entity.Promotions;
import com.ghostchu.tracker.sapling.mapper.PromotionsMapper;
import com.ghostchu.tracker.sapling.service.IPromotionsService;
import com.ghostchu.tracker.sapling.vo.PromotionsVO;
import com.ghostchu.tracker.sapling.vo.TorrentsVO;
import com.ghostchu.tracker.sapling.vo.UserVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
    public PromotionsVO toVO(Promotions promotions) {
        PromotionsVO vo = new PromotionsVO();
        vo.setId(promotions.getId() == null ? 0 : promotions.getId());
        vo.setName(promotions.getName());
        vo.setCondition(promotions.getCondition());
        vo.setUploadModifier(promotions.getUploadModifier());
        vo.setUploadModifier(promotions.getUploadModifier());
        vo.setColor(promotions.getColor());
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

    @Override
    public Promotions generatePromotion(TorrentsVO torrents, UserVO users) {
        for (Promotions promotions : list()) {
            ExpressionParser parser = new SpelExpressionParser();
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable("torrent", torrents);
            context.setVariable("user", users);
            context.setVariable("random", ThreadLocalRandom.current());
            Boolean evalResult = parser.parseExpression(promotions.getCondition()).getValue(context, Boolean.class);
            if (Boolean.TRUE.equals(evalResult)) {
                return promotions;
            }
        }
        return null;
    }

}
