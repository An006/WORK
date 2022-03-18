package com.itheima.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.util.UUID;

/**
 * 体检套餐管理
 */
@RestController
@RequestMapping("/setmeal")
public class SetMealController {
    //使用redispool操作redis服务
    @Autowired
    private JedisPool jedisPool;

    @RequestMapping("/upload")
    public Result upload(@RequestParam("imgFile") MultipartFile imgFile){

        String originalFilename = imgFile.getOriginalFilename();//拿到后缀，原始文件名
        int i = originalFilename.lastIndexOf(".");
        String substring = originalFilename.substring(i-1);
        String fileName= UUID.randomUUID().toString()+substring;//拿到36位的字符串

        try {
            //将文件上传到七牛云服务器
            QiniuUtils.upload2Qiniu(imgFile.getBytes(),fileName);
            jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_RESOURCES,fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return new Result(false, MessageConstant.PIC_UPLOAD_FAIL);
        }
        return new Result(true, MessageConstant.UPLOAD_SUCCESS,fileName);
    }

    @Reference
    private SetmealService setmealService;

    //新增套餐组
    @RequestMapping("/add")
    public Result add(@RequestBody Setmeal setmeal, Integer[] checkgroupIds/*跟传的参数名保持一致*/){
        try {
            setmealService.add(setmeal,checkgroupIds);
            //成功情况
            return new Result(true,MessageConstant.ADD_SETMEAL_SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false, MessageConstant.ADD_SETMEAL_FAIL);
        }
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return setmealService.pageQuery(queryPageBean);
    }



    //删除检查组
    @RequestMapping("/deleteSetmeal")
    public Result deleteSetmeal(Integer id){//跟传的参数名保持一致
        try {
            setmealService.deleteSetmeal(id);
        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false, "删除套餐成功！！");
        }
        //成功情况
        return new Result(true,"删除套餐失败！！");
    }
}
