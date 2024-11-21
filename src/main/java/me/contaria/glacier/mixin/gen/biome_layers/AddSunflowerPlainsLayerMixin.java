package me.contaria.glacier.mixin.gen.biome_layers;

import me.contaria.glacier.optimization.gen.biome_layers.GlacierCachingLayerContext;
import net.minecraft.world.biome.layer.AddSunflowerPlainsLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AddSunflowerPlainsLayer.class)
public abstract class AddSunflowerPlainsLayerMixin {

    @Shadow
    @Final
    private static int PLAINS_ID;

    @Shadow
    @Final
    private static int SUNFLOWER_PLAINS;

    /**
     * @author contaria
     * @reason Skip {@link CachingLayerContext#nextInt} when possible
     */
    @Overwrite
    public int sample(LayerRandomnessSource context, int se) {
        if (se != PLAINS_ID) {
            ((GlacierCachingLayerContext) context).glacier$skipInt();
            return se;
        }
        return context.nextInt(57) == 0 ? SUNFLOWER_PLAINS : se;
    }
}
