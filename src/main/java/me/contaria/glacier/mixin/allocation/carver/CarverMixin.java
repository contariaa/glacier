package me.contaria.glacier.mixin.allocation.carver;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.carver.Carver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

/**
 * Carving creates a lot of {@link BlockPos.Mutable}, {@link MutableBoolean} and {@link Random} object allocations.
 * This optimization increases efforts to reuse these objects.
 */
@Mixin(Carver.class)
public abstract class CarverMixin {

    @Shadow
    @Final
    protected int heightLimit;

    @Shadow
    protected abstract boolean isPositionExcluded(double scaledRelativeX, double scaledRelativeY, double scaledRelativeZ, int y);

    @Shadow
    protected abstract boolean carveAtPoint(Chunk chunk, Function<BlockPos, Biome> posToBiome, BitSet carvingMask, Random random, BlockPos.Mutable mutable, BlockPos.Mutable mutable2, BlockPos.Mutable mutable3, int seaLevel, int mainChunkX, int mainChunkZ, int x, int z, int relativeX, int y, int relativeZ, MutableBoolean mutableBoolean);

    @Shadow
    protected abstract boolean isRegionUncarvable(Chunk chunk, int mainChunkX, int mainChunkZ, int relMinX, int relMaxX, int minY, int maxY, int relMinZ, int relMaxZ);

    @Shadow
    protected abstract boolean canCarveBranch(int mainChunkX, int mainChunkZ, double x, double z, int branch, int branchCount, float baseWidth);

    @Shadow
    public abstract int getBranchFactor();

    // [VanillaCopy] Carver#carveRegion
    @Unique
    protected boolean carveRegion(
            Chunk chunk,
            Function<BlockPos, Biome> posToBiome,
            long seed,
            int seaLevel,
            int chunkX,
            int chunkZ,
            double x,
            double y,
            double z,
            double yaw,
            double pitch,
            BitSet carvingMask,
            BlockPos.Mutable mutable,
            BlockPos.Mutable mutable2,
            BlockPos.Mutable mutable3,
            MutableBoolean mutableBoolean,
            Random random
    ) {
        double d = chunkX * 16 + 8;
        double e = chunkZ * 16 + 8;
        if (!(x < d - 16.0 - yaw * 2.0) && !(z < e - 16.0 - yaw * 2.0) && !(x > d + 16.0 + yaw * 2.0) && !(z > e + 16.0 + yaw * 2.0)) {
            int x1 = Math.max(MathHelper.floor(x - yaw) - chunkX * 16 - 1, 0);
            int x2 = Math.min(MathHelper.floor(x + yaw) - chunkX * 16 + 1, 16);
            int y1 = Math.max(MathHelper.floor(y - pitch) - 1, 1);
            int y2 = Math.min(MathHelper.floor(y + pitch) + 1, this.heightLimit - 8);
            int z1 = Math.max(MathHelper.floor(z - yaw) - chunkZ * 16 - 1, 0);
            int z2 = Math.min(MathHelper.floor(z + yaw) - chunkZ * 16 + 1, 16);
            if (this.isRegionUncarvable(chunk, chunkX, chunkZ, x1, x2, y1, y2, z1, z2)) {
                return false;
            }
            boolean bl = false;
            random.setSeed(seed + (long) chunkX + (long) chunkZ);
            for (int o = x1; o < x2; o++) {
                int p = o + chunkX * 16;
                double f = ((double) p + 0.5 - x) / yaw;

                for (int q = z1; q < z2; q++) {
                    int r = q + chunkZ * 16;
                    double g = ((double) r + 0.5 - z) / yaw;
                    if (!(f * f + g * g >= 1.0)) {
                        mutableBoolean.setFalse();
                        for (int s = y2; s > y1; s--) {
                            double h = ((double) s - 0.5 - y) / pitch;
                            if (!this.isPositionExcluded(f, h, g, s)) {
                                bl |= this.carveAtPoint(chunk, posToBiome, carvingMask, random, mutable, mutable2, mutable3, seaLevel, chunkX, chunkZ, p, r, o, s, q, mutableBoolean);
                            }
                        }
                    }
                }
            }

            return bl;
        }
        return false;
    }
}
