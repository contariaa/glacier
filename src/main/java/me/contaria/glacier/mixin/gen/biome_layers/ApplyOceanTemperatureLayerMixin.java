package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.ApplyOceanTemperatureLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ApplyOceanTemperatureLayer.class)
public abstract class ApplyOceanTemperatureLayerMixin implements IdentityCoordinateTransformer {

    /**
     * @author contaria
     * @reason Reduce sampling
     */
    @Overwrite
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int sample1 = sampler1.sample(this.transformX(x), this.transformZ(z));
        if (!BiomeLayers.isOcean(sample1)) {
            return sample1;
        }
        int sample2 = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (sample2 == BiomeLayers.WARM_OCEAN_ID || sample2 == BiomeLayers.FROZEN_OCEAN_ID) {
            for (int x1 = -8; x1 <= 8; x1 += 4) {
                int transformX1 = this.transformX(x + x1);
                for (int z1 = -8; z1 <= 8; z1 += 4) {
                    if ((x1 != 0 || z1 != 0) && !BiomeLayers.isOcean(sampler1.sample(transformX1, this.transformZ(z + z1)))) {
                        if (sample2 == BiomeLayers.WARM_OCEAN_ID) {
                            return BiomeLayers.LUKEWARM_OCEAN_ID;
                        }
                        return BiomeLayers.COLD_OCEAN_ID;
                    }
                }
            }
        }

        if (sample1 == BiomeLayers.DEEP_OCEAN_ID) {
            if (sample2 == BiomeLayers.LUKEWARM_OCEAN_ID) {
                return BiomeLayers.DEEP_LUKEWARM_OCEAN_ID;
            }
            if (sample2 == BiomeLayers.OCEAN_ID) {
                return BiomeLayers.DEEP_OCEAN_ID;
            }
            if (sample2 == BiomeLayers.COLD_OCEAN_ID) {
                return BiomeLayers.DEEP_COLD_OCEAN_ID;
            }
            if (sample2 == BiomeLayers.FROZEN_OCEAN_ID) {
                return BiomeLayers.DEEP_FROZEN_OCEAN_ID;
            }
        }

        return sample2;
    }
}
