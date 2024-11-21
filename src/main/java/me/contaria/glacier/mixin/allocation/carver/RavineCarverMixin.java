package me.contaria.glacier.mixin.allocation.carver;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.RavineCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

/**
 * @see CarverMixin
 */
@Mixin(RavineCarver.class)
public abstract class RavineCarverMixin extends CarverMixin {

    @Shadow
    @Final
    private float[] heightToHorizontalStretchFactor;

    /**
     * @author contaria
     * @reason Reduce BlockPos.Mutable and MutableBoolean allocations.
     */
    @Overwrite
    private void carveRavine(
            Chunk chunk,
            Function<BlockPos, Biome> posToBiome,
            long seed,
            int seaLevel,
            int mainChunkX,
            int mainChunkZ,
            double x,
            double y,
            double z,
            float width,
            float yaw,
            float pitch,
            int branchStartIndex,
            int branchCount,
            double yawPitchRatio,
            BitSet carvingMask
    ) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        BlockPos.Mutable mutable3 = new BlockPos.Mutable();
        MutableBoolean mutableBoolean = new MutableBoolean();
        Random carverRandom = new Random(0);

        Random random = new Random(seed);
        float f = 1.0F;

        for (int i = 0; i < 256; i++) {
            if (i == 0 || random.nextInt(3) == 0) {
                f = 1.0F + random.nextFloat() * random.nextFloat();
            }

            this.heightToHorizontalStretchFactor[i] = f * f;
        }

        float g = 0.0F;
        float h = 0.0F;

        for (int branch = branchStartIndex; branch < branchCount; branch++) {
            double d = 1.5 + (double) (MathHelper.sin((float) branch * (float) Math.PI / (float) branchCount) * width);
            double e = d * yawPitchRatio;
            d *= (double) random.nextFloat() * 0.25 + 0.75;
            e *= (double) random.nextFloat() * 0.25 + 0.75;
            float k = MathHelper.cos(pitch);
            x += MathHelper.cos(yaw) * k;
            y += MathHelper.sin(pitch);
            z += MathHelper.sin(yaw) * k;
            pitch *= 0.7F;
            pitch += h * 0.05F;
            yaw += g * 0.05F;
            h *= 0.8F;
            g *= 0.5F;
            h += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
            if (random.nextInt(4) != 0) {
                if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, branch, branchCount, width)) {
                    return;
                }

                this.carveRegion(chunk, posToBiome, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, d, e, carvingMask, mutable, mutable2, mutable3, mutableBoolean, carverRandom);
            }
        }
    }
}
