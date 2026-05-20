package com.geek.aisearchimagemcpserver;

import com.geek.aisearchimagemcpserver.Tool.ImageSearchTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AiSearchImageMcpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiSearchImageMcpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider weatherTools(ImageSearchTool imageSearchTool) {
        return MethodToolCallbackProvider.builder().toolObjects(imageSearchTool).build();
    }
}
