package com.geek.superaiagent.tool;

import cn.hutool.core.io.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;

import static com.geek.superaiagent.constant.FileConstant.FILE_BASE_DIR;

@Component
public class FileOperationTool {

    /** 所有读写都限制在此目录下，避免模型访问任意磁盘路径 */
    private final String baseDir = FILE_BASE_DIR + "/file";

    public FileOperationTool() {
        new File(baseDir).mkdirs();
    }

    /** 将相对文件名解析为沙箱内的 File，并拦截明显的路径穿越 */
    private File fileOf(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename empty");
        }
        if (filename.contains("..")) {
            throw new IllegalArgumentException("invalid filename");
        }
        return new File(baseDir, filename);
    }

    @Tool(description = "Read and return the full text content of a file from the workspace. Use this when asked to view, check, or look at what's inside a file.")
    public String readFile(@ToolParam(description = "Path to the file relative to the workspace. Example: 'notes/todo.txt'. Do not use absolute paths like D:\\...") String filename) {
        try {
            File file = fileOf(filename);
            if (!file.exists()) {
                return "file not found";
            }
            if (!file.isFile()) {
                return "not a file";
            }
            // 按 UTF-8 读整个文件为字符串，交给模型使用
            return FileUtil.readUtf8String(file);
        } catch (RuntimeException e) {
            return "Error read file :" + e.getMessage();
        }
    }

    @Tool(description = "Create a new file or overwrite an existing file with the specified text content. Use this when asked to create, write, add, or save text to a file. Parent directories are created automatically.")
    public String writeFile(
            @ToolParam(description = "Relative file path within the workspace. Subdirectories are created if needed. Example: 'output/report.txt'") String filename,
            @ToolParam(description = "The complete text content to write into the file.") String content) {
        try {
            File file = fileOf(filename);
            // 多级子目录时先创建父目录，再覆盖写入 UTF-8
            FileUtil.mkParentDirs(file);
            FileUtil.writeUtf8String(content == null ? "" : content, file);
            return "Writing finished";
        } catch (RuntimeException e) {
            return "Error write file" + e.getMessage();
        }
    }
}
