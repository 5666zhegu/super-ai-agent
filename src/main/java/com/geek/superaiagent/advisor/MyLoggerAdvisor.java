//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.geek.superaiagent.advisor;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import reactor.core.publisher.Flux;
@Slf4j
public class MyLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    private final int order;

    private MyLoggerAdvisor(int order) {
        this.order = order;
    }
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        // 默认值
        private int order = 0;

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public MyLoggerAdvisor build() {
            return new MyLoggerAdvisor(this.order);
        }
    }





    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        this.logRequest(chatClientRequest);
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        this.logResponse(chatClientResponse);
        return chatClientResponse;
    }

    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        this.logRequest(chatClientRequest);
        Flux<ChatClientResponse> chatClientResponses = streamAdvisorChain.nextStream(chatClientRequest);
        return (new ChatClientMessageAggregator()).aggregateChatClientResponse(chatClientResponses, this::logResponse);
    }

    protected void logRequest(ChatClientRequest request) {
        log.info("request: {}", request.prompt().getUserMessage().getText());
    }

    protected void logResponse(ChatClientResponse chatClientResponse) {
        log.info("response: {}", chatClientResponse.chatResponse().getResult().getOutput().getText());
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public int getOrder() {
        return this.order;
    }


}
