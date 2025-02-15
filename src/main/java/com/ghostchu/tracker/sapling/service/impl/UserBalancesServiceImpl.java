package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Currencies;
import com.ghostchu.tracker.sapling.entity.UserBalances;
import com.ghostchu.tracker.sapling.mapper.UserBalancesMapper;
import com.ghostchu.tracker.sapling.service.ICurrenciesService;
import com.ghostchu.tracker.sapling.service.IUserBalancesService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.CurrencyVO;
import com.ghostchu.tracker.sapling.vo.UserBalancesVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class UserBalancesServiceImpl extends MPJBaseServiceImpl<UserBalancesMapper, UserBalances> implements IUserBalancesService {
    @Autowired
    private ICurrenciesService currenciesService;
    @Autowired
    private IUsersService usersService;

    @Override
    @Cacheable(value = "userBalances", key = "#owner")
    public Map<Currencies, BigDecimal> getUserBalances(Long owner) {
        Map<Currencies, BigDecimal> map = new LinkedHashMap<>(); // 需要有序
        List<UserBalances> userBalances = list(new QueryWrapper<UserBalances>()
                .eq("owner", owner));
        for (Currencies currencies : currenciesService.list()) {
            map.put(currencies, BigDecimal.ZERO);
            for (UserBalances userBalance : userBalances) {
                if (userBalance.getCurrency() == currencies.getId()) {
                    map.put(currencies, userBalance.getBalance());
                    break;
                }
            }
        }
        return map;
    }

    @Override
    public UserBalances selectUserBalanceForUpdate(long userId, long currencyId) {
        UserBalances userBalances = baseMapper.selectUserBalancesForUpdate(userId, currencyId);
        if (userBalances == null) {
            userBalances = new UserBalances();
            userBalances.setOwner(userId);
            userBalances.setCurrency(currencyId);
            userBalances.setBalance(BigDecimal.ZERO);
            save(userBalances);
        }
        return userBalances;
    }

    @Override
    @CacheEvict(value = "userBalances", key = "#userBalances.owner")
    public void saveUserBalance(UserBalances userBalances) {
        saveOrUpdate(userBalances);
    }

    @Override
    @CacheEvict(value = "userBalances", key = "#userBalances.owner")
    public void addUserBalance(long userId, long currencyId, BigDecimal amount) {
        UserBalances userBalances = selectUserBalanceForUpdate(userId, currencyId);
        userBalances.setBalance(userBalances.getBalance().add(amount));
        saveUserBalance(userBalances);
    }

    @Override
    public UserBalancesVO toVO(UserBalances userBalances) {
        UserBalancesVO userBalancesVO = new UserBalancesVO();
        userBalancesVO.setId(userBalances.getId());
        userBalancesVO.setOwner(usersService.toVO(usersService.getUserById(userBalances.getOwner())));
        userBalancesVO.setCurrency(currenciesService.toVO(currenciesService.getCurrency(userBalances.getCurrency())));
        userBalancesVO.setBalance(userBalances.getBalance());
        return userBalancesVO;
    }

    @Override
    public Map<CurrencyVO, BigDecimal> toVO(Map<Currencies, BigDecimal> userBalances) {
        Map<CurrencyVO, BigDecimal> map = new LinkedHashMap<>(); // 需要有序
        for (Map.Entry<Currencies, BigDecimal> entry : userBalances.entrySet()) {
            map.put(currenciesService.toVO(entry.getKey()), entry.getValue());
        }
        return map;
    }
}
