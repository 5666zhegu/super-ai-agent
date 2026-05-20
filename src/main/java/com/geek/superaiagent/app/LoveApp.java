package com.geek.superaiagent.app;

import com.geek.superaiagent.advisor.MyLoggerAdvisor;
import com.geek.superaiagent.advisor.SafeGuardAdvisor;
import com.geek.superaiagent.chatMemory.FileBaseMemory;
import com.geek.superaiagent.rag.LoveAppRagCustomAdvisorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import static com.geek.superaiagent.constant.SystemConstant.*;

@Component
@Slf4j
public class LoveApp {

    @Autowired
    private VectorStore PgVectorVectorStore;

    @Autowired
    private ToolCallback[] allTool;

    @Autowired(required = false)
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    private final ChatClient chatClient;

    private static final List<String> sensitiveWords = SENSITIVE_WORDS;
    /**
     * 初始化ChatClicent
     * @param dashscopeChatModel 聊天模型
     */

    public LoveApp(ChatModel dashscopeChatModel,
                   @Value("classpath:SystemPromptTemplate") Resource systemResource
                   ) {

        this.chatClient = createClient(systemResource,dashscopeChatModel);


    }

    private ChatClient createClient(Resource systemResource, ChatModel dashscopeChatModel){
        String Base_dir = System.getProperty("user.dir") + "/tmp/chat-memory";
        FileBaseMemory chatMemory = new FileBaseMemory(Base_dir);

        PromptTemplate promptTemplate = new PromptTemplate(systemResource);
        Map<String, Object> params = Map.of("userName", "者古");
        String systemPrompt = promptTemplate.render(params);

        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        new SafeGuardAdvisor(sensitiveWords,FAIL_RESPONSE,0),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        MyLoggerAdvisor.builder().build()
                        // new ReReadingAdvisor().withOrder(0)

                )
                .build();
        return chatClient;
    }

    /**
     * AI基础聊天（支持多轮基础对话）
     *
     * @param message          用户消息
     * @param conversationId   会话ID
     * @return
     */
    public String doChat(String message ,String conversationId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();
        String contect = chatResponse.getResult().getOutput().getText();
        return contect;


    }

    record LoveReport(String title, List<String> suggestion){
    }
    public LoveReport doChatWithReport(String message ,String conversationId){
        LoveReport loveReport = chatClient.prompt()
                .system(DO_CHAT_REPORT_DEFAULT_PROMPT)
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .entity(LoveReport.class);
        return loveReport;

    }

    public String doChatWithRag(String conversationId,String message){

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .advisors(LoveAppRagCustomAdvisorFactory.createCustomAdvisor(PgVectorVectorStore,"单身"))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        return text;
    }


    public String doChatWithTool(String conversationId,String message){

        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .toolCallbacks(allTool)
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        return text;
    }

    public String doChatWithMcp(String conversationId,String message){

        if (toolCallbackProvider == null) {
            String errorMsg = "MCP ToolCallbackProvider is null — MCP client auto-configuration did not create the bean. Check: 1) spring-ai-starter-mcp-client is on classpath, 2) spring.ai.mcp.client.enabled=true, 3) spring.ai.mcp.client.type=SYNC, 4) mcp-servers.json is valid, 5) npx/npm and node are installed and accessible.";
            log.error(errorMsg);
            return errorMsg;
        }

        // 诊断：打印 MCP 提供的工具列表
        var mcpTools = toolCallbackProvider.getToolCallbacks();
        log.info("MCP ToolCallbackProvider tools count: {}", mcpTools.length);
        for (var tool : mcpTools) {
            log.info("MCP tool: {}", tool.getToolDefinition().name());
        }
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .toolCallbacks(toolCallbackProvider)
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        return text;
    }


}