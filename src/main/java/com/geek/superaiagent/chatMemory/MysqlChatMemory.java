package com.geek.superaiagent.chatMemory;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geek.superaiagent.entity.AiChatMemory;
import com.geek.superaiagent.service.AiChatMemoryService;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class MysqlChatMemory implements ChatMemory {

    private final AiChatMemoryService service;
    private static final int MAX_HISTORY = 1; //  最多保留10条对话（可改）

    public MysqlChatMemory(AiChatMemoryService service) {
        this.service = service;
    }

    @Override
    public void add(String conversationId, Message message) {
        if (message instanceof UserMessage userMsg) {
            AiChatMemory m = new AiChatMemory();
            m.setConversationId(conversationId);
            m.setUserPrompt(userMsg.getText());
            m.setAiResponse("");
            service.save(m);
        }

        if (message instanceof AssistantMessage aiMsg) {
            AiChatMemory last = service.getOne(
                    Wrappers.lambdaQuery(AiChatMemory.class)
                            .eq(AiChatMemory::getConversationId, conversationId)
                            .orderByDesc(AiChatMemory::getId)
                            .last("LIMIT 1")
            );

            if (last != null) {
                last.setAiResponse(aiMsg.getText());
                service.updateById(last);
            }
        }
    }

    @Override
    public void add(String conversationId, List<Message> messages) {
        for (Message msg : messages) add(conversationId, msg);
    }


    @Override
    public List<Message> get(String conversationId) {
        List<AiChatMemory> list = service.list(
                Wrappers.lambdaQuery(AiChatMemory.class)
                        .eq(AiChatMemory::getConversationId, conversationId)
                        .orderByAsc(AiChatMemory::getId)
        );


        int start = Math.max(0, list.size() - MAX_HISTORY);
        List<AiChatMemory> finalList = list.subList(start, list.size());

        List<Message> messages = new ArrayList<>();
        for (AiChatMemory m : finalList) {
            messages.add(new UserMessage(m.getUserPrompt()));
            messages.add(new AssistantMessage(m.getAiResponse()));
        }
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        service.remove(
                Wrappers.lambdaQuery(AiChatMemory.class)
                        .eq(AiChatMemory::getConversationId, conversationId)
        );
    }
}