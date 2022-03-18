package com.itheima.jobs;

import com.itheima.constant.RedisConstant;
import com.itheima.utils.QiniuUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * 自定义job，实现定时清理垃圾图片。
 */
public class clearImgJob {
    @Autowired
    private JedisPool jedisPool;

    public void clearImg(){
        //根据redis的两个集合中，进行差值计算，获得垃圾图片名称
        Set<String> sdiff = jedisPool.getResource().sdiff(RedisConstant.SETMEAL_PIC_RESOURCES, RedisConstant.SETMEAL_PIC_DB_RESOURCES);
        //遍历set结合
        if (sdiff !=null) {
            for (String s : sdiff) {
                QiniuUtils.deleteFileFromQiniu(s);
                //从redis集合中删除图片名称
                jedisPool.getResource().srem(RedisConstant.SETMEAL_PIC_RESOURCES,s);
            }
        }
    }
}
