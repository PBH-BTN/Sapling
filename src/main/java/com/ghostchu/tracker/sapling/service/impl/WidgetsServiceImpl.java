package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Widgets;
import com.ghostchu.tracker.sapling.mapper.WidgetsMapper;
import com.ghostchu.tracker.sapling.service.ICurrenciesService;
import com.ghostchu.tracker.sapling.service.IWidgetsService;
import com.ghostchu.tracker.sapling.vo.WidgetsVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class WidgetsServiceImpl extends MPJBaseServiceImpl<WidgetsMapper, Widgets> implements IWidgetsService {
    @Autowired
    private ICurrenciesService currenciesService;

    @Override
    public Widgets selectWidgetsByIdForUpdate(long id) {
        return baseMapper.selectWidgetsByIdForUpdate(id);
    }

    @Override
    public IPage<Widgets> pageWidgets(int page, int size, String search) {
        IPage<Widgets> widgetsIPage = new Page<>(page, size);
        QueryWrapper<Widgets> queryWrapper = new QueryWrapper<>();
        queryWrapper = queryWrapper.like(search != null, "name", search);
        return page(widgetsIPage, queryWrapper);
    }

    @Override
    @CacheEvict(value = "widgets", key = "#widget.id")
    public void saveWidgets(Widgets widget) {
        saveOrUpdate(widget);
    }

    @Override
    public void deleteWidget(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Cacheable(value = "widgets", key = "#id")
    public Widgets getWidgetById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public WidgetsVO toVO(Widgets widgets) {
        WidgetsVO widgetsVO = new WidgetsVO();
        widgetsVO.setId(widgets.getId());
        widgetsVO.setName(widgets.getName());
        widgetsVO.setIcon(widgets.getIcon());
        widgetsVO.setColor(widgets.getColor());
        widgetsVO.setBuyable(widgets.isBuyable());
        widgetsVO.setBuyCurrency(currenciesService.toVO(currenciesService.getCurrency(widgets.getBuyCurrency())));
        widgetsVO.setBuyBalance(widgets.getBuyBalance());
        widgetsVO.setStock(widgets.getStock());
        widgetsVO.setRestockCronExpression(widgets.getRestockCronExpression());
        widgetsVO.setLastRestock(widgets.getLastRestock());
        widgetsVO.setManaged(widgets.isManaged());
        return widgetsVO;
    }
}
