package com.geek.superaiagent.constant;

import java.util.List;

public class SystemConstant {

    public static final List<String> SENSITIVE_WORDS = List.of(
            "色情",
            "约炮",
            "一夜情",
            "援交",
            "裸聊",
            "成人视频",
            "黄色网站",
            "嫖娼",
            "卖淫",
            "色情网",
            "成人视频",
            "性交易",
            "开房",
            "做爱",
            "强奸",
            "迷奸",
            "乱伦",
            "幼女",
            "萝莉",
            "恋童",
            "吸毒",
            "冰毒",
            "海洛因",
            "大麻",
            "贩毒",
            "赌博",
            "六合彩",
            "赌球",
            "刷单",
            "诈骗",
            "洗钱",
            "办证",
            "假证",
            "代开发票",
            "枪支",
            "炸药",
            "恐怖袭击",
            "极端主义",
            "自杀",
            "轻生",
            "割腕",
            "跳楼",
            "辱骂",
            "傻逼",
            "脑残",
            "废物",
            "滚蛋",
            "操你妈",
            "妈的",
            "死全家",
            "fuck",
            "shit",
            "bitch",
            "nmsl"
    );

    public static final String FAIL_RESPONSE = "你的描述中包含违禁词，请检查之后重新提问哦，或者我们可以聊一聊其他的";

    public static final String DO_CHAT_REPORT_DEFAULT_PROMPT = "每一次谈话结束，根据我的恋爱情况，生成恋爱报告";
}
