package com.ghostchu.tracker.sapling.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghostchu.tracker.sapling.entity.UserBans;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.gvar.Setting;
import com.ghostchu.tracker.sapling.mapper.UsersMapper;
import com.ghostchu.tracker.sapling.service.*;
import com.ghostchu.tracker.sapling.vo.UserBansVO;
import com.ghostchu.tracker.sapling.vo.UserVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class UsersServiceImpl extends MPJBaseServiceImpl<UsersMapper, Users> implements IUsersService {
    @Autowired
    private IPermissionGroupsService permissionGroupsService;
    @Autowired
    private ILevelPermissionGroupsService levelPermissionGroupsService;
    @Autowired
    private IUserBansService userBansService;
    @Autowired
    private ISettingsService settingsService;

    @Override
    public Users getUserByUsernameAndPasswordHash(String username, String passhash) {
        return getOne(new QueryWrapper<Users>()
                .eq("name", username)
                .eq("passhash", passhash));
    }

    @Override
    @Cacheable(value = "users", key = "'id:' + #id", unless = "#result == null")
    public Users getUserById(long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public Users getUserByIdForUpdate(long id) {
        return baseMapper.selectUserForUpdate(id);
    }

    @Override
    public Users registerUser(String username, String passhash, String email, InetAddress registerIp) {
        long groupId = Long.parseLong(settingsService.getValue(Setting.USER_DEFAULTGROUP).orElseThrow());
        Users user = new Users();
        user.setName(username);
        user.setPasshash(passhash);
        user.setEmail(email);
        user.setRegisterAt(OffsetDateTime.now());
        user.setLanguage("zh-CN");
        user.setLevelPermissionGroup(11L);
        user.setRegisterIp(registerIp);
        user.setRegisterAt(OffsetDateTime.now());
        user.setAllowLogin(true);
        user.setSystemAccount(false);
        user.setMyBandwidthDownload(0L);
        user.setMyBandwidthUpload(0L);
        user.setPrimaryPermissionGroup(1L);
        user.setAvatar("/assets/img/avatar.png");
        user.setPasskey(UUID.randomUUID().toString());
        var group = permissionGroupsService.getPermissionGroupById(groupId);
        if (group == null) {
            throw new IllegalStateException("默认用户组不存在");
        }
        user.setPrimaryPermissionGroup(groupId);
        if (!this.save(user)) {
            throw new IllegalStateException("用户注册过程中出现了非预期错误");
        }
        return user;
    }

    @Override
    @Cacheable(value = "users.exists", key = "'username:' + #username", unless = "#result == null")
    public boolean userNameExists(String username) {
        return getOne(new QueryWrapper<Users>()
                .eq("name", username)) != null;
    }

    @Override
    @Cacheable(value = "users.exists", key = "'email:' + #email", unless = "#result == null")
    public boolean userEmailExists(String email) {
        return getOne(new QueryWrapper<Users>()
                .eq("email", email)) != null;
    }

    @Override
    public UserVO toVO(Users user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId() == null ? 0 : user.getId());
        vo.setName(user.getName());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setTitle(user.getTitle());
        vo.setPrimaryPermissionGroup(permissionGroupsService.toVO(permissionGroupsService.getPermissionGroupById(user.getPrimaryPermissionGroup())));
        vo.setLanguage(user.getLanguage());
        vo.setSignature(user.getSignature());
        vo.setMyBandwidthUpload(user.getMyBandwidthUpload());
        vo.setMyBandwidthDownload(user.getMyBandwidthDownload());
        vo.setMyIsp(user.getMyIsp());
        vo.setBanned(user.getBannedId() != null);
        vo.setWarned(user.getWarnedId() != null);
        vo.setSystemAccount(user.isSystemAccount());
        vo.setLevelPermissionGroup(levelPermissionGroupsService.toVO(levelPermissionGroupsService.getLevelPermissionGroupById(user.getLevelPermissionGroup())));
        vo.setRegisterAt(user.getRegisterAt());
        vo.setLastLoginAt(user.getLastLoginAt());
        vo.setLastAccessAt(user.getLastAccessAt());
        vo.setRegisterIp(user.getRegisterIp().getHostAddress());
        vo.setLastLoginIp(user.getLastLoginIp().getHostAddress());
        vo.setLastAccessIp(user.getLastAccessIp().getHostAddress());
        vo.setPasskey(user.getPasskey());
        return vo;
    }

    @Override
    @Cacheable(value = "users", key = "'passkey:' + #passkey")
    public Users getUserByPasskey(String passkey) {
        return getOne(new QueryWrapper<Users>().eq("passkey", passkey));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "'id:' + #user.id"),
            @CacheEvict(value = "users.exists", key = "'username:' + #user.name"),
            @CacheEvict(value = "users.exists", key = "'email:' + #user.email"),
            @CacheEvict(value = "users", key = "'passkey:' + #user.passkey"),
            @CacheEvict(value = "stp.permissions", key = "'id:' + #user.id"),
            @CacheEvict(value = "stp.roles", key = "'id:' + #user.id")
    })
    public boolean updateUser(Users user) {
        return updateById(user);
    }

    @Override
    public IPage<Users> searchUsers(int page, int size, String search) {
        Page<Users> p = new Page<>(page, size);
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper = wrapper
                .like(StringUtils.isNotBlank(search), "name", "%" + search + "%")
                .or()
                .like(StringUtils.isNotBlank(search), "email", "%" + search + "%")
                .orderByAsc("id");
        return page(p, wrapper);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "'id:' + #result.id"),
            @CacheEvict(value = "users.exists", key = "'username:' + #result.name"),
            @CacheEvict(value = "users.exists", key = "'email:' + #result.email"),
            @CacheEvict(value = "users", key = "'passkey:' + #result.passkey"),
            @CacheEvict(value = "stp.permissions", key = "'id:' + #result.id"),
            @CacheEvict(value = "stp.roles", key = "'id:' + #result.id")
    })
    public Users banUser(long id, long op, String reason, OffsetDateTime endedAt) {
        Users user = this.baseMapper.selectUserForUpdate(id);
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }
        var userBans = userBansService.banUser(user.getId(), op, reason, endedAt);
        user.setBannedId(userBans.getId());
        saveOrUpdate(user);
        StpUtil.disable(id, "login", OffsetDateTime.now().until(endedAt, ChronoUnit.SECONDS));
        return user;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", key = "'id:' + #result.id"),
            @CacheEvict(value = "users.exists", key = "'username:' + #result.name"),
            @CacheEvict(value = "users.exists", key = "'email:' + #result.email"),
            @CacheEvict(value = "users", key = "'passkey:' + #result.passkey"),
            @CacheEvict(value = "stp.permissions", key = "'id:' + #result.id"),
            @CacheEvict(value = "stp.roles", key = "'id:' + #result.id")
    })
    public Users unbanUser(long id, long op) {
        Users user = this.baseMapper.selectUserForUpdate(id);
        if (user == null) {
            throw new IllegalStateException("用户不存在");
        }
        user.setBannedId(null);
        saveOrUpdate(user);
        StpUtil.untieDisable(id);
        return user;
    }

    @Override
    public UserBansVO toUserBanVO(UserBans userBans) {
        UserBansVO vo = new UserBansVO();
        vo.setId(userBans.getId());
        vo.setOwner(toVO(getById(userBans.getOwner())));
        vo.setOperator(toVO(getById(userBans.getOperator())));
        vo.setCreatedAt(userBans.getCreatedAt());
        vo.setEndedAt(userBans.getEndedAt());
        vo.setDescription(userBans.getDescription());
        return vo;
    }
}
