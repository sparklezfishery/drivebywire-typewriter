package org.sparklezfish.drivebywire.typewriter;

import net.neoforged.neoforge.common.ModConfigSpec;

final class TypewriterClientConfig {
    static final ModConfigSpec SPEC;
    static final ModConfigSpec.IntValue CHANNEL_SELECTION_HOLD_MS;

    static {
        var builder = new ModConfigSpec.Builder();
        CHANNEL_SELECTION_HOLD_MS = builder
                .translation("drivebywiretypewriter.configuration.channelSelectionHoldMs")
                .comment("How long to hold the channel selection key before opening the keyboard overlay, in milliseconds.",
                        "Set to 0 to open immediately.")
                .defineInRange("channelSelectionHoldMs", 300, 0, 5000);
        SPEC = builder.build();
    }

    private TypewriterClientConfig() { }
}
