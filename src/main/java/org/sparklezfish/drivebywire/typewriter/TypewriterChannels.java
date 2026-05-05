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
            CODE_TO_CHANNEL.put(c, "key" + (char) c);
        }
        // Digits 0-9 (GLFW 48-57)
        for (int c = 48; c <= 57; c++) {
            CODE_TO_CHANNEL.put(c, "key" + (char) c);
        }
        // Symbol keys present in the Typewriter's keyboard UI
        CODE_TO_CHANNEL.put(32, "keySpace");
        CODE_TO_CHANNEL.put(39, "keyApostrophe");
        CODE_TO_CHANNEL.put(44, "keyComma");
        CODE_TO_CHANNEL.put(45, "keyMinus");
        CODE_TO_CHANNEL.put(46, "keyPeriod");
        CODE_TO_CHANNEL.put(47, "keySlash");
        CODE_TO_CHANNEL.put(59, "keySemicolon");
        CODE_TO_CHANNEL.put(61, "keyEquals");
        CODE_TO_CHANNEL.put(91, "keyLeftBracket");
        CODE_TO_CHANNEL.put(92, "keyBackslash");
        CODE_TO_CHANNEL.put(93, "keyRightBracket");
        // Control keys
        CODE_TO_CHANNEL.put(257, "keyEnter");
        CODE_TO_CHANNEL.put(258, "keyTab");
        CODE_TO_CHANNEL.put(259, "keyBackspace");
        CODE_TO_CHANNEL.put(261, "keyDelete");
        CODE_TO_CHANNEL.put(280, "keyCapsLock");
        // Navigation
        CODE_TO_CHANNEL.put(262, "keyRight");
        CODE_TO_CHANNEL.put(263, "keyLeft");
        CODE_TO_CHANNEL.put(264, "keyDown");
        CODE_TO_CHANNEL.put(265, "keyUp");
        CODE_TO_CHANNEL.put(266, "keyPageUp");
        CODE_TO_CHANNEL.put(267, "keyPageDown");
        CODE_TO_CHANNEL.put(269, "keyEnd");
        // Modifiers
        CODE_TO_CHANNEL.put(340, "keyLeftShift");
        CODE_TO_CHANNEL.put(341, "keyLeftCtrl");
        CODE_TO_CHANNEL.put(342, "keyLeftAlt");
        CODE_TO_CHANNEL.put(343, "keyLeftSuper");
        CODE_TO_CHANNEL.put(344, "keyRightShift");
        CODE_TO_CHANNEL.put(345, "keyRightCtrl");
        CODE_TO_CHANNEL.put(346, "keyRightAlt");
        CODE_TO_CHANNEL.put(348, "keyMenu");
    }

    public static final List<String> CHANNELS = List.copyOf(CODE_TO_CHANNEL.values());

    public static final Map<Integer, String> CODE_MAP = Collections.unmodifiableMap(CODE_TO_CHANNEL);

    private TypewriterChannels() {
    }
}
