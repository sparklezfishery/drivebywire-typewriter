package org.sparklezfish.drivebywire.typewriter.blocks;

import com.mojang.serialization.MapCodec;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlock;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.mixin_interface.PlayerTypewriterExtension;
import edn.stratodonut.drivebywire.wire.MultiChannelWireSource;
import org.sparklezfish.drivebywire.typewriter.DriveByWireTypewriterMod;
import org.sparklezfish.drivebywire.typewriter.TypewriterChannels;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;
import java.util.UUID;

public class TypewriterHubBlock extends LinkedTypewriterBlock implements MultiChannelWireSource {

    public static final MapCodec<TypewriterHubBlock> CODEC = simpleCodec(TypewriterHubBlock::new);

    public TypewriterHubBlock(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntityType<? extends LinkedTypewriterBlockEntity> getBlockEntityType() {
        return DriveByWireTypewriterMod.TYPEWRITER_HUB_BLOCK_ENTITY.get();
    }

    @Override
    public List<String> wire$getChannels() {
        return TypewriterChannels.ALL_CHANNEL_DISPLAYS.stream().map(Component::getString).toList();
    }

    @Override
    public String wire$nextChannel(String current, boolean forward) {
        List<String> ch = wire$getChannels();
        int idx = ch.indexOf(current);
        if (idx == -1) return ch.getFirst();
        return ch.get(Math.floorMod(idx + (forward ? 1 : -1), ch.size()));
    }

    @Override
    protected ItemInteractionResult useItemOn(
            ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit
    ) {
        if (stack.isEmpty() && hand == InteractionHand.MAIN_HAND) {
            var be = level.getBlockEntity(pos);
            if (!(be instanceof LinkedTypewriterBlockEntity typewriter))
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            UUID uuid = player.getUUID();
            if (typewriter.checkAndStartUsing(uuid)) {
                if (!level.isClientSide) {
                    level.setBlockAndUpdate(pos, state.setValue(LinkedTypewriterBlock.POWERED, true));
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            if (typewriter.checkUser(uuid)) {
                ((PlayerTypewriterExtension) player).simulated$setCurrentTypewriter(null);
                typewriter.disconnectUser();
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }
}
