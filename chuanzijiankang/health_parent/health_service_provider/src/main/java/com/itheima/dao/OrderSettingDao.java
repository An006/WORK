package com.itheima.dao;

import com.itheima.pojo.OrderSetting;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface OrderSettingDao {
    public Long findCountByOrderData(Date orderDate);

    public void editNumberByOrderData(OrderSetting orderSetting);

    public void add(OrderSetting orderSetting);

    public List<OrderSetting> getOrderSettingByMonth(Map<String, String> map);

    public OrderSetting findByOrderData(Date parseString2Date);

    public void editReservationsByOrderDate(OrderSetting orderSetting);
}
