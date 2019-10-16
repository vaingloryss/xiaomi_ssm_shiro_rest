package com.vainglory.service;

import com.vainglory.pojo.User;

import java.util.List;

public interface IUserService {
    User checkUserName(String username);
    void register(User user);
    User queryUserByUsername(String username);
}
