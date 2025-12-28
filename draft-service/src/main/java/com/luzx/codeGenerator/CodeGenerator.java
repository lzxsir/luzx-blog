package com.luzx.codeGenerator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator
{

    public static void main(String[] args) {
        // 数据库连接配置
        String url = "jdbc:mysql://rm-bp118y002bj7sr0wm6o.mysql.rds.aliyuncs.com:3306/luzx_blog?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "Lzx@123456";
        // 代码生成
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder ->
                {
                    builder
                            .author("昱")        // 设置作者
                            .outputDir(System.getProperty("user.dir") + "/src/main/java") // 输出目录
                            .disableOpenDir()         // 生成后不打开文件夹
                            .commentDate("yyyy-MM-dd HH:mm:ss") // 注释日期格式
                            ;          // 覆盖已有文件
                })
                .packageConfig(builder -> {
                    builder.parent("com.luzx")      // 父包名
//                            .moduleName("demo")            // 模块名
                            .entity("model")              // 实体类包名
                            .mapper("mapper")              // Mapper包名
                            .service("service")            // Service包名
                            .serviceImpl("service.impl")   // ServiceImpl包名
                            .controller("controller")      // Controller包名
                            .xml("mapper")                 // XML文件包名
                            .pathInfo(Collections.singletonMap(
                                    OutputFile.xml,
                                    System.getProperty("user.dir") + "/src/main/resources/mapper"
                            )); // 设置XML路径
                })
                .strategyConfig(builder -> {
                    builder
                            .addInclude("sys_user") // 要生成的表
                            .addTablePrefix("t_", "sys_")  // 表前缀过滤

                            .entityBuilder()               // 实体类配置
                            .enableFileOverride()
                            .enableLombok()            // 启用Lombok
                            .enableChainModel()        // 链式模型
                            .enableTableFieldAnnotation() // 字段注解


                            .mapperBuilder()               // Mapper配置
                            .enableFileOverride()
//                            .enableBaseResultMap()     // 生成ResultMap
//                            .enableBaseColumnList()    // 生成ColumnList


                            .controllerBuilder()           // Controller配置
                            .enableFileOverride()
                            .enableRestStyle()         // REST风格
                            .enableHyphenStyle()       // 驼峰转连字符


                            .serviceBuilder()              // Service配置
                            .enableFileOverride()
                            .formatServiceFileName("%sService") // Service文件名格式
                            .formatServiceImplFileName("%sServiceImpl"); // ServiceImpl文件名格式
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎
                .execute();
    }

}
