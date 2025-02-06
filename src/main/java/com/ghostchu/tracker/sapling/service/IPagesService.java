package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Pages;
import com.ghostchu.tracker.sapling.vo.PagesVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IPagesService extends MPJBaseService<Pages> {

    Pages getPages(String slug);

    PagesVO toVO(Pages pages);

    String renderHtml(Pages pages);
}
