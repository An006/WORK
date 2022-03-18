package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.PermissionDao;
import com.itheima.dao.RoleDao;
import com.itheima.dao.UserDao;
import com.itheima.pojo.Permission;
import com.itheima.pojo.Role;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;


/*
用户服务
 */
@Service(interfaceClass = UserService.class)
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private PermissionDao permissionDao;

    //根据用户名查询数据库获取用户信息和角色
    public User findByUsername(String username){
        User user=userDao.findByUsername(username);//查询用户基本信息，不包含用户角色

        if (user==null){
            return null;
        }

        Integer id = user.getId();
        //根据用户id查询对应的角色
        Set<Role> Roles = roleDao.findByUserId(id);
        //根据角色id查询权限
        for (Role role : Roles) {
            Integer id1 = role.getId();
            Set<Permission> roleandpermission = permissionDao.findByUserId(id1);
            role.setPermissions(roleandpermission);
        }
        user.setRoles(Roles);

        return user;
    }


}
