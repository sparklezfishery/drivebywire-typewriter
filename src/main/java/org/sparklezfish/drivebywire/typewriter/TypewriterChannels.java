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

    private static final Set<String> HUNGARIAN_KEY_NAMES =
        Set.of("á", "é", "í", "ó", "ö", "ő", "ú", "ü", "ű");
    private static final LinkedHashMap<Integer, String> CODE_TO_CHANNEL = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> PRINTABLE_TO_CHANNEL = new LinkedHashMap<>();

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

    public static final List<String> CHANNELS;
    private static final List<String> STANDARD_CHANNELS;
    private static final Set<String> VALID_CHANNELS;

    public static final Map<Integer, String> CODE_MAP = Collections.unmodifiableMap(CODE_TO_CHANNEL);

    static {
        var standardChannels = new LinkedHashSet<>(CODE_TO_CHANNEL.values());
        STANDARD_CHANNELS = List.copyOf(standardChannels);

        var allChannels = new LinkedHashSet<>(standardChannels);
        allChannels.addAll(PRINTABLE_TO_CHANNEL.values());
        CHANNELS = List.copyOf(allChannels);
        VALID_CHANNELS = Set.copyOf(allChannels);
    }

    private static void addPrintable(String keyName, String channelName) {
        PRINTABLE_TO_CHANNEL.put(keyName, "drivebywiretypewriter.key." + channelName);
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
        for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_MENU; key++) {
            String keyName = GLFW.glfwGetKeyName(key, GLFW.glfwGetKeyScancode(key));
            if (keyName != null && HUNGARIAN_KEY_NAMES.contains(keyName.toLowerCase(Locale.ROOT))) {
                return CHANNELS;
            }
        }
        return STANDARD_CHANNELS;
    }

    private TypewriterChannels() {
    }
}
