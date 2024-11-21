package me.contaria.glacier.mixin.gen.biome_layers;

import me.contaria.glacier.optimization.gen.biome_layers.GlacierCachingLayerContext;
import net.minecraft.world.biome.layer.AddIslandLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AddIslandLayer.class)
public abstract class AddIslandLayerMixin implements CrossSamplingLayer {

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int center = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        return BiomeLayers.isShallowOcean(center) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x + 1), this.transformZ(z))) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x + 2), this.transformZ(z + 1))) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x + 1), this.transformZ(z + 2))) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x), this.transformZ(z + 1))) &&
                ((GlacierCachingLayerContext) context).glacier$nextBoolean() ? 1 : center;
    }
}
