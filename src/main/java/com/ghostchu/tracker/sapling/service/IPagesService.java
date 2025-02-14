package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
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

    PagesVO toVO(Pages pages, boolean render);

    String renderHtml(Pages pages);

    IPage<Pages> pagePages(int page, int size, String search);

    void savePage(Pages pages);

    void deletePage(Long id);
}
