package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {
    public void add(CheckGroup checkGroup);
    public void setCheckGroupAndCheckItem(Map map);
    Page<CheckGroup> selectByCondition(String queryString);
    public CheckGroup findById(Integer id);
    List<Integer> findCheckItemsByCheckGroupId(Integer id);
    public void edit(CheckGroup checkGroup);

    public void deleteAssoication(Integer id);
    public void deleteCheckGroup(Integer id);

    public List<CheckGroup> findAll();
}
