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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
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
    public Map<Currencies, BigDecimal> getUserBalances(Long id) {
        Map<Currencies, BigDecimal> map = new HashMap<>();
        List<UserBalances> userBalances = list(new QueryWrapper<UserBalances>()
                .eq("owner", id));
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
        Map<CurrencyVO, BigDecimal> map = new HashMap<>();
        for (Map.Entry<Currencies, BigDecimal> entry : userBalances.entrySet()) {
            map.put(currenciesService.toVO(entry.getKey()), entry.getValue());
        }
        return map;
    }
}
