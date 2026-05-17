package com.geek.superaiagent.tool;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class WebScrapingTool {

    @Tool(description = "Fetch and return the full HTML content of a web page. Use this when asked to open a website, check a webpage, look at an online page, or read content from a URL.")
    public String doWebScrap(@ToolParam(description = "The complete URL of the web page to fetch, starting with http:// or https://.")String url){
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.html();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
