package com.luzx.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyBatisPlusConfig
{
    /**
     * MyBatis Plus 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }


    /**
     * 全局ID配置
     */
    @Bean
    public GlobalConfig globalConfig() {
        GlobalConfig config = new GlobalConfig();
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();

        // 设置主键策略
        dbConfig.setIdType(IdType.ASSIGN_ID);  // 雪花算法
        // 或
        // dbConfig.setIdType(IdType.AUTO);     // 数据库自增

        config.setDbConfig(dbConfig);
        return config;
    }

}
