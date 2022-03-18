package com.itheima.controller;

import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.service.SpringSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {
    //获得当前用户的用户名
    @RequestMapping("/getusername")
    public Result getUsername(){
        //当springSecurity完成认证后，会将当前用户信息保存到上下文对象中，低层基于session
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();//由框架提供的user对象
        if (user!=null) {
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,user.getUsername());
        }else {
            return new Result(false,MessageConstant.GET_USERNAME_FAIL,null);
        }

    }

}
