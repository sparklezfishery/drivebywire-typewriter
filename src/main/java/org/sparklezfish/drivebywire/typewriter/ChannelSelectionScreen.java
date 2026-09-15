package org.sparklezfish.drivebywire.typewriter;

import edn.stratodonut.drivebywire.client.ClientWireNetworkHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.sparklezfish.drivebywire.typewriter.mixin.client.ClientWireNetworkHandlerAccessor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

final class ChannelSelectionScreen extends Screen {
    // Physical GLFW positions; labels and channels are resolved using the active OS layout.
    private static final int[][] ROWS = {
            {49, 50, 51, 52, 53, 54, 55, 56, 57, 48, 45, 61, 259},
            {258, 81, 87, 69, 82, 84, 89, 85, 73, 79, 80, 91, 93, 92},
            {280, 65, 83, 68, 70, 71, 72, 74, 75, 76, 59, 39, 257},
            {340, 90, 88, 67, 86, 66, 78, 77, 44, 46, 47, 344},
            {341, 343, 342, 32, 346, 348, 345},
            {261, 266, 267, 269, 263, 264, 265, 262}
    };
    private record Key(String channel, int x, int y, int width) { }
    private final BlockPos source;
    private final List<Key> keys = new ArrayList<>();
    private ClientLevel level;
    private float scale;
    private int left;
    private int top;
    private int panelHeight;

    ChannelSelectionScreen(BlockPos source) {
        super(Component.translatable("gui.drivebywiretypewriter.channel_title"));
        this.source = source.immutable();
    }

    @Override
    protected void init() {
        if (level == null) level = minecraft.level;
        keys.clear();
        var included = new HashSet<String>();
        int y = 36;
        for (int[] row : ROWS) {
            int x = 12;
            for (int code : row) {
                String channel = TypewriterChannels.resolve(code, GLFW.glfwGetKeyName(code, GLFW.glfwGetKeyScancode(code)));
                int keyWidth = code == GLFW.GLFW_KEY_SPACE ? 224 : 40;
                if (channel != null) {
                    keys.add(new Key(channel, x, y, keyWidth));
                    included.add(channel);
                }
                x += keyWidth + 2;
            }
            y += 28;
        }
        // Include layout-specific keys outside the usual ANSI rows (e.g. Hungarian í).
        int x = 12;
        for (String channel : TypewriterChannels.channelsForCurrentKeyboardLayout()) {
            if (!included.add(channel)) continue;
            if (x + 40 > 600) { x = 12; y += 28; }
            keys.add(new Key(channel, x, y, 40));
            x += 42;
        }
        panelHeight = y + (x > 12 ? 28 : 0) + 42;
        scale = Math.min(1.0f, Math.min((width - 12) / 612.0f, (height - 12) / (float) panelHeight));
        left = (int) ((width - 612 * scale) / 2);
        top = (int) ((height - panelHeight * scale) / 2);
    }

    private Key hovered(double mouseX, double mouseY) {
        double x = (mouseX - left) / scale;
        double y = (mouseY - top) / scale;
        for (Key key : keys) {
            if (x >= key.x && x < key.x + key.width && y >= key.y && y < key.y + 25) return key;
        }
        return null;
    }

    void updateSelection(boolean held) {
        if (minecraft.level != level || !source.equals(ChannelSelectionHandler.selectedHub(minecraft))) {
            onClose();
        } else if (!held) {
            double x = minecraft.mouseHandler.xpos() * width / minecraft.getWindow().getScreenWidth();
            double y = minecraft.mouseHandler.ypos() * height / minecraft.getWindow().getScreenHeight();
            Key key = hovered(x, y);
            if (key != null) {
                ClientWireNetworkHandlerAccessor.typewriter$setCurrentChannel(key.channel);
                minecraft.player.displayClientMessage(Component.translatable("drivebywire.wire.channel.selected",
                        Component.translatable(key.channel)), true);
            }
            onClose();
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Key hovered = hovered(mouseX, mouseY);
        graphics.pose().pushPose();
        graphics.pose().translate(left, top, 0);
        graphics.pose().scale(scale, scale, 1);
        graphics.fill(0, 0, 612, panelHeight, 0xB018202B);
        graphics.drawCenteredString(font, title, 306, 12, 0xFFFFFF);
        String selected = ClientWireNetworkHandler.getCurrentChannel();
        for (Key key : keys) {
            int color = key == hovered ? 0xE057A6B8 : key.channel.equals(selected) ? 0xD0447161 : 0xB0384352;
            graphics.fill(key.x, key.y, key.x + key.width, key.y + 25, color);
            Component label = Component.translatable(key.channel);
            float textScale = Math.min(1.0f, (key.width - 4.0f) / Math.max(1, font.width(label)));
            graphics.pose().pushPose();
            graphics.pose().translate(key.x + key.width / 2.0f, key.y + 9, 0);
            graphics.pose().scale(textScale, textScale, 1);
            graphics.drawCenteredString(font, label, 0, 0, 0xFFFFFF);
            graphics.pose().popPose();
        }
        graphics.drawCenteredString(font, Component.translatable("gui.drivebywiretypewriter.channel_hint",
                ChannelSelectionHandler.SELECT_CHANNEL.getTranslatedKeyMessage()), 306, panelHeight - 30, 0xE0E4EC);
        if (hovered != null) graphics.drawCenteredString(font, Component.translatable(hovered.channel),
                306, panelHeight - 16, 0xFFFFFF);
        graphics.pose().popPose();
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Keep the world visible without Minecraft's menu blur or opaque background.
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) onClose();
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
