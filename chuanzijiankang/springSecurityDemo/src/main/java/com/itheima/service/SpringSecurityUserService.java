package com.itheima.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 *  要想实现security框架的用户查询，必须要实现 UserDetailsService，类
 */


public class SpringSecurityUserService implements UserDetailsService {
    //引入加密对象
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //根据用户名查询用户信息
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("用户输入的用户名为："+username);
        //根据用户名查询数据库获得的用户信息，并不是以往的处理逻辑，包含数据库中存储的密码信息
        //将信息返回给框架
        //框架会进行密码比对（页面提交的密码和数据库查询的密码进行比对）
        //为当前登录用户动态的授权，后期需要改为数据库查询当前用户对应的权限
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        //根据用户名来授予角色权限
        grantedAuthorities.add(new SimpleGrantedAuthority("PERMISSION_A"));//授权
        grantedAuthorities.add(new SimpleGrantedAuthority("PERMISSION_B"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));//授予角色
        String password="admin";
        String s = initUser(password);
        return new User(username,s,grantedAuthorities); //如果不要加密协议，就要在密码前加{noop}
    }

    public String initUser(String password){
        return bCryptPasswordEncoder.encode(password);
    }
}
