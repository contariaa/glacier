package me.contaria.glacier.mixin.memory.chunk.biome_array_data;

import me.contaria.glacier.optimization.memory.biome_array_data.BiomeArrayData;
import me.contaria.glacier.optimization.memory.biome_array_data.GlacierBiomeArray;
import me.contaria.glacier.optimization.memory.biome_array_data.SingleBiomeArrayData;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.chunk.EmptyChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.Consumer;

/**
 * @see GlacierBiomeArray
 */
@Mixin(EmptyChunk.class)
public abstract class EmptyChunkMixin {
    @Unique
    private static final BiomeArrayData EMPTY_BIOME_DATA = new SingleBiomeArrayData(Biomes.PLAINS, BiomeArray.DEFAULT_LENGTH);

    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Util;make(Ljava/lang/Object;Ljava/util/function/Consumer;)Ljava/lang/Object;"
            )
    )
    private static Object doNotCreateEmptyBiomeArray(Object object, Consumer<?> initializer) {
        return null;
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "([Lnet/minecraft/world/biome/Biome;)Lnet/minecraft/world/biome/source/BiomeArray;"
            )
    )
    private static BiomeArray useGlacierBiomeArray(Biome[] data) {
        return new GlacierBiomeArray(EMPTY_BIOME_DATA);
    }
}
