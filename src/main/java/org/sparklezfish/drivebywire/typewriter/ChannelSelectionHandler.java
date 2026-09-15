package org.sparklezfish.drivebywire.typewriter;

import com.mojang.blaze3d.platform.InputConstants;
import dev.simulated_team.simulated.mixin_interface.PlayerTypewriterExtension;
import edn.stratodonut.drivebywire.WireItems;
import edn.stratodonut.drivebywire.client.ClientWireNetworkHandler;
import net.minecraft.Util;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;
import org.sparklezfish.drivebywire.typewriter.blocks.TypewriterHubBlock;
import org.sparklezfish.drivebywire.typewriter.mixin.client.ClientWireNetworkHandlerAccessor;

final class ChannelSelectionHandler {
    static final KeyMapping SELECT_CHANNEL = new KeyMapping(
            "key.drivebywiretypewriter.select_channel", KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "itemGroup.drivebywiretypewriter");
    private static final HoldToOpen HOLD = new HoldToOpen(() -> TypewriterClientConfig.CHANNEL_SELECTION_HOLD_MS.get());
    private static BlockPos pendingSource;

    static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SELECT_CHANNEL);
    }

    static boolean isHeld() {
        var key = SELECT_CHANNEL.getKey();
        long window = Minecraft.getInstance().getWindow().getWindow();
        if (key.getType() == InputConstants.Type.MOUSE) {
            return GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
        }
        if (key.getType() == InputConstants.Type.KEYSYM && key.getValue() != GLFW.GLFW_KEY_UNKNOWN) {
            return InputConstants.isKeyDown(window, key.getValue());
        }
        if (key.getType() == InputConstants.Type.SCANCODE) {
            for (int code = GLFW.GLFW_KEY_SPACE; code <= GLFW.GLFW_KEY_LAST; code++) {
                if (GLFW.glfwGetKeyScancode(code) == key.getValue() && InputConstants.isKeyDown(window, code)) return true;
            }
        }
        return false;
    }

    static BlockPos selectedHub(Minecraft mc) {
        if (mc.player == null || mc.level == null || !mc.isWindowActive() || mc.player.isSpectator()
                || !mc.player.getMainHandItem().is(WireItems.WIRE.get())
                || ((PlayerTypewriterExtension) mc.player).simulated$getCurrentTypewriter() != null) return null;
        BlockPos pos = ClientWireNetworkHandlerAccessor.typewriter$getSelectedSource();
        return pos != null && mc.level.hasChunkAt(pos)
                && mc.level.getBlockState(pos).getBlock() instanceof TypewriterHubBlock ? pos : null;
    }

    static void onClientTick(ClientTickEvent.Pre event) {
        var mc = Minecraft.getInstance();
        boolean down = isHeld();
        if (mc.screen instanceof ChannelSelectionScreen screen) {
            screen.updateSelection(down);
            HOLD.update(down, false, Util.getMillis());
            return;
        }
        BlockPos source = selectedHub(mc);
        if (pendingSource != null && !pendingSource.equals(source)) HOLD.update(down, false, Util.getMillis());
        if (source != null && !source.equals(pendingSource)) {
            Component tip = SELECT_CHANNEL.isUnbound()
                    ? Component.translatable("gui.drivebywiretypewriter.quick_selection_unbound")
                    : Component.translatable("gui.drivebywiretypewriter.quick_selection_tip",
                            SELECT_CHANNEL.getTranslatedKeyMessage());
            // Keep the selected channel visible alongside the shortcut in the action bar.
            mc.player.displayClientMessage(Component.translatable("drivebywire.wire.channel.selected",
                    Component.translatable(ClientWireNetworkHandler.getCurrentChannel()))
                    .append(" — ").append(tip), true);
        }
        pendingSource = source;
        if (HOLD.update(down, source != null && mc.screen == null, Util.getMillis())) {
            mc.setScreen(new ChannelSelectionScreen(source));
        }
        while (SELECT_CHANNEL.consumeClick()) { }
    }
}
