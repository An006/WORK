package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.entity.Result;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 检查项管理
 */
@ComponentScan
@RestController //与Controller就是包含了这个Controller同时加上responseBody(直接让方法返回java对象，让他自动的转为json，通过输出流的方式往会写);
@RequestMapping("/checkitem")//请求路径
public class CheckItemController {

            @Reference  //作用是到zookeeper注册中心去查找一个叫checkitemService的这个服务。
            private CheckItemService checkItemService;

            //新增检查项
            @RequestMapping("/add")
            public Result add(@RequestBody CheckItem checkItem /*去解析提交过来的json数据，把他封装为checkitem对象*/){
                //不知道是否添加成功或者失败,通过trycatch，来判断调用是否成功或者失败。
                try {
                    //服务调用成功
                    checkItemService.add(checkItem);
                } catch (Exception e) {
                    //服务调用失败
                    e.printStackTrace();
                    return new Result(false, MessageConstant.ADD_CHECKITEM_FAIL);
                }
                return new Result(true, MessageConstant.ADD_CHECKITEM_SUCCESS);
            }

            //分页查询检查项
            @RequestMapping("/findPage")
            public PageResult findPage(@RequestBody QueryPageBean queryPageBean){
                PageResult pageResult = checkItemService.pageQuery(queryPageBean);
                return  pageResult;

            }

            //删除检查项
            @PreAuthorize("hasAuthority('CHECKITEM_DELETE')")//如果没有权限就要有权限不足的提示
            @RequestMapping("/delete")
            public Result delete(Integer id){
                try {
                    //服务调用成功
                    checkItemService.deleteById(id);
                } catch (Exception e) {
                    //服务调用失败
                    e.printStackTrace();
                    return new Result(false,MessageConstant.DELETE_CHECKITEM_FAIL);
                }
                return new Result(true, MessageConstant.DELETE_CHECKITEM_SUCCESS );
            }

            //查找检查项
            @RequestMapping("/findById")
            public Result findById(Integer id){
                try {
                    //服务调用成功
                    CheckItem checkItem=checkItemService.findById(id);
                    return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
                } catch (Exception e) {
                    //服务调用失败
                    e.printStackTrace();
                    return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL );
                }
            }

            //编辑检查项
            @RequestMapping("/edit")
            public Result edit(@RequestBody CheckItem checkItem){
                try {
                    //服务调用成功
                    checkItemService.edit(checkItem);
                } catch (Exception e) {
                    //服务调用失败
                    e.printStackTrace();
                    return new Result(false,MessageConstant.EDIT_CHECKITEM_FAIL );
                }
                return new Result(true, MessageConstant.EDIT_CHECKITEM_SUCCESS);
            }

            //编辑检查项
            @RequestMapping("/findAll")
            public Result findAll(){
                try {
                    //服务调用成功
                    List<CheckItem> checkItem=checkItemService.findAll();
                    return new Result(true,MessageConstant.QUERY_CHECKITEM_SUCCESS,checkItem);
                } catch (Exception e) {
                    //服务调用失败
                    e.printStackTrace();
                    return new Result(false, MessageConstant.QUERY_CHECKITEM_FAIL );
                }
            }


}
