package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.Currencies;
import com.ghostchu.tracker.sapling.entity.LevelPermissionGroups;
import com.ghostchu.tracker.sapling.entity.UserStats;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class UsersPromoteService {
    @Autowired
    private ILevelPermissionGroupsService levelPermissionGroupsService;
    @Autowired
    private IUsersService usersService;
    @Autowired
    private IUserStatsService userStatsService;
    @Autowired
    private IUserBalancesService userBalancesService;
    @Autowired
    private ISettingsService settingsService;
    @Autowired
    private IMessagesService messagesService;

    @Transactional
    @Scheduled(cron = "0 */30 * * * ?")
    public void handleUsersPromote() {
        log.info("执行用户晋级权限组更新计划任务……");
        List<LevelPermissionGroups> levelPermissionGroups = levelPermissionGroupsService.list(new QueryWrapper<LevelPermissionGroups>().orderByAsc("priority"));
        LevelPermissionGroups userDefaultGroup = levelPermissionGroupsService.getLevelPermissionGroupById(Long.parseLong(settingsService.getValue(Setting.USER_DEFAULTLEVELGROUP).orElseThrow()));
        if (userDefaultGroup == null) throw new IllegalArgumentException("默认用户等级组不存在，用户提升无法继续");
        int page = 1;
        int size = 1000;
        IPage<Users> usersIPage = usersService.page(new Page<>(page, size));
        do {
            for (Users readOnlyUser : usersIPage.getRecords()) {
                LevelPermissionGroups userGroup = levelPermissionGroupsService.getLevelPermissionGroupById(readOnlyUser.getLevelPermissionGroup());
                UserStats userStats = userStatsService.getUserStats(readOnlyUser.getId());
                Map<Currencies, BigDecimal> userBalances = userBalancesService.getUserBalances(readOnlyUser.getId());
                Map<Long, BigDecimal> idToBalance = new HashMap<>();
                for (Map.Entry<Currencies, BigDecimal> entry : userBalances.entrySet()) {
                    idToBalance.put(entry.getKey().getId(), entry.getValue());
                }
                LevelPermissionGroups newApplicableGroup = userDefaultGroup;
                for (LevelPermissionGroups groups : levelPermissionGroups) {
                    EvaluationContext context = new StandardEvaluationContext();
                    context.setVariable("user", usersService.toVO(readOnlyUser));
                    context.setVariable("userStats", userStatsService.toVO(userStats));
                    context.setVariable("userBalances", idToBalance);
                    context.setVariable("random", ThreadLocalRandom.current());
                    context.setVariable("now", OffsetDateTime.now());
                    ExpressionParser parser = new SpelExpressionParser();
                    Boolean evalResult = parser.parseExpression(groups.getCondition())
                            .getValue(context, Boolean.class);
                    if (Boolean.TRUE.equals(evalResult)) {
                        newApplicableGroup = groups;
                    }
                }
                if (newApplicableGroup.getId() != readOnlyUser.getLevelPermissionGroup()) {
                    String oldName = "<deleted>";
                    if (userGroup != null) {
                        oldName = userGroup.getName();
                    }
                    log.info("更改用户晋级权限组 {}: {} => {}", readOnlyUser.getName(), oldName, newApplicableGroup.getName());
                    Users writeUser = usersService.getUserByIdForUpdate(readOnlyUser.getId());
                    writeUser.setLevelPermissionGroup(newApplicableGroup.getId());
                    usersService.saveOrUpdate(writeUser);
                    messagesService.sendMessage("single", 1, List.of(readOnlyUser.getId()),
                            "用户等级等级发生变化",
                            "根据最新的统计数据，您的晋级权限组已调整为" + newApplicableGroup.getName() + "。阅读站点规则以了解您的新晋级权限组可使用的功能。");
                }
            }
            page++;
        } while (page <= usersIPage.getPages());
    }
}
