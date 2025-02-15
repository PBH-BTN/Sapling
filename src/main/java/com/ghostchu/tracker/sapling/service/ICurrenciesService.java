package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.Currencies;
import com.ghostchu.tracker.sapling.vo.CurrencyVO;
import com.github.yulichang.base.MPJBaseService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface ICurrenciesService extends MPJBaseService<Currencies> {

    IPage<Currencies> pageCurrencies(int page, int size, String search);

    Currencies getCurrency(Long id);

    CurrencyVO toVO(Currencies currency);

    void deleteCurrency(Long id);

    void saveCurrency(Currencies currency);
}
