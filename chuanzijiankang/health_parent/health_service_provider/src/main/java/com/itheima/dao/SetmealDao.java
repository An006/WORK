package com.itheima.dao;

import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetmealDao {
    public void add(Setmeal setmeal);
    public void setSetmealAndCheckGroup(Map<String, Integer> map);
    public Page<Setmeal> selectByCondition(String queryString);
    public List<Setmeal> getSetmeal();
    public void deleteAssoicatio(Integer id);
    public void deleteSetmeal(Integer id);
    //三表联合查询
    public Setmeal findById(Integer id);

    public List<Map<String, Object>> findSetmealCount();

}
