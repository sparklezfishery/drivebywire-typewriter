package org.sparklezfish.drivebywire.typewriter.network;

import edn.stratodonut.drivebywire.wire.WireNetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import org.jetbrains.annotations.NotNull;
import org.sparklezfish.drivebywire.typewriter.DriveByWireTypewriterMod;
import org.sparklezfish.drivebywire.typewriter.TypewriterChannels;
import org.sparklezfish.drivebywire.typewriter.blocks.TypewriterHubBlockEntity;

public record TypewriterHubKeyPacket(BlockPos pos, String channel, boolean press)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<TypewriterHubKeyPacket> TYPE =
        new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(
            DriveByWireTypewriterMod.MODID, "key_input"));

    public static final StreamCodec<FriendlyByteBuf, TypewriterHubKeyPacket> CODEC =
            StreamCodec.of(
                    (buf, pkt) -> {
                        buf.writeBlockPos(pkt.pos);
                        buf.writeUtf(pkt.channel);
                        buf.writeBoolean(pkt.press);
                    },
                    buf -> new TypewriterHubKeyPacket(buf.readBlockPos(), buf.readUtf(), buf.readBoolean())
            );

    @Override
    public CustomPacketPayload.@NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            var level = player.level();
            var be = level.getBlockEntity(pos);
            if (!(be instanceof TypewriterHubBlockEntity hub)) return;
            if (!hub.checkUser(player.getUUID())) return;
            if (!TypewriterChannels.isKnownChannel(channel)) return;
            WireNetworkManager.trySetSignalAt(level, pos, channel, press ? 15 : 0);
        });
    }
}
