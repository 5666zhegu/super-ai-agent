package com.geek.superaiagent.app;

import com.geek.superaiagent.advisor.MyLoggerAdvisor;
import com.geek.superaiagent.advisor.SafeGuardAdvisor;
import com.geek.superaiagent.chatMemory.MysqlChatMemory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import static com.geek.superaiagent.constant.SystemConstant.*;

@Component
@Slf4j
public class LoveApp {
    private final VectorStore LoveApppVectorStore;

    private final Advisor LoveAppRagCloudAdvisor;

    private final ChatClient chatClient;

    private static final List<String> sensitiveWords = SENSITIVE_WORDS;
    /**
     * 初始化ChatClicent
     * @param dashscopeChatModel 聊天模型
     */

    public LoveApp(ChatModel dashscopeChatModel, MysqlChatMemory chatMemory, @Value("classpath:SystemPromptTemplate") Resource systemResource, VectorStore LoveApppVectorStore, Advisor LoveAppRagCloudAdvisor) {
        // String Base_dir = System.getProperty("user.dir") + "/tmp/chat-memory";

        // FileBaseMemory chatMemory = new FileBaseMemory(Base_dir);

        PromptTemplate promptTemplate = new PromptTemplate(systemResource);
        Map<String, Object> params = Map.of("userName", "者古");
        String systemPrompt = promptTemplate.render(params);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemPrompt)
                .defaultAdvisors(
                        new SafeGuardAdvisor(sensitiveWords,FAIL_RESPONSE,0),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        MyLoggerAdvisor.builder().build()
                        // new ReReadingAdvisor().withOrder(0)

                )
                .build();
        this.LoveApppVectorStore = LoveApppVectorStore;
        this.LoveAppRagCloudAdvisor = LoveAppRagCloudAdvisor;



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
                .advisors(LoveAppRagCloudAdvisor)
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        return text;
    }


}