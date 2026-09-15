package org.sparklezfish.drivebywire.typewriter.mixin.client;

import edn.stratodonut.drivebywire.client.ClientWireNetworkHandler;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClientWireNetworkHandler.class, remap = false)
public interface ClientWireNetworkHandlerAccessor {
    @Accessor("selectedSource")
    static BlockPos typewriter$getSelectedSource() {
        throw new AssertionError();
    }

    @Accessor("currentChannel")
    static void typewriter$setCurrentChannel(String channel) {
        throw new AssertionError();
    }
}
