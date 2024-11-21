package me.contaria.glacier.mixin.memory.trim_lists.registry;

import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Fluids.class)
public abstract class FluidsMixin {

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void trimFluidStateIds(CallbackInfo ci) {
        ((GlacierTrimmable) Fluid.STATE_IDS).glacier$trim();
    }
}
