package com.ghostchu.tracker.sapling.mapper;

import com.ghostchu.tracker.sapling.entity.Widgets;


/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface WidgetsMapper extends SaplingMapper<Widgets> {
    Widgets selectWidgetsByIdForUpdate(long id);
}

