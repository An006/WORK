package com.itheima.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.*;

public class PoiTest {
    //使用poi读取Excel文件中的数据
    @Test
    public void ExcelPoiRead() throws IOException {
        //加载一个文件，创建一个excel对象（工作薄）
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File("D:\\ExcelPoi.xlsx")));
        //读取文件中第一个标签页
        XSSFSheet sheetAt = excel.getSheetAt(0);
        //遍历sheet标签页，获得每一行的数据
        for (Row row : sheetAt) {
            //遍历行，获得每个单元格对象
            for (Cell cell : row) {
                System.out.println(cell);
            }
        }
        excel.close();

    }
    @Test
    public void ExcelPoi2Read() throws IOException {
        //加载一个文件，创建一个excel对象（工作薄）
        XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(new File("D:\\ExcelPoi.xlsx")));
        //读取文件中第一个标签页
        XSSFSheet sheetAt = excel.getSheetAt(0);
        //获得当前行中最后的行，从0开始
        int lastRowNum = sheetAt.getLastRowNum();

        for (int i = 0; i <= lastRowNum; i++) {
            XSSFRow row = sheetAt.getRow(i);
            //获取行最后一个单元格,从1开始
            short lastCellNum = row.getLastCellNum();
            for (int j = 0; j <lastCellNum ; j++) {
                XSSFCell cell = row.getCell(j);
                System.out.println(cell);
            }
        }
        excel.close();
    }
    //使用poi向excel文件中写入数据，并且通过输出流将创建的Excel文件保存到本地磁盘。
    @Test
    public void ExcelPoi1Write() throws IOException {
        //在内存中创建一个Excel文件（工作薄）,默认没有工作表
        XSSFWorkbook sheets = new XSSFWorkbook();
        //创建工作表
        XSSFSheet calibre = sheets.createSheet("calibre");
        //在工作表中创建行
        XSSFRow row = calibre.createRow(5);
        //在工作表中创建单元格对象
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("性别");

        for (int i = 6; i <7 ; i++) {
            XSSFRow row1 = calibre.createRow(i);
            row1.createCell(0).setCellValue("xiaogege");
            row1.createCell(1).setCellValue("81");
        }

        //创建一个输出流，将内存中的excel文件写到磁盘,,追加源文件，会被清空
        FileOutputStream fileOutputStream = new FileOutputStream(new File("D:\\ExcelPoi.xlsx"));
        sheets.write(fileOutputStream);
        fileOutputStream.flush();
        sheets.close();
    }

}
