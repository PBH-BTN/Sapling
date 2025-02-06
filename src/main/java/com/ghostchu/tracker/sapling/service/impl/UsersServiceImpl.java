package com.ghostchu.tracker.sapling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.mapper.UsersMapper;
import com.ghostchu.tracker.sapling.service.IPermissionGroupsService;
import com.ghostchu.tracker.sapling.service.IUsersService;
import com.ghostchu.tracker.sapling.vo.UserVO;
import com.github.yulichang.base.MPJBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
@Service
public class UsersServiceImpl extends MPJBaseServiceImpl<UsersMapper, Users> implements IUsersService {
    @Autowired
    private IPermissionGroupsService permissionGroupsService;

    @Override
    public Users getUserByUsernameAndPasswordHash(String username, String passhash) {
        return getOne(new QueryWrapper<Users>()
                .eq("name", username)
                .eq("passhash", passhash));
    }

    @Override
    public Users getUserById(long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public boolean registerUser(String username, String passhash, String email, InetAddress registerIp) {
        Users user = new Users();
        user.setName(username);
        user.setPasshash(passhash);
        user.setEmail(email);
        user.setRegisterAt(OffsetDateTime.now());
        user.setLanguage("zh-CN");
        user.setLevelPermissionGroup(1L);
        user.setRegisterIp(registerIp);
        user.setRegisterAt(OffsetDateTime.now());
        user.setAllowLogin(true);
        user.setSystemAccount(false);
        user.setDownloaded(0L);
        user.setUploaded(0L);
        user.setUploadedReal(0L);
        user.setDownloadedReal(0L);
        user.setSeedTime(0L);
        user.setLeechTime(0L);
        user.setMyBandwidthDownload(0L);
        user.setMyBandwidthUpload(0L);
        user.setPrimaryPermissionGroup(1L);
        user.setAvatar("/assets/img/avatar.png");
        user.setPasskey(UUID.randomUUID().toString());
        return this.save(user);
    }

    @Override
    public boolean userNameExists(String username) {
        return getOne(new QueryWrapper<Users>()
                .eq("name", username)) != null;
    }

    @Override
    public boolean userEmailExists(String email) {
        return getOne(new QueryWrapper<Users>()
                .eq("email", email)) != null;
    }

    @Override
    public UserVO toVO(Users user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setAvatar(user.getAvatar());
        vo.setTitle(user.getTitle());
        vo.setPrimaryPermissionGroup(permissionGroupsService.toVO(permissionGroupsService.getPermissionGroupById(user.getPrimaryPermissionGroup())));
        vo.setUploaded(user.getUploaded());
        vo.setUploadedReal(user.getUploadedReal());
        vo.setDownloaded(user.getDownloaded());
        vo.setDownloadedReal(user.getDownloadedReal());
        vo.setSeedTime(user.getSeedTime());
        vo.setLeechTime(user.getLeechTime());
        vo.setLanguage(user.getLanguage());
        vo.setSignature(user.getSignature());
        vo.setMyBandwidthUpload(user.getMyBandwidthUpload());
        vo.setMyBandwidthDownload(user.getMyBandwidthDownload());
        vo.setMyIsp(user.getMyIsp());
        vo.setBanned(user.getBannedId() != null);
        vo.setWarned(user.getWarnedId() != null);
        vo.setSystemAccount(user.isSystemAccount());
        vo.setLevelPermissionGroup(permissionGroupsService.toVO(permissionGroupsService.getPermissionGroupById(user.getLevelPermissionGroup())));
        return vo;
    }

    @Override
    public Users getUserByPasskey(String passkey) {
        return getOne(new QueryWrapper<Users>().eq("passkey", passkey));
    }

}
