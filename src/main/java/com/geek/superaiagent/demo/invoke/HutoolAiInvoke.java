package com.geek.superaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil; // 用 Hutool 自带的
import java.util.HashMap;
import java.util.Map;

public class HutoolAiInvoke {

    // 你的阿里云灵积 API Key
    private static final String API_KEY = TestApiKey.API_KEY;

    public static void main(String[] args) {
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 组装请求参数（和你curl完全一致）
        Map<String, Object> body = new HashMap<>();
        body.put("model", "qwen-plus");

        Map<String, Object> input = new HashMap<>();
        Map<String, String> msg1 = Map.of("role", "system", "content", "You are a helpful assistant.");
        Map<String, String> msg2 = Map.of("role", "user", "content", "你是谁？");
        input.put("messages", new Object[]{msg1, msg2});

        Map<String, Object> params = Map.of("result_format", "message");

        body.put("input", input);
        body.put("parameters", params);

        // 发送请求（Hutool 原生）
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + API_KEY)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(body)) // 这里用 Hutool 自带
                .execute();

        // 输出结果
        System.out.println("状态码：" + response.getStatus());
        System.out.println("返回结果：" + response.body());
    }
}