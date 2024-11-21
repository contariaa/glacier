package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.world.biome.layer.AddRiversLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.util.IdentityCoordinateTransformer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AddRiversLayer.class)
public abstract class AddRiversLayerMixin implements IdentityCoordinateTransformer {

    @Shadow
    @Final
    private static int RIVER_ID;

    @Shadow
    @Final
    private static int SNOWY_TUNDRA_ID;

    @Shadow
    @Final
    private static int FROZEN_RIVER_ID;

    @Shadow
    @Final
    private static int MUSHROOM_FIELDS_ID;

    @Shadow
    @Final
    private static int MUSHROOM_FIELD_SHORE_ID;

    /**
     * @author contaria
     * @reason Reduce sampling
     */
    @Overwrite
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int sample1 = sampler1.sample(this.transformX(x), this.transformZ(z));
        if (BiomeLayers.isOcean(sample1)) {
            return sample1;
        }
        int sample2 = sampler2.sample(this.transformX(x), this.transformZ(z));
        if (sample2 == RIVER_ID) {
            if (sample1 == SNOWY_TUNDRA_ID) {
                return FROZEN_RIVER_ID;
            }
            return sample1 != MUSHROOM_FIELDS_ID && sample1 != MUSHROOM_FIELD_SHORE_ID ? sample2 & 0xFF : MUSHROOM_FIELD_SHORE_ID;
        }
        return sample1;
    }
}
