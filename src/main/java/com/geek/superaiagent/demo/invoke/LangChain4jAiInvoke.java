package com.geek.superaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class LangChain4jAiInvoke {

    public static void main(String[] args) {
        QwenChatModel QwenChanModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-plus")
                .build();

        String chat = QwenChanModel.chat("你是谁？");
        System.out.println(chat);
    }
}
