package org.sparklezfish.drivebywire.typewriter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TypewriterChannels {

    private static final String HUNGARIAN_CHARACTERS = "áÁéÉíÍóÓöÖőŐúÚüÜűŰ";
    private static final String I18N_PREFIX = "drivebywire.typewriter.channel.";

    // 核心映射表（仅通道名改为i18n键，逻辑不变）
    private static final LinkedHashMap<Integer, String> CODE_TO_CHANNEL = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> CHARACTER_TO_CHANNEL = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> CHANNEL_NAMES = new LinkedHashMap<>();

    static {
        // 字母 A-Z (GLFW 65-90)
        for (int c = 65; c <= 90; c++) {
            String key = String.valueOf((char) c).toLowerCase();
            String channel = I18N_PREFIX + key;
            registerCode(c, channel);
            registerCharacter(Character.toString((char) c), channel);
            registerCharacter(Character.toString(Character.toLowerCase((char) c)), channel);
        }
        // 数字 0-9 (GLFW 48-57)
        for (int c = 48; c <= 57; c++) {
            String channel = I18N_PREFIX + (char) c;
            registerCode(c, channel);
            registerCharacter(Character.toString((char) c), channel);
        }

        // 符号按键（全替换为i18n键）
        registerCodeAndCharacter(32, " ", I18N_PREFIX + "space");
        registerCodeAndCharacter(39, "'", I18N_PREFIX + "apostrophe");
        registerCodeAndCharacter(44, ",", I18N_PREFIX + "comma");
        registerCodeAndCharacter(45, "-", I18N_PREFIX + "minus");
        registerCodeAndCharacter(46, ".", I18N_PREFIX + "period");
        registerCodeAndCharacter(47, "/", I18N_PREFIX + "slash");
        registerCodeAndCharacter(59, ";", I18N_PREFIX + "semicolon");
        registerCodeAndCharacter(61, "=", I18N_PREFIX + "equals");
        registerCodeAndCharacter(91, "[", I18N_PREFIX + "left_bracket");
        registerCodeAndCharacter(92, "\\", I18N_PREFIX + "backslash");
        registerCodeAndCharacter(93, "]", I18N_PREFIX + "right_bracket");

        // 匈牙利字符
        registerHungarianCharacters();

        // 控制按键
        registerCode(257, I18N_PREFIX + "enter");
        registerCode(258, I18N_PREFIX + "tab");
        registerCode(259, I18N_PREFIX + "backspace");
        registerCode(261, I18N_PREFIX + "delete");
        registerCode(280, I18N_PREFIX + "caps_lock");

        // 导航按键
        registerCode(262, I18N_PREFIX + "right");
        registerCode(263, I18N_PREFIX + "left");
        registerCode(264, I18N_PREFIX + "down");
        registerCode(265, I18N_PREFIX + "up");
        registerCode(266, I18N_PREFIX + "page_up");
        registerCode(267, I18N_PREFIX + "page_down");
        registerCode(269, I18N_PREFIX + "end");

        // 修饰按键
        registerCode(340, I18N_PREFIX + "left_shift");
        registerCode(341, I18N_PREFIX + "left_ctrl");
        registerCode(342, I18N_PREFIX + "left_alt");
        registerCode(343, I18N_PREFIX + "left_super");
        registerCode(344, I18N_PREFIX + "right_shift");
        registerCode(345, I18N_PREFIX + "right_ctrl");
        registerCode(346, I18N_PREFIX + "right_alt");
        registerCode(348, I18N_PREFIX + "menu");
    }

    public static final List<String> CHANNELS = List.copyOf(CHANNEL_NAMES.values());

    public static final Map<Integer, String> CODE_MAP = Collections.unmodifiableMap(CODE_TO_CHANNEL);
    public static final Map<String, String> CHARACTER_MAP = Collections.unmodifiableMap(CHARACTER_TO_CHANNEL);

    private TypewriterChannels() {
    }

    public static boolean isKnownChannel(String channel) {
        return CHANNELS.contains(channel);
    }

    public static String channelForCharacter(String character) {
        if (character == null || character.isEmpty())
            return null;
        return CHARACTER_TO_CHANNEL.get(character);
    }

    public static String channelForCode(int key) {
        return CODE_TO_CHANNEL.get(key);
    }

    public static boolean isHungarianCharacter(String character) {
        return character != null
                && character.codePointCount(0, character.length()) == 1
                && HUNGARIAN_CHARACTERS.indexOf(character.charAt(0)) >= 0;
    }

    private static void registerCode(int code, String channel) {
        CODE_TO_CHANNEL.put(code, channel);
        CHANNEL_NAMES.put(channel, channel);
    }

    private static void registerCharacter(String character, String channel) {
        CHARACTER_TO_CHANNEL.put(character, channel);
        CHANNEL_NAMES.put(channel, channel);
    }

    private static void registerCodeAndCharacter(int code, String character, String channel) {
        registerCode(code, channel);
        registerCharacter(character, channel);
    }

    private static void registerHungarianCharacters() {
        registerAccent("á", I18N_PREFIX + "hungarian.a_acute");
        registerAccent("é", I18N_PREFIX + "hungarian.e_acute");
        registerAccent("í", I18N_PREFIX + "hungarian.i_acute");
        registerAccent("ó", I18N_PREFIX + "hungarian.o_acute");
        registerAccent("ö", I18N_PREFIX + "hungarian.o_diaeresis");
        registerAccent("ő", I18N_PREFIX + "hungarian.o_double_acute");
        registerAccent("ú", I18N_PREFIX + "hungarian.u_acute");
        registerAccent("ü", I18N_PREFIX + "hungarian.u_diaeresis");
        registerAccent("ű", I18N_PREFIX + "hungarian.u_double_acute");
    }

    private static void registerAccent(String character, String channel) {
        registerCharacter(character, channel);
        registerCharacter(character.toUpperCase(), channel);
    }
}
