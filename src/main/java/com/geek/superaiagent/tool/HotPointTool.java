package com.geek.superaiagent.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HotPointTool {

    private static final String BASE_URL = "https://www.searchapi.io";

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    public HotPointTool(
            ObjectMapper objectMapper,
            @Value("${searchapi.api-key}") String apiKey) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    @Tool(description = "Fetch the current trending hot topics and news headlines from Baidu. Use this when asked about today's hot news, trending topics, what's happening now, or current events in China. Returns ranked titles with links.")
    public String hotPoint(
            @ToolParam(description = "Number of hot topics to return (1-50). Default is 10.") Integer num,
            @ToolParam(description = "Language preference: 0 for both simplified and traditional Chinese, 1 for simplified Chinese (default), 2 for traditional Chinese.") Integer ct) {
        if (apiKey == null || apiKey.isBlank()) {
            return "searchapi.api-key is not configured (set env SEARCHAPI_API_KEY or searchapi.api-key in yml)";
        }
        int numVal = num == null || num < 1 ? 10 : Math.min(num, 50);
        int ctVal = ct == null ? 1 : ct;
        try {
            String body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/search")
                            .queryParam("engine", "baidu")
                            .queryParam("q", "今日热点")
                            .queryParam("api_key", apiKey)
                            .queryParam("num", numVal)
                            .queryParam("ct", ctVal)
                            .build())
                    .retrieve()
                    .body(String.class);
            if (body == null || body.isBlank()) {
                return "empty response";
            }
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                return root.path("error").asText("unknown error");
            }
            return formatTopSearches(root, numVal);
        } catch (Exception e) {
            return "hot point search failed: " + e.getMessage();
        }
    }

    private String formatTopSearches(JsonNode root, int limit) {
        JsonNode list = root.path("top_searches");
        if (!list.isArray() || list.isEmpty()) {
            return "no top_searches in response";
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (JsonNode item : list) {
            if (count >= limit) {
                break;
            }
            sb.append(item.path("position").asInt()).append(". ");
            sb.append(item.path("query").asText("")).append('\n');
            sb.append(item.path("link").asText("")).append("\n\n");
            count++;
        }
        return sb.toString().trim();
    }
}
