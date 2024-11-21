package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.AddMushroomIslandLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.DiagonalCrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerSampleContext;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AddMushroomIslandLayer.class)
public abstract class AddMushroomIslandLayerMixin implements DiagonalCrossSamplingLayer {
    @Shadow
    @Final
    private static int MUSHROOM_FIELDS_ID;

    @Override
    public int sample(LayerSampleContext<?> context, LayerSampler parent, int x, int z) {
        int center = parent.sample(this.transformX(x + 1), this.transformZ(z + 1));
        return BiomeLayers.isShallowOcean(center) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x), this.transformZ(z + 2))) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x + 2), this.transformZ(z + 2))) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x + 2), this.transformZ(z))) &&
                BiomeLayers.isShallowOcean(parent.sample(this.transformX(x), this.transformZ(z))) &&
                context.nextInt(100) == 0 ? MUSHROOM_FIELDS_ID : center;
    }
}
