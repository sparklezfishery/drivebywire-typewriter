package org.sparklezfish.drivebywire.typewriter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TypewriterChannels {

    private static final LinkedHashMap<Integer, String> CODE_TO_CHANNEL = new LinkedHashMap<>();

    static {
        // Letters A-Z (GLFW codes match ASCII uppercase: 65-90)
        for (int c = 65; c <= 90; c++) {
            char ch = (char) c;
            CODE_TO_CHANNEL.put(c, "drivebywiretypewriter.key." + Character.toLowerCase(ch));
        }
        // Digits 0-9 (GLFW 48-57)
        for (int c = 48; c <= 57; c++) {
            char ch = (char) c;
            CODE_TO_CHANNEL.put(c, "drivebywiretypewriter.key." + ch);
        }

        // Symbol keys present in the Typewriter's keyboard UI
        CODE_TO_CHANNEL.put(32,  "drivebywiretypewriter.key.space");
        CODE_TO_CHANNEL.put(39,  "drivebywiretypewriter.key.apostrophe");
        CODE_TO_CHANNEL.put(44,  "drivebywiretypewriter.key.comma");
        CODE_TO_CHANNEL.put(45,  "drivebywiretypewriter.key.minus");
        CODE_TO_CHANNEL.put(46,  "drivebywiretypewriter.key.period");
        CODE_TO_CHANNEL.put(47,  "drivebywiretypewriter.key.slash");
        CODE_TO_CHANNEL.put(59,  "drivebywiretypewriter.key.semicolon");
        CODE_TO_CHANNEL.put(61,  "drivebywiretypewriter.key.equals");
        CODE_TO_CHANNEL.put(91,  "drivebywiretypewriter.key.left_bracket");
        CODE_TO_CHANNEL.put(92,  "drivebywiretypewriter.key.backslash");
        CODE_TO_CHANNEL.put(93,  "drivebywiretypewriter.key.right_bracket");
        // Control keys
        CODE_TO_CHANNEL.put(257, "drivebywiretypewriter.key.enter");
        CODE_TO_CHANNEL.put(258, "drivebywiretypewriter.key.tab");
        CODE_TO_CHANNEL.put(259, "drivebywiretypewriter.key.backspace");
        CODE_TO_CHANNEL.put(261, "drivebywiretypewriter.key.delete");
        CODE_TO_CHANNEL.put(280, "drivebywiretypewriter.key.caps_lock");

        // Navigation
        CODE_TO_CHANNEL.put(262, "drivebywiretypewriter.key.right");
        CODE_TO_CHANNEL.put(263, "drivebywiretypewriter.key.left");
        CODE_TO_CHANNEL.put(264, "drivebywiretypewriter.key.down");
        CODE_TO_CHANNEL.put(265, "drivebywiretypewriter.key.up");
        CODE_TO_CHANNEL.put(266, "drivebywiretypewriter.key.page_up");
        CODE_TO_CHANNEL.put(267, "drivebywiretypewriter.key.page_down");
        CODE_TO_CHANNEL.put(269, "drivebywiretypewriter.key.end");

        // Modifiers
        CODE_TO_CHANNEL.put(340, "drivebywiretypewriter.key.left_shift");
        CODE_TO_CHANNEL.put(341, "drivebywiretypewriter.key.left_ctrl");
        CODE_TO_CHANNEL.put(342, "drivebywiretypewriter.key.left_alt");
        CODE_TO_CHANNEL.put(343, "drivebywiretypewriter.key.left_super");
        CODE_TO_CHANNEL.put(344, "drivebywiretypewriter.key.right_shift");
        CODE_TO_CHANNEL.put(345, "drivebywiretypewriter.key.right_ctrl");
        CODE_TO_CHANNEL.put(346, "drivebywiretypewriter.key.right_alt");
        CODE_TO_CHANNEL.put(348, "drivebywiretypewriter.key.menu");
    }

    public static final List<String> CHANNELS = List.copyOf(CODE_TO_CHANNEL.values());

    public static final Map<Integer, String> CODE_MAP = Collections.unmodifiableMap(CODE_TO_CHANNEL);

    private TypewriterChannels() {
    }
}
