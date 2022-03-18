package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetmealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetmealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = SetmealService.class)
@Transactional
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealDao setmealDao;
    @Autowired
    private JedisPool jedisPool;
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;
    @Value("${out_put_path}")//通过，value注入，前提spring配置了class文件
    private String outPutPath;

    //加入套餐服务
    public void add(Setmeal setmeal, Integer[] checkGroupIds) {
        //需要操作两个表
        //一个是setmeal的表
        setmealDao.add(setmeal);
        //插入后通过selectkey已经把id取出
        setSetmealAndCheckGroup(setmeal.getId(), checkGroupIds);
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,setmeal.getImg());

        //当添加套餐后，需要重新生成静态页面（套餐列表页面/套餐详情页面）
        generateMobileStaticHtml();
    }

    //生成当前方法所需的静态页面
    public void generateMobileStaticHtml(){
        //在生成静态页面之前需要查询数据
        List<Setmeal> setmeal = setmealDao.getSetmeal();
        //需要生成套餐列表静态页面
        gengerateMobileSetmealListStaticHtml(setmeal);
        //需要生成套餐详情静态页面
        gengerateMobileSetmealDetailStaticHtml(setmeal);
    }
    //需要生成套餐列表静态页面
    public void gengerateMobileSetmealListStaticHtml(List<Setmeal> setmeal){
        Map<String,List> map = new HashMap<>();
        map.put("setmealList",setmeal);
        //为模板提供数据
        gengerateHtml("mobile_setmeal.ftl","mobile_setmeal.html",map);
    }

    //需要生成套餐详情静态页面(可能有多个)
    public void gengerateMobileSetmealDetailStaticHtml(List<Setmeal> setmeal){
        for (Setmeal setmeal1 : setmeal) {
            Map<String,Setmeal> map = new HashMap<>();
            map.put("setmeal",setmealDao.findById(setmeal1.getId()));
            gengerateHtml("mobile_setmeal_detail.ftl","setmeal_detail_"+setmeal1.getId()+".html",map);
        }


    }

    //用于生成静态页面
    public void gengerateHtml(String template,String htmlPageName,Map map ){//使用那个模板，生成的静态文件名，对应的map所需要的数据
        Configuration configuration = freeMarkerConfigurer.getConfiguration();//已经配置好了
        Writer writer=null;
        try {
            Template template1 = configuration.getTemplate(template);
            //构造输出流
            writer=new FileWriter(new File(outPutPath+"/"+htmlPageName));//这里如果不new File的话，那么他要么继续写源文件上的，要么就会重新创建，所以每生成一个必须new 一个，修改的时候也是new一个。
            template1.process(map,writer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
    }


    //分页查询
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();
        //基于mybatis的分页查询
        PageHelper.startPage(currentPage,pageSize);
        Page<Setmeal> page= setmealDao.selectByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());
    }

    //获取所有套餐
    public List<Setmeal> getSetmeal() {
        return setmealDao.getSetmeal();
    }

    //删除套餐
    public void deleteSetmeal(Integer id) {
        //先删除关系表
        setmealDao.deleteAssoicatio(id);
        //再删除套餐表
        setmealDao.deleteSetmeal(id);

        //需要完善，如果对套餐进行了删除，那么就要删除静态详情页面和静态套餐列表，重新生成，需要思考

    }

    //查找套餐
    public Setmeal findById(Integer id) {
        return setmealDao.findById(id);
    }

    //查询套餐预约占比
    public List<Map<String, Object>> findSetmealCount() {
        return setmealDao.findSetmealCount();
    }

    private void setSetmealAndCheckGroup(Integer id, Integer[] checkGroupIds) {
        if(checkGroupIds!=null && checkGroupIds.length>0) {
            //另一个是setmel和checkgroup表
            for (Integer checkGroupId : checkGroupIds) {
                Map<String, Integer> map = new HashMap<>();
                map.put("setmealId", id);
                map.put("checkgroupId", checkGroupId);
                setmealDao.setSetmealAndCheckGroup(map);
            }
        }

    }
}
