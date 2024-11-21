package me.contaria.glacier.mixin.gen.biome_layers;

import me.contaria.glacier.optimization.gen.biome_layers.GlacierCachingLayerContext;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.layer.AddHillsLayer;
import net.minecraft.world.biome.layer.BiomeLayers;
import net.minecraft.world.biome.layer.type.MergingLayer;
import net.minecraft.world.biome.layer.util.LayerRandomnessSource;
import net.minecraft.world.biome.layer.util.LayerSampler;
import net.minecraft.world.biome.layer.util.NorthWestCoordinateTransformer;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.*;

import static net.minecraft.world.biome.layer.BiomeLayers.*;

@Mixin(AddHillsLayer.class)
public abstract class AddHillsLayerMixin implements MergingLayer, NorthWestCoordinateTransformer {

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    @Final
    private static int DESERT_ID;

    @Shadow
    @Final
    private static int DESERT_HILLS_ID;

    @Shadow
    @Final
    private static int FOREST_ID;

    @Shadow
    @Final
    private static int WOODED_HILLS_ID;

    @Shadow
    @Final
    private static int BIRCH_FOREST_ID;

    @Shadow
    @Final
    private static int BIRCH_FOREST_HILLS_ID;

    @Shadow
    @Final
    private static int DARK_FOREST_ID;

    @Shadow
    @Final
    private static int PLAINS_ID;

    @Shadow
    @Final
    private static int TAIGA_ID;

    @Shadow
    @Final
    private static int TAIGA_HILLS_ID;

    @Shadow
    @Final
    private static int GIANT_TREE_TAIGA_ID;

    @Shadow
    @Final
    private static int GIANT_TREE_TAIGA_HILLS_ID;

    @Shadow
    @Final
    private static int SNOWY_TAIGA_ID;

    @Shadow
    @Final
    private static int SNOWY_TAIGA_HILLS_ID;

    @Shadow
    @Final
    private static int SNOWY_TUNDRA_ID;

    @Shadow
    @Final
    private static int SNOWY_MOUNTAINS_ID;

    @Shadow
    @Final
    private static int BADLANDS_ID;

    @Shadow
    @Final
    private static int WOODED_BADLANDS_PLATEAU_ID;

    @Shadow
    @Final
    private static int SAVANNA_PLATEAU_ID;

    @Shadow
    @Final
    private static int SAVANNA_ID;

    @Shadow
    @Final
    private static int WOODED_MOUNTAINS_ID;

    @Shadow
    @Final
    private static int MOUNTAINS_ID;

    @Shadow
    @Final
    private static int BAMBOO_JUNGLE_HILLS_ID;

    @Shadow
    @Final
    private static int BAMBOO_JUNGLE_ID;

    @Shadow
    @Final
    private static int JUNGLE_HILLS_ID;

    @Shadow
    @Final
    private static int JUNGLE_ID;

    /**
     * @author contaria
     * @reason Reduce sampling
     */
    @Overwrite
    public int sample(LayerRandomnessSource context, LayerSampler sampler1, LayerSampler sampler2, int x, int z) {
        int sample1 = sampler1.sample(this.transformX(x + 1), this.transformZ(z + 1));
        int sample2 = sampler2.sample(this.transformX(x + 1), this.transformZ(z + 1));
        if (sample1 > 255) {
            LOGGER.debug("old! {}", sample1);
        }

        int k = (sample2 - 2) % 29;
        if (sample2 >= 2 && k == 1 && !BiomeLayers.isShallowOcean(sample1)) {
            Biome biome = Registry.BIOME.get(sample1);
            if (biome == null || !biome.hasParent()) {
                Biome biome2 = Biome.getModifiedBiome(biome);
                return biome2 == null ? sample1 : Registry.BIOME.getRawId(biome2);
            }
        }

        if (context.nextInt(3) == 0 || k == 0) {
            int l = sample1;
            if (sample1 == DESERT_ID) {
                l = DESERT_HILLS_ID;
            } else if (sample1 == FOREST_ID) {
                l = WOODED_HILLS_ID;
            } else if (sample1 == BIRCH_FOREST_ID) {
                l = BIRCH_FOREST_HILLS_ID;
            } else if (sample1 == DARK_FOREST_ID) {
                l = PLAINS_ID;
            } else if (sample1 == TAIGA_ID) {
                l = TAIGA_HILLS_ID;
            } else if (sample1 == GIANT_TREE_TAIGA_ID) {
                l = GIANT_TREE_TAIGA_HILLS_ID;
            } else if (sample1 == SNOWY_TAIGA_ID) {
                l = SNOWY_TAIGA_HILLS_ID;
            } else if (sample1 == PLAINS_ID) {
                l = context.nextInt(3) == 0 ? WOODED_HILLS_ID : FOREST_ID;
            } else if (sample1 == SNOWY_TUNDRA_ID) {
                l = SNOWY_MOUNTAINS_ID;
            } else if (sample1 == JUNGLE_ID) {
                l = JUNGLE_HILLS_ID;
            } else if (sample1 == BAMBOO_JUNGLE_ID) {
                l = BAMBOO_JUNGLE_HILLS_ID;
            } else if (sample1 == OCEAN_ID) {
                l = DEEP_OCEAN_ID;
            } else if (sample1 == LUKEWARM_OCEAN_ID) {
                l = DEEP_LUKEWARM_OCEAN_ID;
            } else if (sample1 == COLD_OCEAN_ID) {
                l = DEEP_COLD_OCEAN_ID;
            } else if (sample1 == FROZEN_OCEAN_ID) {
                l = DEEP_FROZEN_OCEAN_ID;
            } else if (sample1 == MOUNTAINS_ID) {
                l = WOODED_MOUNTAINS_ID;
            } else if (sample1 == SAVANNA_ID) {
                l = SAVANNA_PLATEAU_ID;
            } else if (BiomeLayers.areSimilar(sample1, WOODED_BADLANDS_PLATEAU_ID)) {
                l = BADLANDS_ID;
            } else if ((sample1 == DEEP_OCEAN_ID || sample1 == DEEP_LUKEWARM_OCEAN_ID || sample1 == DEEP_COLD_OCEAN_ID || sample1 == DEEP_FROZEN_OCEAN_ID) && context.nextInt(3) == 0) {
                l = ((GlacierCachingLayerContext) context).glacier$nextBoolean() ? PLAINS_ID : FOREST_ID;
            }

            if (k == 0 && l != sample1) {
                Biome biome2 = Biome.getModifiedBiome(Registry.BIOME.get(l));
                if (biome2 == null) {
                    return sample1;
                }
                l = Registry.BIOME.getRawId(biome2);
            }

            if (isSurroundedByEnoughSimilarBiomes(sample1, sampler1, this.transformX(x), this.transformX(x + 1), this.transformX(x + 2), this.transformZ(z), this.transformZ(z + 1), this.transformZ(z + 2))) {
                return l;
            }
        }
        return sample1;
    }

    @Unique
    private static boolean isSurroundedByEnoughSimilarBiomes(int id, LayerSampler sampler, int x, int x1, int x2, int z, int z1, int z2) {
        if (areSimilar(sampler.sample(x1, z), id)) {
            if (areSimilar(sampler.sample(x2, z1), id)) {
                return areSimilar(sampler.sample(x, z1), id) || areSimilar(sampler.sample(x1, z2), id);
            }
            return areSimilar(sampler.sample(x, z1), id) && areSimilar(sampler.sample(x1, z2), id);
        }
        return areSimilar(sampler.sample(x2, z1), id) && areSimilar(sampler.sample(x, z1), id) && areSimilar(sampler.sample(x1, z2), id);
    }
}
