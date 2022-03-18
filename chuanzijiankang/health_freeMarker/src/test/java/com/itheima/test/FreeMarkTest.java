package com.itheima.test;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FreeMarkTest {
    public static void main(String[] args) throws IOException, TemplateException {
        //创建freemark的配置对象
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        //设置环境信息
        //设置模板文件所在的目录
        configuration.setDirectoryForTemplateLoading(new File("d:\\ftl"));
        //设置字符集
        configuration.setDefaultEncoding("utf-8");
        //加载模板
        Template template = configuration.getTemplate("test.ftl");
        //创建数据模型,通常是map构造
        Map<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("name","张三");
        stringStringHashMap.put("message","萨比");
        //创建Writer对象
        Writer fileWriter = new FileWriter("d:\\ftl\\test.html");
        //通过输出流，和模板，对象，输出
        template.process(stringStringHashMap,fileWriter);
        //关闭writer对象
        fileWriter.close();

    }
}
