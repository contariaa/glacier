package me.contaria.glacier.mixin.gen.biome_layers;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.layer.BiomeLayers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BiomeLayers.class)
public abstract class BiomeLayersMixin {
    @Unique
    private static final int WOODED_BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.WOODED_BADLANDS_PLATEAU);
    @Unique
    private static final int BADLANDS_PLATEAU_ID = Registry.BIOME.getRawId(Biomes.BADLANDS_PLATEAU);

    /**
     * @author contaria
     * @reason Avoid expensive registry lookup if possible
     */
    @Overwrite
    public static boolean areSimilar(int id1, int id2) {
        if (id1 == id2) {
            return true;
        }
        if (id1 == WOODED_BADLANDS_PLATEAU_ID || id1 == BADLANDS_PLATEAU_ID) {
            return id2 == WOODED_BADLANDS_PLATEAU_ID || id2 == BADLANDS_PLATEAU_ID;
        }
        Biome biome = Registry.BIOME.get(id1);
        Biome biome2 = Registry.BIOME.get(id2);
        return (biome != null && biome2 != null) &&
                (biome == biome2 || (biome.getCategory() != Biome.Category.NONE && biome.getCategory() == biome2.getCategory()));
    }
}
