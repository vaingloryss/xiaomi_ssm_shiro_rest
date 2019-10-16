package com.vainglory.service.serviceImpl;

import com.vainglory.mapper.RoleMapper;
import com.vainglory.mapper.UserMapper;
import com.vainglory.pojo.User;
import com.vainglory.service.IUserService;
import com.vainglory.util.CodeUtils;
import com.vainglory.util.EmailUtils;
import com.vainglory.util.MD5Util;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service("userServiceImpl")
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User checkUserName(String username) {
        User user = userMapper.findByUserName(username);
        return user;
    }

    @Override
    public void register(User user) {
        System.out.println("UserServiceImpl日志：register...");
        //开发时，flag默认为1，省去激活步骤
        user.setFlag(1);
        user.setCode(CodeUtils.getCode());
        user.setSalt(UUID.randomUUID().toString());
        Sha256Hash sha256Hash = new Sha256Hash(user.getPassword(),user.getSalt(),1000);
        user.setPassword(sha256Hash.toBase64());
        userMapper.add(user);
        EmailUtils.sendEmail(user);
        System.out.println(user.getId());
        //为新注册的用户添加角色：1：管理员，2，用户
        roleMapper.add(user.getId(),2);
    }

    @Override
    public List<User> getUserList(Integer flag) {
        return userMapper.findByFlag(flag);
    }

    @Override
    public void deleteUser(Integer id) {
        userMapper.deleteUser(id);
    }

    @Override
    public User queryUserByUsername(String username) {
        return userMapper.findByUserName(username);
    }
}
