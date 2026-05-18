package com.geek.superaiagent.tool;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.geek.superaiagent.constant.FileConstant.FILE_BASE_DIR;

@Component
public class ResourceDownTool {

    private final String baseDir = FILE_BASE_DIR + "/download";

    public ResourceDownTool() {
        new File(baseDir).mkdirs();
    }

    /** 相对路径落在 tmp/file 下，禁止 .. 跳出沙箱 */
    private File fileOf(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename empty");
        }
        if (filename.contains("..")) {
            throw new IllegalArgumentException("invalid filename");
        }
        return new File(baseDir, filename);
    }

    @Tool(description = "Download a file from a URL and save it to the workspace. Use this when asked to download images, wallpapers, documents, or any online resource to a local file.")
    public String download(
            @ToolParam(description = "The complete URL of the resource to download, starting with http:// or https://.") String url,
            @ToolParam(description = "Local filename to save as. For images, include the proper extension (e.g. 'wallpaper.jpg', 'photo.png').") String filename) {
        if (url == null || url.isBlank()) {
            return "url empty";
        }
        try {
            File dest = fileOf(filename);
            FileUtil.mkParentDirs(dest);
            // Hutool 拉取远程流并写入本地文件
            HttpUtil.downloadFile(url, dest);
            return "ok";
        } catch (RuntimeException e) {
            return "Error download: " + e.getMessage();
        }
    }
}
