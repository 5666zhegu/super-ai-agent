package com.geek.superaiagent.rag;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter.*;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

@Component
public class LoveAppRagCustomAdvisorFactory {

    public static Advisor createCustomAdvisor(VectorStore vectorStore,String status){
        FilterExpressionBuilder fb = new FilterExpressionBuilder();
        Expression expression = fb.eq("status", status).build();
        VectorStoreDocumentRetriever documentRetriever = VectorStoreDocumentRetriever.builder()
                .vectorStore(vectorStore)
                .similarityThreshold(0.6)
                .topK(7)
                .filterExpression(expression)
                .build();


        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(documentRetriever)
                .build();
            }

    }
