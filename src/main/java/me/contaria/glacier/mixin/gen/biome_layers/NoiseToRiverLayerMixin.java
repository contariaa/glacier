package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.NoiseToRiverLayer;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseToRiverLayer.class)
public abstract class NoiseToRiverLayerMixin implements CrossSamplingLayer {
    @Shadow
    @Final
    public static int RIVER_ID;

    @Shadow
    private static int isValidForRiver(int value) {
        return 0;
    }

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int i = isValidForRiver(parent.sample(this.transformX(x + 1), this.transformZ(z + 1)));
        return i == isValidForRiver(parent.sample(this.transformX(x), this.transformZ(z + 1))) &&
                i == isValidForRiver(parent.sample(this.transformX(x + 1), this.transformZ(z))) &&
                i == isValidForRiver(parent.sample(this.transformX(x + 2), this.transformZ(z + 1))) &&
                i == isValidForRiver(parent.sample(this.transformX(x + 1), this.transformZ(z + 2))) ? -1 : RIVER_ID;
    }
}
