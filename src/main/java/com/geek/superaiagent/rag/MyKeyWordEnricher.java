package com.geek.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MyKeyWordEnricher {

    private final KeywordMetadataEnricher enricher;

    public MyKeyWordEnricher(ChatModel dashscopeChatModel) {
        enricher = KeywordMetadataEnricher.builder(dashscopeChatModel)
                .keywordCount(3)
                .build();
    }

    public List<Document> enrichDocument(List<Document> documents){

        return enricher.apply(documents);
    }
}
