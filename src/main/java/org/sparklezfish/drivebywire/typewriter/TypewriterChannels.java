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
            CODE_TO_CHANNEL.put(c, "create_submarine.key." + Character.toLowerCase(ch));
        }
        // Digits 0-9 (GLFW 48-57)
        for (int c = 48; c <= 57; c++) {
            char ch = (char) c;
            CODE_TO_CHANNEL.put(c, "create_submarine.key." + ch);
        }

        // Symbol keys present in the Typewriter's keyboard UI
        CODE_TO_CHANNEL.put(32,  "create_submarine.key.space");
        CODE_TO_CHANNEL.put(39,  "create_submarine.key.apostrophe");
        CODE_TO_CHANNEL.put(44,  "create_submarine.key.comma");
        CODE_TO_CHANNEL.put(45,  "create_submarine.key.minus");
        CODE_TO_CHANNEL.put(46,  "create_submarine.key.period");
        CODE_TO_CHANNEL.put(47,  "create_submarine.key.slash");
        CODE_TO_CHANNEL.put(59,  "create_submarine.key.semicolon");
        CODE_TO_CHANNEL.put(61,  "create_submarine.key.equals");
        CODE_TO_CHANNEL.put(91,  "create_submarine.key.left_bracket");
        CODE_TO_CHANNEL.put(92,  "create_submarine.key.backslash");
        CODE_TO_CHANNEL.put(93,  "create_submarine.key.right_bracket");
        // Control keys
        CODE_TO_CHANNEL.put(257, "create_submarine.key.enter");
        CODE_TO_CHANNEL.put(258, "create_submarine.key.tab");
        CODE_TO_CHANNEL.put(259, "create_submarine.key.backspace");
        CODE_TO_CHANNEL.put(261, "create_submarine.key.delete");
        CODE_TO_CHANNEL.put(280, "create_submarine.key.caps_lock");

        // Navigation
        CODE_TO_CHANNEL.put(262, "create_submarine.key.right");
        CODE_TO_CHANNEL.put(263, "create_submarine.key.left");
        CODE_TO_CHANNEL.put(264, "create_submarine.key.down");
        CODE_TO_CHANNEL.put(265, "create_submarine.key.up");
        CODE_TO_CHANNEL.put(266, "create_submarine.key.page_up");
        CODE_TO_CHANNEL.put(267, "create_submarine.key.page_down");
        CODE_TO_CHANNEL.put(269, "create_submarine.key.end");

        // Modifiers
        CODE_TO_CHANNEL.put(340, "create_submarine.key.left_shift");
        CODE_TO_CHANNEL.put(341, "create_submarine.key.left_ctrl");
        CODE_TO_CHANNEL.put(342, "create_submarine.key.left_alt");
        CODE_TO_CHANNEL.put(343, "create_submarine.key.left_super");
        CODE_TO_CHANNEL.put(344, "create_submarine.key.right_shift");
        CODE_TO_CHANNEL.put(345, "create_submarine.key.right_ctrl");
        CODE_TO_CHANNEL.put(346, "create_submarine.key.right_alt");
        CODE_TO_CHANNEL.put(348, "create_submarine.key.menu");
    }

    public static final List<String> CHANNELS = List.copyOf(CODE_TO_CHANNEL.values());

    public static final Map<Integer, String> CODE_MAP = Collections.unmodifiableMap(CODE_TO_CHANNEL);

    private TypewriterChannels() {
    }
}
