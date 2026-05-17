package com.geek.superaiagent.demo.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;


import java.util.List;

@Component
public class MyMutltQueryExpenderDemo {

    public final MultiQueryExpander multiQueryExpander;


    public MyMutltQueryExpenderDemo(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        multiQueryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(builder)
                .numberOfQueries(3)
                .build();
    }

    public List<Query> expendQuery(String query){
        List<Query> queries = multiQueryExpander.expand(new Query(query));
        return queries;
    }
}
