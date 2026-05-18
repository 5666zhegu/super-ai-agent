package com.geek.superaiagent.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 调用 SearchAPI.io 的 Baidu 引擎实时搜索，见 https://www.searchapi.io/docs/baidu
 */
@Component
public class BaiduSearchTool {

    private static final String BASE_URL = "https://www.searchapi.io";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public BaiduSearchTool(
            ObjectMapper objectMapper,
            @Value("${searchapi.api-key}") String apiKey) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        // 固定访问 SearchAPI.io 域名，具体路径在请求里拼
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    @Tool(description = "Search the web using Baidu for real-time information. Best for queries about Chinese websites, local recommendations (restaurants, attractions, shops), current events, prices, and factual lookups. Returns titles, links, and snippets.")
    public String baiduSearch(
            @ToolParam(description = "Search keywords in Chinese or English. Include location, category, or time for better results. Example: '广州天环广场附近好吃的餐厅'") String query,
            @ToolParam(description = "Result page number starting from 1. Default is 1.") Integer page,
            @ToolParam(description = "Number of results to return (1-50). Default is 10.") Integer num,
            @ToolParam(description = "Language filter: 0 for mixed Chinese, 1 for simplified Chinese only, 2 for traditional Chinese only. Leave empty for default.") Integer ct) {
        // 密钥来自配置/环境变量，未配置则直接提示，避免无意义请求
        if (apiKey == null || apiKey.isBlank()) {
            return "searchapi.api-key is not configured (set env SEARCHAPI_API_KEY or searchapi.api-key in yml)";
        }
        if (query == null || query.isBlank()) {
            return "query empty";
        }
        // 与官方文档一致：page 默认 1；num 默认 10，且不超过单页上限 50
        int pageVal = page == null || page < 1 ? 1 : page;
        int numVal = num == null || num < 1 ? 10 : Math.min(num, 50);
        try {
            // GET /api/v1/search：engine=baidu + 鉴权 api_key，其余为查询与分页
            String body = restClient.get()
                    .uri(uriBuilder -> {
                        var b = uriBuilder.path("/api/v1/search")
                                .queryParam("engine", "baidu")
                                .queryParam("q", query)
                                .queryParam("api_key", apiKey)
                                .queryParam("page", pageVal)
                                .queryParam("num", numVal);
                        if (ct != null) {
                            b.queryParam("ct", ct);
                        }
                        return b.build();
                    })
                    .retrieve()
                    .body(String.class);
            if (body == null || body.isBlank()) {
                return "empty response";
            }
            JsonNode root = objectMapper.readTree(body);
            // 接口错误时 JSON 里常有 error 字段，优先返回给模型
            if (root.has("error")) {
                return root.path("error").asText("unknown error");
            }
            return formatOrganic(root);
        } catch (Exception e) {
            return "search failed: " + e.getMessage();
        }
    }

    /** 只抽取自然结果列表，拼成「标题/链接/摘要」便于模型阅读 */
    private String formatOrganic(JsonNode root) {
        JsonNode list = root.path("organic_results");
        if (!list.isArray() || list.isEmpty()) {
            return "no organic_results in response";
        }
        StringBuilder sb = new StringBuilder();
        for (JsonNode item : list) {
            sb.append(item.path("title").asText("")).append('\n');
            sb.append(item.path("link").asText("")).append('\n');
            sb.append(item.path("snippet").asText("")).append("\n\n");
        }
        return sb.toString().trim();
    }
}
