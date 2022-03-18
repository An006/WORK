package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 检查组管理
 */
@RestController
@RequestMapping("/checkgroup")
public class CheckGroupController {

    @Reference
    private CheckGroupService checkGroupService;


    //新增检查组
    @RequestMapping("/add")
    public Result add(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds/*跟传的参数名保持一致*/){
        try {
            checkGroupService.add(checkGroup,checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        //成功情况
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

    //分页查询
    @RequestMapping("/findPage")
    public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
        return checkGroupService.pageQuery(queryPageBean);
    }

    //根据id查询检查组
    @RequestMapping("/findById")
    public Result findById(Integer id){
        try {
            CheckGroup checkGroup=checkGroupService.findById(id);
            //成功情况
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL,null);
        }
    }
    //根据id查询检查组
    @RequestMapping("/findCheckItemsByCheckGroupId")
    public Result findCheckItemsByCheckGroupId(Integer id){
        try {
            List<Integer> checkItemsByCheckGroupId=checkGroupService.findCheckItemsByCheckGroupId(id);
            //成功情况
            return new Result(true, MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItemsByCheckGroupId);
        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false,MessageConstant.QUERY_CHECKITEM_FAIL,null);
        }
    }

    //新增检查组
    @RequestMapping("/edit")
    public Result edit(@RequestBody CheckGroup checkGroup,Integer[] checkitemIds/*跟传的参数名保持一致*/){
        try {
            checkGroupService.edit(checkGroup,checkitemIds);
        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false, MessageConstant.ADD_CHECKGROUP_FAIL);
        }
        //成功情况
        return new Result(true,MessageConstant.ADD_CHECKGROUP_SUCCESS);
    }

   //删除检查组
    @RequestMapping("/deleteCheckGroup")
    public Result deleteCheckGroup(Integer id){//跟传的参数名保持一致
        try {
            checkGroupService.deleteCheckGroup(id);
        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false, MessageConstant.DELETE_CHECKGROUP_FAIL);
        }
        //成功情况
        return new Result(true,MessageConstant.DELETE_CHECKITEM_SUCCESS);
    }

    //查询所有检查组
    @RequestMapping("/findAll")
    public Result findAll(){
        try {
            List<CheckGroup> checkGroup=checkGroupService.findAll();
            //成功情况
            return new Result(true, MessageConstant.QUERY_CHECKGROUP_SUCCESS,checkGroup);
        } catch (Exception e) {
            e.printStackTrace();//失败情况
            return new Result(false,MessageConstant.QUERY_CHECKGROUP_FAIL,null);
        }
    }
}
