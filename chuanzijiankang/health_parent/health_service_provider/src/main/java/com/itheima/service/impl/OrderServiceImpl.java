package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.constant.MessageConstant;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.OrderSettingDao;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.pojo.Order;
import com.itheima.pojo.OrderSetting;
import com.itheima.service.OrderService;
import com.itheima.utils.DateUtils;
import org.apache.xmlbeans.impl.jam.mutable.MMember;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 体检预约服务
 */
@Service(interfaceClass = OrderService.class)
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderSettingDao orderSettingDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;

    //预约逻辑处理
    public Result order(Map map) throws Exception {

        //1.检查用户所选择的预约日期是否已经提前进行了预约设置，如果没有设置则无法进行。
        String data=(String) map.get("orderDate");
        Date date = DateUtils.parseString2Date(data);//预约的日期
        OrderSetting orderSetting = orderSettingDao.findByOrderData(date);
        if (orderSetting == null) {
            //指定日期没有进行预约设置，无法完成体检预约
            return new Result(false,MessageConstant.SELECTED_DATE_CANNOT_ORDER);
        }

        //2.检查用户所选择的预约日期是否已经约满，如果约满则无法预约。
        if (!(orderSetting.getNumber() >= orderSetting.getReservations())) {
            //已经约满，无法预约
            return new Result(false,MessageConstant.ORDER_FULL);
        }

        //3.检查用户是否为重复预约（同一天，同一个用户预约同一个套餐），如果是重复预约则无法完成再次预约
        String mail = (String) map.get("telephone");

        Member member = memberDao.findByTelephone(mail);

        if(member != null/*如果用户不为空*/){
            //判断是否在重复预约
            Integer id = member.getId();
            Integer setmealId = Integer.parseInt( (String) map.get("setmealId"));//套餐id
            Order order = new Order(id, date, setmealId);
            List<Order> Orders = orderDao.findByCondition(order);
            if (Orders !=null && Orders.size()>0) {//加一个判断比较保险，万一有其他套餐在预约了，那么这是or ，不是 and
                for (Order order1 : Orders) {
                    if (order1.getSetmealId().equals(setmealId)) {
                        return new Result(false,MessageConstant.HAS_ORDERED);
                    }
                }
            }
        }else {
            //4.检查当前用户是否为会员，如果是会员则直接完成预约，如果不是会员，则自动完成注册并进行预约
            String name = (String) map.get("name");
            String sex=(String)map.get("sex");
            String idCard=(String)map.get("idCard");

            member.setName(name);
            member.setName(sex);
            member.setIdCard(idCard);
            member.setPhoneNumber(mail);
            member.setRegTime(new Date());
            member.setEmail(mail);
            memberDao.add(member);
        }

        //5.预约成功，更新当日的已预约人数。
        Integer id = member.getId();
        String orderType=(String) map.get("orderType");
        Integer setmealId=Integer.parseInt( (String) map.get("setmealId"));
        Order order = new Order(id/*设置主键值，因为添加后给与了返回*/, date,orderType ,Order.ORDERSTATUS_NO,setmealId);

        orderDao.add(order);

        Object Lock = new Object();
        synchronized (Lock){
            orderSetting.setReservations(orderSetting.getReservations()+1);//设置已预约人数
        }

        orderSettingDao.editReservationsByOrderDate(orderSetting);

        return new Result(true, MessageConstant.ORDER_SUCCESS,order.getId());
    }

    //查询预约信息(体检人姓名，体检日期，体检套餐，预约类型)
   public Map findById(Integer id/*预约id*/) {
        Map byId4Detail = orderDao.findById4Detail(id);
       if (byId4Detail !=null) {
           try {
               Date orderDate = (Date) byId4Detail.remove("orderDate");
               String date = DateUtils.parseDate2String(orderDate);
               byId4Detail.put("orderDate",date);
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
        return byId4Detail;
   }

}
