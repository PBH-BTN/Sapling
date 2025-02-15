package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Currencies;
import com.ghostchu.tracker.sapling.mapper.CurrenciesMapper;
import com.ghostchu.tracker.sapling.service.ICurrenciesService;
import com.ghostchu.tracker.sapling.vo.CurrencyVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
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
public class CurrenciesServiceImpl extends MPJBaseServiceImpl<CurrenciesMapper, Currencies> implements ICurrenciesService {

    @Override
    public IPage<Currencies> pageCurrencies(int page, int size, String search) {
        IPage<Currencies> iPage = new Page<>(page, size);
        return page(iPage, null);
    }

    @Override
    @Cacheable(value = "currencies", key = "#id", unless = "#result == null")
    public Currencies getCurrency(Long id) {
        return getById(id);
    }

    @Override
    public CurrencyVO toVO(Currencies currency) {
        CurrencyVO currencyVO = new CurrencyVO();
        currencyVO.setId(currency.getId());
        currencyVO.setName(currency.getName());
        currencyVO.setColor(currency.getColor());
        currencyVO.setManaged(currency.isManaged());
        return currencyVO;
    }

    @Override
    @CacheEvict(value = "currencies", key = "#id")
    public void deleteCurrency(Long id) {
        Currencies currencies = getById(id);
        if (currencies != null) {
            if (currencies.isManaged()) {
                throw new IllegalStateException("Cannot delete managed currency");
            }
            removeById(id);
        }
    }

    @Override
    @CacheEvict(value = "currencies", allEntries = true)
    public void saveCurrency(Currencies currencies) {
        saveOrUpdate(currencies);
    }
}
