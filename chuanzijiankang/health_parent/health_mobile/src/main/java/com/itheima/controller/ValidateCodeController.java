package com.itheima.controller;

import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.MailUtils;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * 验证码
 */

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    private JedisPool jedisPool;

    //体检预约发送验证码
    @RequestMapping("/send40rder")
    public Result send40rder(String mail){
        //随机生成4位数字验证码
        return sendValidateCodeAndResult(mail,RedisMessageConstant.SENDTYPE_ORDER,4);
    }

    //手机快速登录发送验证码
    @RequestMapping("/send4Login")
    public Result send4Login(String telephone){
        //随机生成6位数字验证码
        return sendValidateCodeAndResult(telephone,RedisMessageConstant.SENDTYPE_LOGIN,6);
    }






    private Result sendValidateCodeAndResult(String mail,String method,int length) {
        Integer integer = ValidateCodeUtils.generateValidateCode(length);
        //给用户发送验证码
        if (!MailUtils.sendMail(mail, "峰谷健康提醒你，你的验证码为:" + integer + "--开车不规范，沟沟里打转，5分钟有效哦！！！", "峰谷健康")) {
            //将验证码保存到redis（5分钟）
            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
        }
        jedisPool.getResource().setex(mail + method, 3000, integer.toString());
        return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
    }

}
