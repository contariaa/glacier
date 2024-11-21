package me.contaria.glacier.mixin.gen.biome_layers;

import me.contaria.glacier.optimization.gen.biome_layers.GlacierCachingLayerContext;
import net.minecraft.world.biome.layer.SmoothenShorelineLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SmoothenShorelineLayer.class)
public abstract class SmoothenShorelineLayerMixin {

    /**
     * @author contaria
     * @reason Replace {@link CachingLayerContext#nextInt} with {@link GlacierCachingLayerContext#glacier$nextBoolean}
     */
    @Overwrite
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (e == w) {
            if (n == s) {
                return ((GlacierCachingLayerContext) context).glacier$nextBoolean() ? w : n;
            }
            return w;
        }
        if (n == s) {
            return n;
        }
        return center;
    }
}
