package me.contaria.glacier.mixin.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.RandomPatchFeature;
import net.minecraft.world.gen.feature.RandomPatchFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(RandomPatchFeature.class)
public abstract class RandomPatchFeatureMixin {

    /**
     * @author contaria
     * @reason Reduce BlockState lookups and BlockPos allocations.
     */
    @Overwrite
    public boolean generate(
            ServerWorldAccess world,
            StructureAccessor structures,
            ChunkGenerator generator,
            Random random,
            BlockPos pos,
            RandomPatchFeatureConfig config
    ) {
        BlockState blockState = config.stateProvider.getBlockState(random, pos);
        if (config.project) {
            pos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE_WG, pos);
        }

        boolean generated = false;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutableDown = new BlockPos.Mutable();

        boolean checkStateDown = !config.whitelist.isEmpty() || !config.blacklist.isEmpty();

        for (int j = 0; j < config.tries; j++) {
            mutable.set(
                    pos,
                    random.nextInt(config.spreadX + 1) - random.nextInt(config.spreadX + 1),
                    random.nextInt(config.spreadY + 1) - random.nextInt(config.spreadY + 1),
                    random.nextInt(config.spreadZ + 1) - random.nextInt(config.spreadZ + 1)
            );
            mutableDown.set(mutable.getX(), mutable.getY() - 1, mutable.getZ());
            if (((config.canReplace && blockState.getMaterial().isReplaceable()) || world.isAir(mutable)) && blockState.canPlaceAt(world, mutable)) {
                BlockState stateDown = checkStateDown ? world.getBlockState(mutableDown) : null;
                if ((config.whitelist.isEmpty() || config.whitelist.contains(stateDown.getBlock())) && !config.blacklist.contains(stateDown) && (
                        !config.needsWater ||
                                world.getFluidState(mutableDown.move(-1, 0, 0)).isIn(FluidTags.WATER) ||
                                world.getFluidState(mutableDown.move(2, 0, 0)).isIn(FluidTags.WATER) ||
                                world.getFluidState(mutableDown.move(-1, 0, -1)).isIn(FluidTags.WATER) ||
                                world.getFluidState(mutableDown.move(0, 0, 2)).isIn(FluidTags.WATER)
                )) {
                    config.blockPlacer.generate(world, mutable, blockState, random);
                    generated = true;
                }
            }
        }

        return generated;
    }
}
