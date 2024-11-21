package me.contaria.glacier.mixin.memory.trim_lists.registry;

import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void trimBlockStateIds(CallbackInfo ci) {
        ((GlacierTrimmable) Block.STATE_IDS).glacier$trim();
    }
}
