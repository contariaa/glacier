package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.AddClimateLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AddClimateLayers.AddTemperateBiomesLayer.class)
public abstract class AddClimateLayers$AddTemperatureBiomesLayerMixin implements CrossSamplingLayer {

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int center = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        if (center != 1) {
            return center;
        }
        int n = parent.sample(this.transformX(x + 1), this.transformZ(z));
        if (n == 3 || n == 4) {
            return 2;
        }
        int e = parent.sample(this.transformX(x + 2), this.transformZ(z + 1));
        if (e == 3 || e == 4) {
            return 2;
        }
        int s = parent.sample(this.transformX(x + 1), this.transformZ(z + 2));
        if (s == 3 || s == 4) {
            return 3;
        }
        int w = parent.sample(this.transformX(x), this.transformZ(z + 1));
        if (w == 3 || w == 4) {
            return 2;
        }
        return center;
    }
}
