package com.geek.superaiagent.tool;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@Component
public class TerminalOperationTool {

    /** 控制台合并输出过长时截断，避免撑爆模型上下文 */
    private static final int MAX_OUTPUT_CHARS = 8000;

    @Tool(description = "Execute a shell command in the system terminal and return the output. Use this for system operations, checking system info, running scripts, or any task that requires command-line execution.")
    public String executeCommand(
            @ToolParam(description = "The full shell command to execute. Works with Windows (cmd) or Unix (bash) commands. Example: 'dir D:\\project' or 'ls -la /path'.") String command) {
        if (command == null || command.isBlank()) {
            return "command empty";
        }
        try {
            // Windows 用 cmd /c，类 Unix 用 sh -c，才能正确处理管道、重定向等一整条命令
            boolean win = System.getProperty("os.name").toLowerCase().contains("win");
            ProcessBuilder pb = win
                    ? new ProcessBuilder("cmd", "/c", command)
                    : new ProcessBuilder("sh", "-c", command);
            // 把 stderr 并进 stdout，一次读完
            pb.redirectErrorStream(true);
            Process process = pb.start();
            Charset cs = Charset.defaultCharset();
            StringBuilder out = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), cs))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    out.append(line).append('\n');
                }
            }
            int exit = process.waitFor();
            String text = out.toString().trim();
            if (text.length() > MAX_OUTPUT_CHARS) {
                text = text.substring(0, MAX_OUTPUT_CHARS) + "\n...(truncated)";
            }
            return "exit=" + exit + "\n" + text;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "interrupted: " + e.getMessage();
        } catch (Exception e) {
            return "Error execute command: " + e.getMessage();
        }
    }
}
