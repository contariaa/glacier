package me.contaria.glacier.mixin.gen.biome_layers;

import me.contaria.glacier.optimization.gen.biome_layers.GlacierCachingLayerContext;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.source.SeedMixer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CachingLayerContext.class)
public abstract class CachingLayerContextMixin implements GlacierCachingLayerContext {

    @Shadow
    @Final
    private long worldSeed;

    @Shadow
    private long localSeed;

    @Override
    public void glacier$skipInt() {
        this.localSeed = SeedMixer.mixSeed(this.localSeed, this.worldSeed);
    }

    @Override
    public boolean glacier$nextBoolean() {
        boolean bl = (this.localSeed & 16777216) == 0;
        this.localSeed = SeedMixer.mixSeed(this.localSeed, this.worldSeed);
        return bl;
    }
}
