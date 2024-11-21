package me.contaria.glacier.mixin.gen.biome_layers;

import me.contaria.glacier.optimization.gen.biome_layers.GlacierCachingLayerContext;
import net.minecraft.world.biome.layer.AddBambooJungleLayer;
import net.minecraft.world.biome.layer.util.CachingLayerContext;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AddBambooJungleLayer.class)
public abstract class AddBambooJungleLayerMixin {

    @Shadow
    @Final
    private static int JUNGLE_ID;

    @Shadow
    @Final
    private static int BAMBOO_JUNGLE_ID;

    /**
     * @author contaria
     * @reason Skip {@link CachingLayerContext#nextInt} when possible
     */
    @Overwrite
    public int sample(LayerRandomnessSource context, int se) {
        if (se != JUNGLE_ID) {
            ((GlacierCachingLayerContext) context).glacier$skipInt();
            return se;
        }
        return context.nextInt(10) == 0 ? BAMBOO_JUNGLE_ID : se;
    }
}
