package com.vainglory.service;

import com.vainglory.pojo.User;

import java.util.List;

public interface IUserService {
    User checkUserName(String username);
    void register(User user);

    List<User> getUserList(Integer flag);

    void deleteUser(Integer id);

    User queryUserByUsername(String username);
}
