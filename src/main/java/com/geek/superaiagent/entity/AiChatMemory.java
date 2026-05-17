//package com.geek.superaiagent.entity;
//
//import com.baomidou.mybatisplus.annotation.IdType;
//import com.baomidou.mybatisplus.annotation.TableField;
//import com.baomidou.mybatisplus.annotation.TableId;
//import com.baomidou.mybatisplus.annotation.TableName;
//import java.io.Serializable;
//import java.time.LocalDateTime;
//import lombok.Data;
//
///**
// *
// * @TableName ai_chat_memory
// */
//@TableName(value ="ai_chat_memory")
//@Data
//public class AiChatMemory implements Serializable {
//    /**
//     * 主键id
//     */
//    @TableId(type = IdType.AUTO)
//    private Long id;
//
//    /**
//     * 会话id
//     */
//    private String conversationId;
//
//    /**
//     * 用户提问
//     */
//    private String userPrompt;
//
//    /**
//     * AI回答
//     */
//    private String aiResponse;
//
//    /**
//     * 创建时间
//     */
//    private LocalDateTime createTime;
//
//    @TableField(exist = false)
//    private static final long serialVersionUID = 1L;
//}