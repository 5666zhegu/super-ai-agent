package com.geek.superaiagent.rag;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class LoveAppVectorStoreConfig {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Resource
    private MyKeyWordEnricher myKeyWordEnricher;


    //    @Bean
    VectorStore LoveApppVectorStore(EmbeddingModel dashscopeEmbeddingModel){
        SimpleVectorStore vectorStore = SimpleVectorStore.builder(dashscopeEmbeddingModel).build();
        List<Document> documents = loveAppDocumentLoader.loadMarkdown();
        documents = myKeyWordEnricher.enrichDocument(documents);
        vectorStore.doAdd(documents);
        return vectorStore;
    }





}
