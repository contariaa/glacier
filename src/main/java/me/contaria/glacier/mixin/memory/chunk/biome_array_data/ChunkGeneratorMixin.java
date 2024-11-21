package me.contaria.glacier.mixin.memory.chunk.biome_array_data;

import me.contaria.glacier.optimization.memory.biome_array_data.GlacierBiomeArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Redirect(
            method = "populateBiomes",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/util/math/ChunkPos;Lnet/minecraft/world/biome/source/BiomeSource;)Lnet/minecraft/world/biome/source/BiomeArray;"
            )
    )
    private BiomeArray useGlacierBiomeArray(ChunkPos pos, BiomeSource source) {
        return new GlacierBiomeArray(pos, source);
    }
}
