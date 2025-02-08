package me.contaria.glacier.mixin.memory.chunk.biome_array_data;

import me.contaria.glacier.optimization.memory.biome_array_data.GlacierBiomeArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @see GlacierBiomeArray
 */
@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {

    @Redirect(
            method = "deserialize",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/source/BiomeSource;[I)Lnet/minecraft/world/biome/source/BiomeArray;"
            )
    )
    private static BiomeArray useGlacierBiomeArray(ChunkPos pos, BiomeSource source, int[] rawIds) {
        return new GlacierBiomeArray(pos, source, rawIds);
    }
}
