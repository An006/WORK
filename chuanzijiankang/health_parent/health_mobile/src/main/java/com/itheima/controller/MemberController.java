package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;

import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 处理会员相关操作
 */
@RestController
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private JedisPool jedisPool;
    @Reference
    private MemberService memberService;

    //手机号快速登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map loginInfo){
        //从redis中对比保存的验证码
        String mail = (String) loginInfo.get("telephone");
        String redisValidCode = jedisPool.getResource().get(mail + RedisMessageConstant.SENDTYPE_LOGIN);
        String validateCode = (String) loginInfo.get("validateCode");
        if (validateCode!=null && redisValidCode!=null && validateCode.equals(redisValidCode)){
            //验证码输入正确
            //判断当前用户是否为会员（查询会员来确定）
            Member member=memberService.findByTelephone(mail);
            if (member==null){
                //添加一些注册的属性
                member.setEmail(mail);
                member.setPhoneNumber(mail);
                member.setRegTime(new Date());
                memberService.add(member);
            }
            //向客户端，浏览器写入cookie,内容为手机号，跟踪用户，等用户发送任何一个请求，它都会带着cookie去，这个时候服务器就可以检查cookie,把手机号取出来就知道了那个用户发的请求。
            //区分用户对象
            Cookie cookie = new Cookie("login_member_mail",mail);
            cookie.setPath("/");//根路径
            cookie.setMaxAge(60*60*24*30);//30天
            response.addCookie(cookie);//把信息写到客户端浏览器
            //将会员信息保存到redis中
            String s  = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(mail,60*30,s);
            return new Result(true,MessageConstant.LOGIN_SUCCESS);
        }else{
            return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
    }
}
