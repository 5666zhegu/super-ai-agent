package com.geek.aisearchimagemcpserver.Tool;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ImageSearchTool {

    @Value("${pexels.api-key}")
    private String apiKey;

    private static final int DEFAULT_PER_PAGE = 20;
    private static final String DEFAULT_LOCALE = "zh-CN";

    @Tool(description = "Search photos from Pexels")
    public String searchImage(
            @ToolParam(description = "Search query, e.g. Nature, Tigers, People") String query) {

        String url = "https://api.pexels.com/v1/search?query="
                + URLEncoder.encode(query, StandardCharsets.UTF_8)
                + "&per_page=" + DEFAULT_PER_PAGE
                + "&locale=" + DEFAULT_LOCALE;

        HttpResponse response = HttpRequest.get(url)
                .header("Authorization", apiKey)
                .execute();

        JSONObject json = new JSONObject(response.body());
        JSONArray photos = json.getJSONArray("photos");
        return photos.stream()
                .map(JSONObject.class::cast)
                .map(p -> p.getStr("url"))
                .collect(Collectors.joining("\n"));
    }
}
