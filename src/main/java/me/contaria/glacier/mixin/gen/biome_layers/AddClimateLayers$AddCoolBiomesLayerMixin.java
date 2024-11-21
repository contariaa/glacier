package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.AddClimateLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AddClimateLayers.AddCoolBiomesLayer.class)
public abstract class AddClimateLayers$AddCoolBiomesLayerMixin implements CrossSamplingLayer {

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int center = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        if (center != 4) {
            return center;
        }
        int n = parent.sample(this.transformX(x + 1), this.transformZ(z));
        if (n == 1 || n == 2) {
            return 3;
        }
        int e = parent.sample(this.transformX(x + 2), this.transformZ(z + 1));
        if (e == 1 || e == 2) {
            return 3;
        }
        int s = parent.sample(this.transformX(x + 1), this.transformZ(z + 2));
        if (s == 1 || s == 2) {
            return 3;
        }
        int w = parent.sample(this.transformX(x), this.transformZ(z + 1));
        if (w == 1 || w == 2) {
            return 3;
        }
        return center;
    }
}
