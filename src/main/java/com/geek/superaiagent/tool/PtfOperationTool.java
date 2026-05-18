package com.geek.superaiagent.tool;

import cn.hutool.core.io.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Locale;

import static com.geek.superaiagent.constant.FileConstant.FILE_BASE_DIR;

@Component
public class PtfOperationTool {

    private final String baseDir = FILE_BASE_DIR + "/ptf";

    public PtfOperationTool() {
        new File(baseDir).mkdirs();
    }

    /** 输出落在 tmp/file 下，禁止 .. */
    private File fileOf(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("filename empty");
        }
        if (filename.contains("..")) {
            throw new IllegalArgumentException("invalid filename");
        }
        String name = filename.trim();
        if (!name.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
            name = name + ".pdf";
        }
        return new File(baseDir, name);
    }

    @Tool(description = "Generate a PDF document from plain text content. Use this when asked to create a PDF, make a document, or generate a report file. Supports Chinese text. The '.pdf' extension is appended automatically if not provided.")
    public String writePdf(
            @ToolParam(description = "Desired filename for the PDF. '.pdf' extension is appended automatically if missing. Example: 'date_plan' produces 'date_plan.pdf'.") String filename,
            @ToolParam(description = "The full text body of the PDF. Each line becomes a separate paragraph. Supports Chinese and English.") String content) {
        try {
            File dest = fileOf(filename);
            FileUtil.mkParentDirs(dest);
            // font-asian 提供的宋体 + GB 编码，可正常显示中文
            PdfFont font = PdfFontFactory.createFont(
                    "STSong-Light",
                    "UniGB-UCS2-H",
                    PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            PdfWriter writer = new PdfWriter(dest.getAbsolutePath());
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);
            try {
                doc.setFont(font);
                String body = content == null ? "" : content;
                for (String line : body.split("\\R", -1)) {
                    doc.add(new Paragraph(line));
                }
            } finally {
                doc.close();
            }
            return "ok";
        } catch (Exception e) {
            return "Error write pdf: " + e.getMessage();
        }
    }
}
