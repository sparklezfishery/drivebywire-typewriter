package org.sparklezfish.drivebywire.typewriter;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterInteractionHandler;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterRenderer;
import dev.simulated_team.simulated.mixin_interface.PlayerTypewriterExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;
import org.sparklezfish.drivebywire.typewriter.blocks.TypewriterHubBlockEntity;
import org.sparklezfish.drivebywire.typewriter.network.TypewriterHubDisconnectPacket;
import org.sparklezfish.drivebywire.typewriter.network.TypewriterHubKeyPacket;

import java.util.HashSet;
import java.util.Set;

@Mod(value = DriveByWireTypewriterMod.MODID, dist = Dist.CLIENT)
public class DriveByWireTypewriterClient {

    private static final Set<Integer> heldKeys = new HashSet<>();
    private static boolean prevWindowActive = true;
    private static boolean wasConnected = false;
//    private static String lastLoggedKeyboardProfile = "";

    public DriveByWireTypewriterClient(ModContainer container) {
        NeoForge.EVENT_BUS.addListener(DriveByWireTypewriterClient::onKeyInput);
        NeoForge.EVENT_BUS.addListener(DriveByWireTypewriterClient::onClientTick);
        var modBus = container.getEventBus();
        if (modBus != null) {
            modBus.addListener(DriveByWireTypewriterClient::registerRenderers);
        }
    }

    private static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
            DriveByWireTypewriterMod.TYPEWRITER_HUB_BLOCK_ENTITY.get(),
            LinkedTypewriterRenderer::new
        );
    }

    private static void onClientTick(ClientTickEvent.Pre event) {
        var mc = Minecraft.getInstance();
        boolean nowActive = mc.isWindowActive();
        boolean lostFocus = prevWindowActive && !nowActive;
        prevWindowActive = nowActive;

        var player = mc.player;
        if (player == null || mc.level == null) {
            if (!heldKeys.isEmpty()) releaseAllHeld(null, null);
            wasConnected = false;
            return;
        }

        var pos = ((PlayerTypewriterExtension) player).simulated$getCurrentTypewriter();
        var be = pos != null ? mc.level.getBlockEntity(pos) : null;
        boolean nowConnected = be instanceof LinkedTypewriterBlockEntity ltbe && ltbe.powered
                               && be instanceof TypewriterHubBlockEntity;

//        if (nowConnected && !wasConnected) {
//            logKeyboardProfile();
//        }

        boolean disconnected = wasConnected && !nowConnected;
        wasConnected = nowConnected;

        // Disconnect whenever focus is lost or any screen opens (ESC, chat, etc.) while connected.
        // No held-keys guard — even if typewriterPos is set but no keys are down, movement
        // keys pressed after returning would still hit preventPress.
        boolean forceDisconnect = (lostFocus || mc.screen != null) && nowConnected;

        if (forceDisconnect) {
            releaseAllHeld(pos, pos); // nowConnected guarantees be is TypewriterHubBlockEntity
        } else if ((lostFocus || disconnected) && !heldKeys.isEmpty()) {
            // Focus lost while not connected, or server kicked us — clean up local key state only
            heldKeys.clear();
            LinkedTypewriterInteractionHandler.getPressedKeys().clear();
        }
    }

    private static void releaseAllHeld(BlockPos typewriterPos, BlockPos validPos) {
        var mc = Minecraft.getInstance();
        if (validPos != null) {
            for (int key : heldKeys) {
                PacketDistributor.sendToServer(new TypewriterHubKeyPacket(validPos, key, false));
            }
            PacketDistributor.sendToServer(new TypewriterHubDisconnectPacket(validPos));
        }
        heldKeys.clear();
        LinkedTypewriterInteractionHandler.getPressedKeys().clear();
        var player = mc.player;
        if (player != null) {
            ((PlayerTypewriterExtension) player).simulated$setCurrentTypewriter(null);
        }
        LinkedTypewriterInteractionHandler.associateTypewriter(null);
    }

    private static void onKeyInput(InputEvent.Key event) {
        if (event.getAction() == GLFW.GLFW_REPEAT) return;

        var mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null || mc.level == null) return;

        var typewriterPos = ((PlayerTypewriterExtension) player).simulated$getCurrentTypewriter();
        if (typewriterPos == null) return;

        var be = mc.level.getBlockEntity(typewriterPos);
        if (!(be instanceof TypewriterHubBlockEntity hub)) return;
        if (!hub.powered) return;

        if (!TypewriterChannels.DISPLAY_MAP.containsKey(event.getKey())) return;

        // Suppress vanilla key bindings (e.g. E→inventory, Q→drop, Ctrl→sprint).
        // InputEvent.Key fires after Minecraft increments click counts, so consumeClick()
        // here clears those counts before the game loop reads them.
        LinkedTypewriterInteractionHandler.preventPress(event.getKey(), event.getScanCode());

        // Track held keys so the tick handler can release them on disconnect/focus-loss
        if (event.getAction() == GLFW.GLFW_PRESS) {
            heldKeys.add(event.getKey());
        } else {
            heldKeys.remove(event.getKey());
        }

        // Update key animation using the same index mapping as LinkedTypewriterInteractionHandler
        int animIdx = animIndex(event.getKey());
        var pressedKeys = LinkedTypewriterInteractionHandler.getPressedKeys();
        if (event.getAction() == GLFW.GLFW_PRESS) {
            if (!pressedKeys.contains(animIdx)) pressedKeys.addElement(animIdx);
        } else {
            pressedKeys.removeElement(animIdx);
        }

        PacketDistributor.sendToServer(new TypewriterHubKeyPacket(
            typewriterPos,
            event.getKey(),
            event.getAction() == GLFW.GLFW_PRESS
        ));
    }

//    private static void logKeyboardProfile() {
//        String profile = detectKeyboardProfile();
//        if (profile.equals(lastLoggedKeyboardProfile))
//            return;
//        lastLoggedKeyboardProfile = profile;
//        DriveByWireTypewriterMod.LOGGER.info("Typewriter keyboard layout detected as {}.", profile);
//    }

//    private static String detectKeyboardProfile() {
//        for (int key = GLFW.GLFW_KEY_SPACE; key <= GLFW.GLFW_KEY_MENU; key++) {
//            int scanCode = GLFW.glfwGetKeyScancode(key);
//            String keyName = GLFW.glfwGetKeyName(key, scanCode);
//            if (TypewriterChannels.isHungarianCharacter(keyName))
//                return "Hungarian-compatible";
//        }
//        return "English-compatible";
//    }

    private static int animIndex(int key) {
        // Matches LinkedTypewriterInteractionHandler's presetKeys map exactly.
        // Spacebar is slot 13 (the wide spacebar model); other keys use slots 0-12.
        if (key >= GLFW.GLFW_KEY_1 && key <= GLFW.GLFW_KEY_9) return key - GLFW.GLFW_KEY_1;
        if (key >= GLFW.GLFW_KEY_KP_1 && key <= GLFW.GLFW_KEY_KP_9) return key - GLFW.GLFW_KEY_KP_1;
        return switch (key) {
            case GLFW.GLFW_KEY_Q -> 0;
            case GLFW.GLFW_KEY_W -> 1;
            case GLFW.GLFW_KEY_E -> 2;
            case GLFW.GLFW_KEY_UP -> 4;
            case GLFW.GLFW_KEY_A -> 6;
            case GLFW.GLFW_KEY_S -> 7;
            case GLFW.GLFW_KEY_D -> 8;
            case GLFW.GLFW_KEY_LEFT -> 10;
            case GLFW.GLFW_KEY_DOWN -> 11;
            case GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_KEY_0, GLFW.GLFW_KEY_KP_0 -> 12;
            case GLFW.GLFW_KEY_SPACE -> 13;
            default -> new java.util.Random(key).nextInt(13);
        };
    }
}
