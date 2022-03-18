package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.OrderSettingDao;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service(interfaceClass = OrderSettingService.class)
@Transactional
public class OrderSettingServiceImpl implements OrderSettingService {

    @Autowired
    private OrderSettingDao orderSettingDao;

    //批量导入预约设置数据
    public void add(List<OrderSetting> list) {
        addOrderSettings(list);
    }


    //根据月份查询对应的预约设置数据
    public List<Map> getOrderSettingByMonth(String date) {
        String begin=date+"-1";
        String end=date+"-31";
        Map<String,String> map = new HashMap<>();
        map.put("begin",begin);
        map.put("end",end);
        //调用dao，根据日期范围查询预约设置的数据
        List<OrderSetting> list = orderSettingDao.getOrderSettingByMonth(map);
        List<Map> maps = new ArrayList<>();
        if (list !=null && list.size()>0) {
            for (OrderSetting orderSetting : list) {
                Map<String, Object> stringObjectHashMap = new HashMap<>();
                stringObjectHashMap.put("date",orderSetting.getOrderDate().getDate());//获取日期序号
                stringObjectHashMap.put("number",orderSetting.getNumber());//获取设置预约数
                stringObjectHashMap.put("reservations",orderSetting.getReservations());
                maps.add(stringObjectHashMap);
            }
        }
        return maps;
    }

    //设置预约数
    public void editNumberByDate(OrderSetting orderSetting) {
        Long countByOrderData = orderSettingDao.findCountByOrderData(orderSetting.getOrderDate());
        //根据当前日期查询是否已经进行了预约设置
        if (countByOrderData>0) {
            //当前日期已经进行了预约设置，执行更新操作
            orderSettingDao.editNumberByOrderData(orderSetting);
        }else {
            //当前日期没有预约的相关设置，执行插入操作。
            orderSettingDao.add(orderSetting);

        }
    }


    private void addOrderSettings(List<OrderSetting> list) {
        //不要直接把数据，直接插入，需要检查，然后插入数据
        if (list!=null && list.size()>0) {
            for (OrderSetting orderSetting : list) {
                //检查此数据（日期）是否存在
                Long countByOrderData = orderSettingDao.findCountByOrderData(orderSetting.getOrderDate());
                if (countByOrderData>0){
                    //已经存在，执行更新操作
                    orderSettingDao.editNumberByOrderData(orderSetting);
                }else {
                    //不存在，执行删除操作
                    orderSettingDao.add(orderSetting);
                }

            }
        }
    }
}
