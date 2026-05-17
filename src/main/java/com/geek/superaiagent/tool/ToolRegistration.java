package com.geek.superaiagent.tool;

import jakarta.annotation.Resource;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ToolRegistration {

    @Resource
    private BaiduSearchTool baiduSearchTool;
    @Resource
    private FileOperationTool fileOperationTool;
    @Resource
    private HotPointTool hotPointTool;
    @Resource
    private PtfOperationTool ptfOperationTool;
    @Resource
    private ResourceDownTool resourceDownTool;
    @Resource
    private TerminalOperationTool terminalOperationTool;
    @Resource
    private WebScrapingTool webScrapingTool;


    @Bean
    public ToolCallback[] allTool(){
        return ToolCallbacks.from(baiduSearchTool
        ,fileOperationTool
        ,hotPointTool
        ,ptfOperationTool
        ,resourceDownTool
        ,terminalOperationTool
        ,webScrapingTool);
    }
}
