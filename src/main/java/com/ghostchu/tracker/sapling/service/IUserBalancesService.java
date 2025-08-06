package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Currencies;
import com.ghostchu.tracker.sapling.entity.UserBalances;
import com.ghostchu.tracker.sapling.vo.CurrencyVO;
import com.ghostchu.tracker.sapling.vo.UserBalancesVO;
import com.github.yulichang.base.MPJBaseService;
import org.springframework.cache.annotation.CacheEvict;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IUserBalancesService extends MPJBaseService<UserBalances> {

    Map<Currencies, BigDecimal> getUserBalances(Long id);

    UserBalances selectUserBalanceForUpdate(long userId, long currencyId);

    void saveUserBalance(UserBalances userBalances);

    @CacheEvict(value = "userBalances", key = "#userBalances.owner")
    void addUserBalance(long userId, long currencyId, BigDecimal amount);

    UserBalancesVO toVO(UserBalances userBalances);

    Map<CurrencyVO, BigDecimal> toVO(Map<Currencies, BigDecimal> userBalances);
}
