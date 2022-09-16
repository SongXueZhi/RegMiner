package com.fudan.annotation.platform.backend;


import cn.dev33.satoken.SaManager;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@MapperScan("com.fudan.annotation.platform.backend.dao")
@ComponentScan(basePackages = {"com.fudan.annotation.platform.backend.*"})
@SpringBootApplication
@EnableAsync
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("启动成功：Sa-Token配置如下：" + SaManager.getConfig());
    }

}
