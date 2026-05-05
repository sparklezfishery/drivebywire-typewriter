package org.sparklezfish.drivebywire.typewriter.network;

import dev.simulated_team.simulated.mixin_interface.PlayerTypewriterExtension;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.sparklezfish.drivebywire.typewriter.DriveByWireTypewriterMod;
import org.sparklezfish.drivebywire.typewriter.blocks.TypewriterHubBlockEntity;

public record TypewriterHubDisconnectPacket(BlockPos pos) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TypewriterHubDisconnectPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            DriveByWireTypewriterMod.MODID, "disconnect"));

    public static final StreamCodec<FriendlyByteBuf, TypewriterHubDisconnectPacket> CODEC =
        StreamCodec.of(
            (buf, pkt) -> buf.writeBlockPos(pkt.pos),
            buf -> new TypewriterHubDisconnectPacket(buf.readBlockPos())
        );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            var be = player.level().getBlockEntity(pos);
            if (!(be instanceof TypewriterHubBlockEntity hub)) return;
            if (!hub.checkUser(player.getUUID())) return;
            ((PlayerTypewriterExtension) player).simulated$setCurrentTypewriter(null);
            hub.disconnectUser();
        });
    }
}
