package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;

import com.itheima.service.MemberService;
import com.itheima.service.OrderService;
import com.itheima.service.ReportService;
import com.itheima.service.SetmealService;
import com.itheima.utils.DateUtils;


import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Reference
    private MemberService memberService;
    @Reference
    private SetmealService setmealService;
    @Reference
    private OrderService orderService;
    @Reference
    private ReportService reportService;

    //会员数量折现图
    @RequestMapping("/getMemberReport")
    public Result getMemberReport(){
        //使用模拟数据测试对象格式是否能转为echarts所需的数据格式
        HashMap<String, Object> Map = new HashMap<>();
        List<String> months = new ArrayList<>();
        //计算过去一年的的12个月
        Calendar calendar = Calendar.getInstance();//获得日历对象，模拟时间就是当前时间
        calendar.add(Calendar.MONTH,-12);//按月往前推12个月
        for (int i = 0; i <= 12 ; i++) {
            calendar.add(Calendar.MONTH,1);
            Date time1 = calendar.getTime();
            try {
                String s = DateUtils.parseDate2String(time1, "yyyy.MM");
                months.add(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        List<Integer> memberCount=new ArrayList<>();
        memberCount=memberService.findCountMemberByMonth(months);

        Map.put("months",months);
        Map.put("memberCount",memberCount);
        return new Result(true, MessageConstant.GET_MEMBER_NUMBER_REPORT_SUCCESS,Map);
    }


    //套餐报表
    @RequestMapping("/getSetmealReport")
    public Result getSetmealReport(){
        //使用模拟数据测试使用什么样的java对象转换为饼形图所需的json数据格式

        try {
            List<Map<String, Object>> setmealCount = setmealService.findSetmealCount();
            Map map=new HashMap<String,Object>();
            List<String> setmealNames=new ArrayList<>();
            for (Map<String, Object> stringObjectMap : setmealCount) {
                setmealNames.add((String) stringObjectMap.get("name"));
                map.put("setmealNames", setmealNames);
                map.put("setmealCount", setmealCount);
            }
            return new Result(true,MessageConstant.GET_SETMEAL_COUNT_REPORT_SUCCESS,map);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,MessageConstant.GET_SETMEAL_COUNT_REPORT_FAIL);
        }
    }

    @RequestMapping("/getBusinessReportData")
    public Result getBusinessReportData(){
        try {
            Map<String, Object> reportData=reportService.getBusinessReportData();
            return new Result(true, MessageConstant.GET_BUSINESS_REPORT_SUCCESS, reportData);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_FAIL,null);
        }
    }
    @RequestMapping("/exportBusinessReport")
    public Result exportBusinessReport(HttpServletRequest request, HttpServletResponse response){
        try {
            Map<String, Object> result=reportService.getBusinessReportData();
            String reportDate = (String) result.get("reportDate");
            Integer todayNewMember = (Integer) result.get("todayNewMember");
            Integer totalMember = (Integer) result.get("totalMember");
            Integer thisWeekNewMember = (Integer) result.get("thisWeekNewMember");
            Integer thisMonthNewMember = (Integer) result.get("thisMonthNewMember");
            Integer todayOrderNumber = (Integer) result.get("todayOrderNumber");
            Integer thisWeekOrderNumber = (Integer) result.get("thisWeekOrderNumber");
            Integer thisMonthOrderNumber = (Integer) result.get("thisMonthOrderNumber");
            Integer todayVisitsNumber = (Integer) result.get("todayVisitsNumber");
            Integer thisWeekVisitsNumber = (Integer) result.get("thisWeekVisitsNumber");
            Integer thisMonthVisitsNumber = (Integer) result.get("thisMonthVisitsNumber");
            List<Map<String,Object>> hotSetmeal = (List<Map<String, Object>>) result.get("hotSetmeal");



            //动态的来获得当前这个模板文件在磁盘上的绝对路径
            String template = request.getSession().getServletContext().getRealPath("template")
                    + File.separator/*这个是根据操作系统自动的确定分隔符*/
                    + "report_template.xlsx";

            //基于提供的模板Excel模板文件在内存中创建一个Excel表格对象
            XSSFWorkbook sheets = new XSSFWorkbook(new FileInputStream(new File(template)));
            //直接读取工作表
            XSSFSheet sheetAt = sheets.getSheetAt(0);
            //获得第三行,第6列，填充日期
            XSSFRow row1 = sheetAt.getRow(2);
            row1.getCell(5).setCellValue(reportDate);

            XSSFRow row2 = sheetAt.getRow(4);
            row2.getCell(5).setCellValue(todayNewMember);
            row2.getCell(7).setCellValue(totalMember);

            XSSFRow row3 = sheetAt.getRow(5);
            row3.getCell(5).setCellValue(thisWeekNewMember);
            row3.getCell(7).setCellValue(thisMonthNewMember);


            XSSFRow row4 = sheetAt.getRow(7);
            row4.getCell(5).setCellValue(todayOrderNumber);
            row4.getCell(7).setCellValue(todayVisitsNumber);

            XSSFRow row5 = sheetAt.getRow(8);
            row5.getCell(5).setCellValue(thisWeekOrderNumber);
            row5.getCell(7).setCellValue(thisWeekVisitsNumber);

            XSSFRow row6 = sheetAt.getRow(9);
            row6.getCell(5).setCellValue(thisMonthOrderNumber);
            row6.getCell(7).setCellValue(thisMonthVisitsNumber);

            for (Map<String, Object> map : hotSetmeal) {
                int i=12;
                XSSFRow row = sheetAt.getRow(i);
                String name = (String) map.get("name");
                Long setmeal_count= (Long) map.get("setmeal_count");
                BigDecimal proportion = (BigDecimal) map.get("proportion");
                row.getCell(4).setCellValue(name);
                row.getCell(5).setCellValue(setmeal_count);
                row.getCell(6).setCellValue(proportion.doubleValue());
                i=i+1;
            }

            //使用输出流，进行表格下载,基于浏览器作为客户端下载
            ServletOutputStream outputStream = response.getOutputStream();
            //需要让客户端知道，写出来的文件是什么样的类型
            response.setContentType("application/vnd.ms-excel");//向客户端声明写回的文件，代表的是excel文件
            response.setHeader("content-Disposition","attachment;filename=report.xlsx");//下载的类型，以附件的形式进行下载

            sheets.write(outputStream);
            outputStream.flush();

            outputStream.close();
            sheets.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }


    //导出运营数据为pdf文件并提供客户端下载
    @RequestMapping("/exportBusinessReportForPDF")
    public Result exportBusinessReportForPDF(HttpServletRequest request, HttpServletResponse response){
        try{
            Map<String, Object> result=reportService.getBusinessReportData();
            List<Map<String,Object>> hotSetmeal = (List<Map<String, Object>>) result.get("hotSetmeal");

            //动态获得pdf模板文件的绝对路径
            String jrxmlPath =request.getSession().getServletContext().getRealPath("template")
                    + File.separator/*这个是根据操作系统自动的确定分隔符*/
                    +"health_business3.jrxml";

            String jasperPath = request.getSession().getServletContext().getRealPath("template")
                    + File.separator/*这个是根据操作系统自动的确定分隔符*/
                    +"health_business3.jasper";

            //编译模板
            JasperCompileManager.compileReportToFile(jrxmlPath,jasperPath);
            //javabean形式填充数据
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperPath, result,new JRBeanCollectionDataSource(hotSetmeal));

            //使用输出流，进行表格下载,基于浏览器作为客户端下载
            ServletOutputStream outputStream = response.getOutputStream();
            //需要让客户端知道，写出来的文件是什么样的类型
            response.setContentType("application/pdf");//向客户端声明写回的文件，代表的是pdf文件
            response.setHeader("content-Disposition","attachment;filename=report.pdf");//下载的类型，以附件的形式进行下载
            JasperExportManager.exportReportToPdfStream(jasperPrint,outputStream);
            outputStream.flush();
            outputStream.close();
            return null;
        }catch (Exception e){
            e.printStackTrace();
            e.printStackTrace();
            return new Result(true,MessageConstant.GET_BUSINESS_REPORT_FAIL);
        }
    }

}
