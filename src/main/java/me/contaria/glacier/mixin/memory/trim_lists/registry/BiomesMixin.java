package me.contaria.glacier.mixin.memory.trim_lists.registry;

import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Biomes.class)
public abstract class BiomesMixin {

    @Inject(
            method = "<clinit>",
            at = @At("TAIL")
    )
    private static void trimParentBiomeIdMap(CallbackInfo ci) {
        ((GlacierTrimmable) Biome.PARENT_BIOME_ID_MAP).glacier$trim();
    }
}
