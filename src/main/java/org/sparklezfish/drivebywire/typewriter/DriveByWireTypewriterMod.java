package org.sparklezfish.drivebywire.typewriter;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import org.slf4j.Logger;
import org.sparklezfish.drivebywire.typewriter.blocks.TypewriterHubBlock;
import org.sparklezfish.drivebywire.typewriter.blocks.TypewriterHubBlockEntity;
import org.sparklezfish.drivebywire.typewriter.network.TypewriterHubDisconnectPacket;
import org.sparklezfish.drivebywire.typewriter.network.TypewriterHubKeyPacket;

@Mod(DriveByWireTypewriterMod.MODID)
public class DriveByWireTypewriterMod {

    public static final String MODID = "drivebywiretypewriter";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredBlock<TypewriterHubBlock> TYPEWRITER_HUB = BLOCKS.register(
        "typewriter_hub",
        () -> new TypewriterHubBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.5f, 3.0f)
            .sound(SoundType.WOOD)
            .noOcclusion())
    );
    public static final DeferredItem<BlockItem> TYPEWRITER_HUB_ITEM = ITEMS.registerSimpleBlockItem(TYPEWRITER_HUB);
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register(
        "main",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.drivebywiretypewriter"))
            .icon(() -> TYPEWRITER_HUB_ITEM.get().getDefaultInstance())
            .displayItems((params, output) -> output.accept(TYPEWRITER_HUB_ITEM.get()))
            .build()
    );


    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TypewriterHubBlockEntity>>
        TYPEWRITER_HUB_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(
        "typewriter_hub",
        () -> BlockEntityType.Builder.of(TypewriterHubBlockEntity::create, TYPEWRITER_HUB.get()).build(null)
    );

    public DriveByWireTypewriterMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerPayloads);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");
        registrar.playToServer(
            TypewriterHubKeyPacket.TYPE,
            TypewriterHubKeyPacket.CODEC,
            TypewriterHubKeyPacket::handle
        );
        registrar.playToServer(
            TypewriterHubDisconnectPacket.TYPE,
            TypewriterHubDisconnectPacket.CODEC,
            TypewriterHubDisconnectPacket::handle
        );
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info(
            "Drive By Wire Typewriter bridge loaded — {} channels registered.",
            TypewriterChannels.CHANNELS.size()
        );
    }
}
