package com.geek.superaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan("com.geek.superaiagent.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class SuperAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SuperAiAgentApplication.class, args);
    }

}
