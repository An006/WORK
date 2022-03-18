package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 检查组服务
 */
@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    private CheckGroupDao checkGroupDao;

    //新增检查组，同时需要让检查组管理检查项
    public void add(CheckGroup checkGroup, Integer[] checkitemIds) {
            //新增检查组，操作checkGroup
        checkGroupDao.add(checkGroup);
        //设置检查组和检查项的关联关系，多对多，操作中间关系表，checkgroup_checkItem
        Integer id = checkGroup.getId();
        setCheckGroupAndCheckItem(checkGroup,checkitemIds);
    }

    //分页查询
    public PageResult pageQuery(QueryPageBean queryPageBean){
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //基于mybatis的分页查询
        PageHelper.startPage(currentPage,pageSize);
        Page<CheckGroup> page= checkGroupDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //根据id查找检查组
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    //根据检查组id查检查项id
    public List<Integer> findCheckItemsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemsByCheckGroupId(id);
    }

    //编辑检查项和检查组信息
    public void edit(CheckGroup checkGroup, Integer[] checkitemIds) {
            //修改检查组基本信息
            checkGroupDao.edit(checkGroup);
            //清理当前检查组关联的检查项
            Integer id = checkGroup.getId();
            checkGroupDao.deleteAssoication(id);
            //重新加入关系
            setCheckGroupAndCheckItem(checkGroup,checkitemIds);
    }

    //删除检查组
    public void deleteCheckGroup(Integer id) {
        //清理当前检查组关联的检查项
        checkGroupDao.deleteAssoication(id);
        //清理检查组中的数据
        checkGroupDao.deleteCheckGroup(id);
    }

    //查询所有检查组
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }

    private void setCheckGroupAndCheckItem(CheckGroup checkGroup,Integer[] checkitemIds) {
        if (checkitemIds != null && checkitemIds.length > 0) {
            Integer id =checkGroup.getId() ;
            for (Integer checkitemId : checkitemIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("checkGroupId", id);
                map.put("checkItemId", checkitemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }

}
