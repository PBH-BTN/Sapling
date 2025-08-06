package com.ghostchu.tracker.sapling.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ghostchu.tracker.sapling.entity.UserBans;
import com.ghostchu.tracker.sapling.entity.Users;
import com.ghostchu.tracker.sapling.vo.UserBansVO;
import com.ghostchu.tracker.sapling.vo.UserVO;
import com.github.yulichang.base.MPJBaseService;

import java.net.InetAddress;
import java.time.OffsetDateTime;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IUsersService extends MPJBaseService<Users> {

    Users getUserByUsernameAndPasswordHash(String username, String passhash);

    Users getUserById(long id);

    Users getUserByIdForUpdate(long id);

    Users registerUser(String username, String passhash, String email, InetAddress registerIp);

    boolean userNameExists(String username);

    boolean userEmailExists(String email);

    UserVO toVO(Users userById);

    Users getUserByPasskey(String passkey);

    boolean updateUser(Users user);

    IPage<Users> searchUsers(int page, int size, String search);

    Users banUser(long id, long op, String reason, OffsetDateTime endedAt);

    Users unbanUser(long id, long op);

    UserBansVO toUserBanVO(UserBans userBans);
}
