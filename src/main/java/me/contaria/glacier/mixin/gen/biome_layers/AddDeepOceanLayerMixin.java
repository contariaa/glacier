package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.AddDeepOceanLayer;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.minecraft.world.biome.layer.BiomeLayers.*;

@Mixin(AddDeepOceanLayer.class)
public abstract class AddDeepOceanLayerMixin implements CrossSamplingLayer {

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int center = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        if (isShallowOcean(center) && this.isSurroundedByEnoughShallowOcean(parent, x, z)) {
            if (center == WARM_OCEAN_ID) {
                return DEEP_WARM_OCEAN_ID;
            }
            if (center == LUKEWARM_OCEAN_ID) {
                return DEEP_LUKEWARM_OCEAN_ID;
            }
            if (center == COLD_OCEAN_ID) {
                return DEEP_COLD_OCEAN_ID;
            }
            if (center == FROZEN_OCEAN_ID) {
                return DEEP_FROZEN_OCEAN_ID;
            }
            return DEEP_OCEAN_ID;
        }
        return center;
    }

    @Unique
    private boolean isSurroundedByEnoughShallowOcean(LayerSampler parent, int x, int z) {
        if (isShallowOcean(parent.sample(this.transformX(x + 1), this.transformZ(z)))) {
            if (isShallowOcean(parent.sample(this.transformX(x + 2), this.transformZ(z + 1)))) {
                return isShallowOcean(parent.sample(this.transformX(x + 1), this.transformZ(z + 2))) || isShallowOcean(parent.sample(this.transformX(x), this.transformZ(z + 1)));
            }
            return isShallowOcean(parent.sample(this.transformX(x + 1), this.transformZ(z + 2))) && isShallowOcean(parent.sample(this.transformX(x), this.transformZ(z + 1)));
        }
        return isShallowOcean(parent.sample(this.transformX(x + 2), this.transformZ(z + 1))) && isShallowOcean(parent.sample(this.transformX(x + 1), this.transformZ(z + 2))) && isShallowOcean(parent.sample(this.transformX(x), this.transformZ(z + 1)));
    }
}
