package com.itheima.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 检查项服务
 */
@Service(interfaceClass = CheckItemService.class)/*如果加了事物注解之后，那么就要明确当前服务实现的是那个接口*/
@Transactional
public class CheckItemServiceImpl implements CheckItemService {
    //注入dao对象
    @Autowired
    private CheckItemDao checkItemDao;

    //新增检查项
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
    }

    //分页查询检查项
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();//查询条件
        //完成分页查询，基于mybatis框架的分页助手插件来完成的，简化
        PageHelper.startPage(currentPage,pageSize);//基于本地线程实现的，
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        long total = page.getTotal();//总条数
        List<CheckItem> result = page.getResult();//数据集合
        return new PageResult(total,result);
    }

    //根据id删除检查项
    public void deleteById(Integer id){
            //判断当前检查项是否已经关联到检查组了，如果关联了，就删除不了，如果没有关联，则可以删除
        long countByCheckItemId = checkItemDao.findCountByCheckItemId(id);
        if (countByCheckItemId>0){
            //当前检查项已经被关联到检查组了，不允许删除
           throw  new RuntimeException(); /*这里少写throw吧 不然的话无论if是否满足，后边的delete操作都会执行*/
        }else {
            checkItemDao.deleteByid(id);
        }
    };

    //编辑检查项
    public void edit(CheckItem checkItem){
        checkItemDao.edit(checkItem);
    }

    //查询所需编辑的检查项
    public CheckItem findById(int id){
        return checkItemDao.findById(id);
    }

    //查询所有的检查项
    public List<CheckItem> findAll(){
        return checkItemDao.findAll();
    }

}
