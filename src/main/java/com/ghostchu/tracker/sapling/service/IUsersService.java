package com.ghostchu.tracker.sapling.service;

import com.ghostchu.tracker.sapling.entity.Users;
import com.baomidou.mybatisplus.extension.service.IService;

import java.net.InetAddress;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Ghost_chu
 * @since 2025-02-04
 */
public interface IUsersService extends IService<Users> {

    Users getUserByUsernameAndPasswordHash(String username, String passhash);

    boolean registerUser(String username, String passhash, String email, InetAddress registerIp);

    boolean userNameExists(String username);

    boolean userEmailExists(String email);
}
