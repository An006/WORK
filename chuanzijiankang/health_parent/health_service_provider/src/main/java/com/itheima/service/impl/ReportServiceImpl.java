package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.itheima.dao.MemberDao;
import com.itheima.dao.OrderDao;
import com.itheima.dao.SetmealDao;
import com.itheima.service.OrderService;
import com.itheima.service.ReportService;
import com.itheima.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional
@Service(interfaceClass = ReportService.class)
public class ReportServiceImpl implements ReportService {
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private SetmealDao setmealDao;

    //综合数据查询
    public Map<String, Object> getBusinessReportData() throws Exception {
        //使用map
        //            reportData:{
        //                        reportDate:null,
        //                        todayNewMember :0,
        //                        totalMember :0,
        //                        thisWeekNewMember :0,
        //                        thisMonthNewMember :0,
        //                        todayOrderNumber :0,
        //                        todayVisitsNumber :0,
        //                        thisWeekOrderNumber :0,
        //                        thisWeekVisitsNumber :0,
        //                        thisMonthOrderNumber :0,
        //                        thisMonthVisitsNumber :0,
        //                        hotSetmeal :[
        //                {name:'阳光爸妈升级肿瘤12项筛查（男女单人）体检套餐',setmeal_count:200,proportion:0.222},
        //                {name:'阳光爸妈升级肿瘤12项筛查体检套餐',setmeal_count:200,proportion:0.222}
        //                    ]
        //            }
        Map<String, Object> Map = new HashMap<>();

        //报表的产生日期
        String now = DateUtils.parseDate2String(DateUtils.getToday());
        Map.put("reportDate",now);

        //新增的会员数
        Map.put("todayNewMember",memberDao.findMemberCountByDate(now));

        //总会员数
        Map.put("totalMember",memberDao.findMemberTotalCount());

        //本周新增会员数
        //获得本周一日期
        Date thisWeekMonday = DateUtils.getThisWeekMonday();
        String thisWeekMonday1 = DateUtils.parseDate2String(thisWeekMonday);
        Map.put("thisWeekNewMember",memberDao.findMemberCountAfterDate(thisWeekMonday1));

        //本月新增会员数
        //本月第一天
        Date firstDay4ThisMonth = DateUtils.getFirstDay4ThisMonth();
        String firstDay4ThisMonth1 = DateUtils.parseDate2String(firstDay4ThisMonth);
        Map.put("thisMonthNewMember",memberDao.findMemberCountAfterDate(firstDay4ThisMonth1));

        //今日订单数
        //todayOrderNumber
        Map.put("todayOrderNumber",orderDao.findOrderCountByDate(now));

        //今日到诊人数
        //todayVisitsNumber
        Map.put("todayVisitsNumber",orderDao.findVisitsCountByDate(now));

        //这个星期的订单数
        //thisWeekOrderNumber
        Map.put("thisWeekOrderNumber",orderDao.findOrderCountAfterDate(thisWeekMonday1));

        //这个星期的到诊人数
        // thisWeekVisitsNumber
        Map.put("thisWeekVisitsNumber",orderDao.findVisitsCountAfterDate(thisWeekMonday1));

        //thisMonthOrderNumber
        Map.put("thisMonthOrderNumber",orderDao.findOrderCountAfterDate(firstDay4ThisMonth1));
        //thisMonthVisitsNumber
        Map.put("thisMonthVisitsNumber",orderDao.findVisitsCountAfterDate(firstDay4ThisMonth1));

        //热门套餐
        List<Map<String,Object>> hotSetmeal = orderDao.findHotSetmeal();
        Map.put("hotSetmeal",hotSetmeal);

        return Map;
    }
}
