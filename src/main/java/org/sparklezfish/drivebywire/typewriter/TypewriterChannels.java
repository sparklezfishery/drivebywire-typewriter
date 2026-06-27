package org.sparklezfish.drivebywire.typewriter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.lwjgl.glfw.GLFW;

public final class TypewriterChannels {

    private static final LinkedHashMap<Integer, String> CODE_TO_CHANNEL = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> PRINTABLE_TO_CHANNEL = new LinkedHashMap<>();
    private static final LinkedHashSet<String> NON_PRINTABLE_CHANNELS = new LinkedHashSet<>();

    static {
        // GLFW key names use the active keyboard layout, unlike the key codes below.
        // This lets the same physical keys expose the characters printed by Hungarian
        // and other layouts instead of always pretending the keyboard is US English.
        for (char c = 'a'; c <= 'z'; c++) {
            addPrintable(String.valueOf(c), String.valueOf(c));
        }
        for (char c = '0'; c <= '9'; c++) {
            addPrintable(String.valueOf(c), String.valueOf(c));
        }
        addPrintable(" ", "space");
        addPrintable("'", "apostrophe");
        addPrintable(",", "comma");
        addPrintable("-", "minus");
        addPrintable(".", "period");
        addPrintable("/", "slash");
        addPrintable(";", "semicolon");
        addPrintable("=", "equals");
        addPrintable("[", "left_bracket");
        addPrintable("\\", "backslash");
        addPrintable("]", "right_bracket");

        addPrintable("á", "a_acute");
        addPrintable("é", "e_acute");
        addPrintable("í", "i_acute");
        addPrintable("ó", "o_acute");
        addPrintable("ö", "o_diaeresis");
        addPrintable("ő", "o_double_acute");
        addPrintable("ú", "u_acute");
        addPrintable("ü", "u_diaeresis");
        addPrintable("ű", "u_double_acute");

        // Fallbacks for non-printable keys and platforms where GLFW has no key name.
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
        addNonPrintable(257, "enter");
        addNonPrintable(258, "tab");
        addNonPrintable(259, "backspace");
        addNonPrintable(261, "delete");
        addNonPrintable(280, "caps_lock");

        // Navigation
        addNonPrintable(262, "right");
        addNonPrintable(263, "left");
        addNonPrintable(264, "down");
        addNonPrintable(265, "up");
        addNonPrintable(266, "page_up");
        addNonPrintable(267, "page_down");
        addNonPrintable(269, "end");

        // Modifiers
        addNonPrintable(340, "left_shift");
        addNonPrintable(341, "left_ctrl");
        addNonPrintable(342, "left_alt");
        addNonPrintable(343, "left_super");
        addNonPrintable(344, "right_shift");
        addNonPrintable(345, "right_ctrl");
        addNonPrintable(346, "right_alt");
        addNonPrintable(348, "menu");
    }

    public static final List<String> CHANNELS;
    private static final List<String> STANDARD_CHANNELS;
    private static final Set<String> STANDARD_CHANNEL_SET;
    private static final Set<String> VALID_CHANNELS;

    public static final Map<Integer, String> CODE_MAP = Collections.unmodifiableMap(CODE_TO_CHANNEL);

    static {
        var standardChannels = new LinkedHashSet<>(CODE_TO_CHANNEL.values());
        STANDARD_CHANNELS = List.copyOf(standardChannels);
        STANDARD_CHANNEL_SET = Set.copyOf(standardChannels);

        var allChannels = new LinkedHashSet<>(standardChannels);
        allChannels.addAll(PRINTABLE_TO_CHANNEL.values());
        CHANNELS = List.copyOf(allChannels);
        VALID_CHANNELS = Set.copyOf(allChannels);
    }

    private static void addPrintable(String keyName, String channelName) {
        PRINTABLE_TO_CHANNEL.put(keyName, "drivebywiretypewriter.key." + channelName);
    }

    private static void addNonPrintable(int keyCode, String channelName) {
        String channel = "drivebywiretypewriter.key." + channelName;
        CODE_TO_CHANNEL.put(keyCode, channel);
        NON_PRINTABLE_CHANNELS.add(channel);
    }

    public static String resolve(int keyCode, String glfwKeyName) {
        if (glfwKeyName != null) {
            String channel = PRINTABLE_TO_CHANNEL.get(glfwKeyName.toLowerCase(Locale.ROOT));
            if (channel != null) return channel;
        }
        return CODE_TO_CHANNEL.get(keyCode);
    }

    public static boolean isValid(String channel) {
        return VALID_CHANNELS.contains(channel);
    }

    public static List<String> channelsForCurrentKeyboardLayout() {
        LinkedHashSet<String> layoutChannels = new LinkedHashSet<>();
        boolean hasLayoutSpecificKey = false;

        for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_MENU; key++) {
            String keyName = GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key));
            if (keyName == null) continue;

            String normalized = keyName.toLowerCase(Locale.ROOT);
            String channel = PRINTABLE_TO_CHANNEL.get(normalized);
            if (channel != null) {
                layoutChannels.add(channel);
                if (!STANDARD_CHANNEL_SET.contains(channel)) {
                    hasLayoutSpecificKey = true;
                }
            }
        }

        if (!hasLayoutSpecificKey) {
            return STANDARD_CHANNELS;
        }

        layoutChannels.addAll(NON_PRINTABLE_CHANNELS);
        return CHANNELS.stream().filter(layoutChannels::contains).toList();
    }

    private TypewriterChannels() {
    }
}
