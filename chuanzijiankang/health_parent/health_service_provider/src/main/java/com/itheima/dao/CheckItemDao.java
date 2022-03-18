package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface CheckItemDao {
    public void add(CheckItem checkItem);
    public Page<CheckItem> selectByCondition(@Param(value = "value")String queryString);
    public long findCountByCheckItemId(Integer id);
    public void deleteByid(Integer id);
    public void edit(CheckItem checkItem);
    public CheckItem findById(int id);
    public List<CheckItem> findAll();

}
