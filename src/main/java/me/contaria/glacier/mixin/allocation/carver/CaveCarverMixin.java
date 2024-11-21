package me.contaria.glacier.mixin.allocation.carver;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.carver.CaveCarver;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

/**
 * @see CarverMixin
 */
@Mixin(CaveCarver.class)
public abstract class CaveCarverMixin extends CarverMixin {

    @Shadow
    protected abstract int getCaveY(Random random);

    @Shadow
    protected abstract float getTunnelSystemWidth(Random random);

    @Shadow
    protected abstract double getTunnelSystemHeightWidthRatio();

    @Shadow
    protected abstract int getMaxCaveCount();

    /**
     * @author contaria
     * @reason Reduce BlockPos.Mutable and MutableBoolean allocations.
     */
    @Overwrite
    public boolean carve(
            Chunk chunk, Function<BlockPos, Biome> function, Random random, int seaLevel, int chunkX, int chunkZ, int l, int m, BitSet bitSet, ProbabilityConfig probabilityConfig
    ) {
        int branches = (this.getBranchFactor() * 2 - 1) * 16;
        int caves = random.nextInt(random.nextInt(random.nextInt(this.getMaxCaveCount()) + 1) + 1);

        if (caves == 0) {
            return true;
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        BlockPos.Mutable mutable3 = new BlockPos.Mutable();
        MutableBoolean mutableBoolean = new MutableBoolean();
        Random carverRandom = new Random(0);
        Random tunnelRandom = new Random(0);

        for (int cave = 0; cave < caves; cave++) {
            double x = chunkX * 16 + random.nextInt(16);
            double y = this.getCaveY(random);
            double z = chunkZ * 16 + random.nextInt(16);
            int tunnels = 1;
            if (random.nextInt(4) == 0) {
                this.carveCave(chunk, function, random.nextLong(), seaLevel, l, m, x, y, z, 1.0F + random.nextFloat() * 6.0F, 0.5, bitSet, mutable, mutable2, mutable3, mutableBoolean, carverRandom);
                tunnels += random.nextInt(4);
            }

            for (int tunnel = 0; tunnel < tunnels; tunnel++) {
                this.carveTunnels(chunk, function, random.nextLong(), seaLevel, l, m, x, y, z, this.getTunnelSystemWidth(random), random.nextFloat() * (float) (Math.PI * 2), (random.nextFloat() - 0.5F) / 4.0F, 0, branches - random.nextInt(branches / 4), this.getTunnelSystemHeightWidthRatio(), bitSet, mutable, mutable2, mutable3, mutableBoolean, carverRandom, tunnelRandom);
            }
        }

        return true;
    }

    @Unique
    protected void carveCave(
            Chunk chunk,
            Function<BlockPos, Biome> posToBiome,
            long seed,
            int seaLevel,
            int mainChunkX,
            int mainChunkZ,
            double x,
            double y,
            double z,
            float yaw,
            double yawPitchRatio,
            BitSet carvingMask,
            BlockPos.Mutable mutable,
            BlockPos.Mutable mutable2,
            BlockPos.Mutable mutable3,
            MutableBoolean mutableBoolean,
            Random carverRandom
    ) {
        double d = 1.5 + (double) (MathHelper.sin((float) (Math.PI / 2)) * yaw);
        this.carveRegion(chunk, posToBiome, seed, seaLevel, mainChunkX, mainChunkZ, x + 1.0, y, z, d, d * yawPitchRatio, carvingMask, mutable, mutable2, mutable3, mutableBoolean, carverRandom);
    }

    @Unique
    protected void carveTunnels(
            Chunk chunk,
            Function<BlockPos, Biome> postToBiome,
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
            BitSet carvingMask,
            BlockPos.Mutable mutable,
            BlockPos.Mutable mutable2,
            BlockPos.Mutable mutable3,
            MutableBoolean mutableBoolean,
            Random carverRandom,
            Random random
    ) {
        random.setSeed(seed);
        int i = random.nextInt(branchCount / 2) + branchCount / 4;
        boolean bl = random.nextInt(6) == 0;
        float f = 0.0F;
        float g = 0.0F;

        for (int branch = branchStartIndex; branch < branchCount; branch++) {
            double d = 1.5 + (double) (MathHelper.sin((float) Math.PI * (float) branch / (float) branchCount) * width);
            double e = d * yawPitchRatio;
            float h = MathHelper.cos(pitch);
            x += MathHelper.cos(yaw) * h;
            y += MathHelper.sin(pitch);
            z += MathHelper.sin(yaw) * h;
            pitch *= bl ? 0.92F : 0.7F;
            pitch += g * 0.1F;
            yaw += f * 0.1F;
            g *= 0.9F;
            f *= 0.75F;
            g += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 2.0F;
            f += (random.nextFloat() - random.nextFloat()) * random.nextFloat() * 4.0F;
            if (branch == i && width > 1.0F) {
                Random tunnelRandom = new Random();
                this.carveTunnels(
                        chunk,
                        postToBiome,
                        random.nextLong(),
                        seaLevel,
                        mainChunkX,
                        mainChunkZ,
                        x,
                        y,
                        z,
                        random.nextFloat() * 0.5F + 0.5F,
                        yaw - (float) (Math.PI / 2),
                        pitch / 3.0F,
                        branch,
                        branchCount,
                        1.0,
                        carvingMask,
                        mutable,
                        mutable2,
                        mutable3,
                        mutableBoolean,
                        carverRandom,
                        tunnelRandom
                );
                this.carveTunnels(
                        chunk,
                        postToBiome,
                        random.nextLong(),
                        seaLevel,
                        mainChunkX,
                        mainChunkZ,
                        x,
                        y,
                        z,
                        random.nextFloat() * 0.5F + 0.5F,
                        yaw + (float) (Math.PI / 2),
                        pitch / 3.0F,
                        branch,
                        branchCount,
                        1.0,
                        carvingMask,
                        mutable,
                        mutable2,
                        mutable3,
                        mutableBoolean,
                        carverRandom,
                        tunnelRandom
                );
                return;
            }

            if (random.nextInt(4) != 0) {
                if (!this.canCarveBranch(mainChunkX, mainChunkZ, x, z, branch, branchCount, width)) {
                    return;
                }

                this.carveRegion(chunk, postToBiome, seed, seaLevel, mainChunkX, mainChunkZ, x, y, z, d, e, carvingMask, mutable, mutable2, mutable3, mutableBoolean, carverRandom);
            }
        }
    }
}
