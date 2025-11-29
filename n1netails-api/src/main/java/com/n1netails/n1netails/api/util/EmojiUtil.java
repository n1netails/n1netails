package com.n1netails.n1netails.api.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmojiUtil {

    /**
     * Get tail level emoji
     * | Level          | Emoji    |
     * | -------------- | ---------
     * | **INFO**       | 💡      |
     * | **SUCCESS**    | ✅      |
     * | **WARN**       | ⚠️      |
     * | **ERROR**      | ❌      |
     * | **CRITICAL**   | 🚨      |
     * | **KUDA**       | 🦊      |
     * @param tailLevel tail level
     * @return emoji value for tail level
     */
    public static String getTailLevelEmoji(String tailLevel) {
        return switch (tailLevel) {
            case "INFO" -> "\uD83D\uDCA1 "; // 💡 info
            case "SUCCESS" -> "✅ "; // ✅ success
            case "WARN" -> "⚠\uFE0F "; // ⚠️ warn
            case "ERROR" -> "❌ "; // ❌ error
            case "CRITICAL" -> "\uD83D\uDEA8 "; // 🚨 critical
            default -> "\uD83E\uDD8A "; // 🦊 kuda (custom level)
        };
    }
}
