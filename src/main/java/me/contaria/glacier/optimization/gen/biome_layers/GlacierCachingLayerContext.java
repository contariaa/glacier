package me.contaria.glacier.optimization.gen.biome_layers;

import net.minecraft.world.biome.layer.util.CachingLayerContext;

/**
 * Used to simulate {@link CachingLayerContext#nextInt} calls without the extra calculations.
 */
public interface GlacierCachingLayerContext {

    void glacier$skipInt();

    boolean glacier$nextBoolean();
}
