package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Widgets;
import com.ghostchu.tracker.sapling.vo.WidgetsVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IWidgetsService extends MPJBaseService<Widgets> {
    Widgets selectWidgetsByIdForUpdate(long id);

    IPage<Widgets> pageWidgets(int page, int size, String search);

    void saveWidgets(Widgets widgets);

    void deleteWidget(Long id);

    Widgets getWidgetById(Long id);

    WidgetsVO toVO(Widgets widgetById);
}
