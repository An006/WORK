package com.itheima.test;


import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class jsaperreportsTest {

    //基于javabean数据
    @Test
    public void test1() throws JRException {
        String jrxmlPath =
                "D:\\WORK\\chuanzijiankang\\JasperReportsDemo\\src\\main\\resources\\Demo2.jrxml";
        String jasperPath =
                "D:\\WORK\\chuanzijiankang\\JasperReportsDemo\\src\\main\\resources\\Demo2.jasper";

        //编译模板
        JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);

        //构造数据
        Map paramters = new HashMap();
        paramters.put("Company","itcast");
        //javabean数据源填充
        List<Map> list = new ArrayList();
        Map map1 = new HashMap();
        map1.put("name","xiaoming");
        map1.put("code","beijing");
        map1.put("sex","女");
        map1.put("age","18");
        Map map2 = new HashMap();
        map2.put("name","xiaoming");
        map2.put("code","beijing1");
        map2.put("sex","男");
        map2.put("age","18");

        list.add(map1);
        list.add(map2);

        //填充数据
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(jasperPath,
                        paramters,
                        new JRBeanCollectionDataSource(list));

        //输出文件
        String pdfPath = "D:\\test.pdf";
        JasperExportManager.exportReportToPdfFile(jasperPrint,pdfPath);
    }


    //基于jdbc填充数据
    @Test
    public void test2() throws JRException, ClassNotFoundException, SQLException {
         /*       Class.forName("com.mysql.cj.jdbc.Driver");
        //Connection mrsan = DriverManager.getConnection();

        String jrxmlPath =
                "D:\\WORK\\chuanzijiankang\\JasperReportsDemo\\src\\main\\resources\\Demo1.jrxml";
        String jasperPath =
                "D:\\WORK\\chuanzijiankang\\JasperReportsDemo\\src\\main\\resources\\Demo1.jasper";


        //编译模板
        JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);

        //为模板准备数据，

        Map paramters = new HashMap<String,String>();
        paramters.put("Company","比例比例");

        //填充数据
        JasperPrint jasperPrint;
        jasperPrint = (JasperPrint) JasperFillManager.fillReport(jasperPath,paramters,mrsan);
        //输出文件
        String pdfPath = "D:\\test.pdf";
        JasperExportManager.exportReportToPdfFile(jasperPrint,pdfPath);*/
    }
}
