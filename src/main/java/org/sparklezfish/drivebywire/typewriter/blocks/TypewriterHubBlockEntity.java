package org.sparklezfish.drivebywire.typewriter.blocks;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlock;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import edn.stratodonut.drivebywire.wire.WireNetworkManager;
import org.sparklezfish.drivebywire.typewriter.DriveByWireTypewriterMod;
import org.sparklezfish.drivebywire.typewriter.TypewriterChannels;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class TypewriterHubBlockEntity extends LinkedTypewriterBlockEntity {

    public TypewriterHubBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static TypewriterHubBlockEntity create(BlockPos pos, BlockState state) {
        return new TypewriterHubBlockEntity(DriveByWireTypewriterMod.TYPEWRITER_HUB_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void disconnectUser() {
        var level = getLevel();
        if (level != null && !level.isClientSide) {
            for (Component d : TypewriterChannels.ALL_CHANNEL_DISPLAYS) {
                WireNetworkManager.trySetSignalAt(level, getBlockPos(), d.getString(), 0);
            }
        }
        super.disconnectUser();
        if (level != null && !level.isClientSide) {
            level.setBlockAndUpdate(
                    getBlockPos(),
                    level.getBlockState(getBlockPos()).setValue(LinkedTypewriterBlock.POWERED, false)
            );
        }
    }

    @Override
    public void onKeyInteraction(
            UUID user, LinkedTypewriterEntries.KeyboardEntry toBind, int key, boolean press
    ) {
        // not current user
        if (!this.checkUser(user)) return;

        // Skip key-binding mode (toBind != null means player is assigning a key slot, not typing)
        if (toBind != null) return;

        var level = this.getLevel();
        if (level == null || level.isClientSide) return;

        var channel = TypewriterChannels.DISPLAY_MAP.get(key);

        if (channel == null) return;

        WireNetworkManager.trySetSignalAt(level, this.getBlockPos(), channel.getString(), press ? 15 : 0);
    }
}
