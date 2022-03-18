package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Order;
import com.itheima.service.OrderService;
import com.itheima.utils.MailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private JedisPool jedisPool;

    @Reference
    private OrderService orderService;

    @RequestMapping("/submit")
    public Result submit(@RequestBody Map orderInfo){

        //验证验证码，输入是否正确
        String mail = (String) orderInfo.get("telephone");
        //将用户输入的验证码和redis保存的验证码进行对比，成功则存储到数据库
        String validateCode = jedisPool.getResource().get(mail + RedisMessageConstant.SENDTYPE_ORDER);//redis数据库中的
        String validateCode1 = (String) orderInfo.get("validateCode");//用户输入的
        System.out.println(orderInfo);

        if ( validateCode1 !=null && validateCode!=null && validateCode.equals(validateCode1)){

            orderInfo.put("orderType", Order.ORDERTYPE_WEIXIN);//分为两种预约方式，微信端自助预约/电话预约
                Result result=null;
                try {
                   result = orderService.order(orderInfo);//通过远程调用服务，实现在线预约业务处理
                }catch (Exception e){
                   e.printStackTrace();
                   return result;
               }
                if (result.isFlag()){
                     //预约成功，可以为用户发送短信
                    try {
                        MailUtils.sendMail(mail,"峰谷健康提醒你，你已经预约成功了哟！"+orderInfo.get("orderDate")+"--开车不规范，沟沟里打转！！！","峰谷健康");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            return result;

        }else {
        //如果比对不成功，则返回结果
        return new Result(false,MessageConstant.VALIDATECODE_ERROR);
        }
    }


    @RequestMapping("/findById")
    public Result findById(Integer id) {
        try {
            Map order = orderService.findById(id);
            return new Result(true, MessageConstant.QUERY_ORDER_SUCCESS, order);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.QUERY_ORDER_FAIL);
        }
    }
}
