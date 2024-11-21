package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddEdgeBiomesLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.CrossSamplingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(AddEdgeBiomesLayer.class)
public abstract class AddEdgeBiomesLayerMixin implements CrossSamplingLayer {

    @Shadow
    @Final
    private static int JUNGLE_EDGE_ID;

    @Shadow
    @Final
    private static int JUNGLE_ID;

    @Shadow
    @Final
    private static int JUNGLE_HILLS_ID;

    @Shadow
    @Final
    private static int FOREST_ID;

    @Shadow
    @Final
    private static int TAIGA_ID;

    @Shadow
    @Final
    private static int MUSHROOM_FIELDS_ID;

    @Shadow
    @Final
    private static int MUSHROOM_FIELD_SHORE_ID;

    @Shadow
    @Final
    private static int BEACH_ID;

    @Shadow
    @Final
    private static int MOUNTAINS_ID;

    @Shadow
    @Final
    private static int WOODED_MOUNTAINS_ID;

    @Shadow
    @Final
    private static int MOUNTAIN_EDGE_ID;

    @Shadow
    @Final
    private static int SNOWY_BEACH_ID;

    @Shadow
    @Final
    private static int WOODED_BADLANDS_PLATEAU_ID;

    @Shadow
    @Final
    private static int BADLANDS_ID;

    @Shadow
    @Final
    private static int RIVER_ID;

    @Shadow
    @Final
    private static int SWAMP_ID;

    @Shadow
    @Final
    private static int DESERT_ID;

    @Shadow
    @Final
    private static int STONE_SHORE_ID;

    @Shadow
    protected abstract boolean isBadlands(int id);

    /**
     * @author contaria
     * @reason Avoid expensive registry lookup if possible
     */
    @Overwrite
    public int sample(LayerRandomnessSource context, int n, int e, int s, int w, int center) {
        if (center == MUSHROOM_FIELDS_ID) {
            if (BiomeLayers.isShallowOcean(n) || BiomeLayers.isShallowOcean(e) || BiomeLayers.isShallowOcean(s) || BiomeLayers.isShallowOcean(w)) {
                return MUSHROOM_FIELD_SHORE_ID;
            }
        } else {
            Biome biome = Registry.BIOME.get(center);
            if (biome != null && biome.getCategory() == Biome.Category.JUNGLE) {
                if (!isWooded(n) || !isWooded(e) || !isWooded(s) || !isWooded(w)) {
                    return JUNGLE_EDGE_ID;
                }

                if (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w)) {
                    return BEACH_ID;
                }
            } else if (center != MOUNTAINS_ID && center != WOODED_MOUNTAINS_ID && center != MOUNTAIN_EDGE_ID) {
                if (biome != null && biome.getPrecipitation() == Biome.Precipitation.SNOW) {
                    if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
                        return SNOWY_BEACH_ID;
                    }
                } else if (center != BADLANDS_ID && center != WOODED_BADLANDS_PLATEAU_ID) {
                    if (!BiomeLayers.isOcean(center) && center != RIVER_ID && center != SWAMP_ID && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
                        return BEACH_ID;
                    }
                } else if (!BiomeLayers.isOcean(n) && !BiomeLayers.isOcean(e) && !BiomeLayers.isOcean(s) && !BiomeLayers.isOcean(w) && (!this.isBadlands(n) || !this.isBadlands(e) || !this.isBadlands(s) || !this.isBadlands(w))) {
                    return DESERT_ID;
                }
            } else if (!BiomeLayers.isOcean(center) && (BiomeLayers.isOcean(n) || BiomeLayers.isOcean(e) || BiomeLayers.isOcean(s) || BiomeLayers.isOcean(w))) {
                return STONE_SHORE_ID;
            }
        }

        return center;
    }

    /**
     * @author contaria
     * @reason Avoid expensive registry lookup if possible
     */
    @Overwrite
    private static boolean isWooded(int id) {
        if (id == JUNGLE_EDGE_ID || id == JUNGLE_ID || id == JUNGLE_HILLS_ID || id == FOREST_ID || id == TAIGA_ID || BiomeLayers.isOcean(id)) {
            return true;
        }
        Biome biome = Registry.BIOME.get(id);
        return biome != null && biome.getCategory() == Biome.Category.JUNGLE;
    }
}
