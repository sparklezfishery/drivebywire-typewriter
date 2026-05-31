package org.sparklezfish.drivebywire.typewriter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.network.chat.Component;

public final class TypewriterChannels {
    private static final String I18N_PREFIX = DriveByWireTypewriterMod.MODID + ".channel";
    private static final String CHANNEL_ID_PREFIX = "channel";
    // Display Mapping ChannelCode -> ChannelName (i18n)
    private static final LinkedHashMap<Integer, Component> CODE_TO_CHANNEL_DISPLAY = new LinkedHashMap<>();
    private static final LinkedHashMap<Integer, String> CODE_TO_CHANNEL_ID = new LinkedHashMap<>();
    private static final String HUNGARIAN_CHARACTERS = "áÁéÉíÍóÓöÖőŐúÚüÜűŰ";

    static {
        // Letters A-Z (GLFW codes match ASCII lowercase: 65-90)
        for (int c = 65; c <= 90; c++) {
            registerChannel(c, String.valueOf((char) (c + 32)));
        }
        // Digits 0-9 (GLFW 48-57)
        for (int c = 48; c <= 57; c++) {
            registerChannel(c);
        }
        // Symbol keys present in the Typewriter's keyboard UI
        registerChannel(32, "space");
        registerChannel(39, "apostrophe");
        registerChannel(44, "comma");
        registerChannel(45, "minus");
        registerChannel(46, "period");
        registerChannel(47, "slash");
        registerChannel(59, "semicolon");
        registerChannel(61, "equals");
        registerChannel(91, "l_bracket");
        registerChannel(93, "r_bracket");
        registerChannel(92, "backslash");
        // Control keys
        registerChannel(257, "enter");
        registerChannel(258, "tab");
        registerChannel(259, "backspace");
        registerChannel(261, "delete");
        registerChannel(280, "caps_lock");
        // Navigation
        registerChannel(262, "right");
        registerChannel(263, "left");
        registerChannel(264, "down");
        registerChannel(265, "up");
        registerChannel(266, "page_up");
        registerChannel(267, "page_down");
        registerChannel(269, "end");
        // Modifiers
        registerChannel(340, "l_shift");
        registerChannel(341, "l_ctrl");
        registerChannel(342, "l_alt");
        registerChannel(343, "l_super");
        registerChannel(344, "r_shift");
        registerChannel(345, "r_ctrl");
        registerChannel(346, "r_alt");
        registerChannel(348, "menu");
    }

    public static List<Component> ALL_CHANNEL_DISPLAYS = List.copyOf(CODE_TO_CHANNEL_DISPLAY.values());
    public static final Map<Integer, Component> DISPLAY_MAP = Collections.unmodifiableMap(CODE_TO_CHANNEL_DISPLAY);

    private TypewriterChannels() {
    }

    public static boolean isHungarianCharacter(String character) {
        return character != null
                && character.codePointCount(0, character.length()) == 1
                && HUNGARIAN_CHARACTERS.indexOf(character.charAt(0)) >= 0;
    }

    private static void registerChannel(int code) {
        registerChannel(code, Character.toString((char) code));
    }

    private static void registerChannel(int code, String channelKey) {
        CODE_TO_CHANNEL_DISPLAY.put(code, Component.translatable(String.format("%s.%s", I18N_PREFIX, channelKey)));
        CODE_TO_CHANNEL_ID.put(code, String.format("%s.%s", CHANNEL_ID_PREFIX, channelKey));
    }
}
