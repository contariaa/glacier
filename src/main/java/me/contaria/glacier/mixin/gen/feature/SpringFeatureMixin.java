package me.contaria.glacier.mixin.gen.feature;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.SpringFeature;
import net.minecraft.world.gen.feature.SpringFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(SpringFeature.class)
public abstract class SpringFeatureMixin {

    /**
     * @author contaria
     * @reason Avoid looking up the same BlockState twice, return early if requirements can't be met and reduce BlockPos allocations
     */
    @Overwrite
    public boolean generate(
            ServerWorldAccess world,
            StructureAccessor structures,
            ChunkGenerator generator,
            Random random,
            BlockPos pos,
            SpringFeatureConfig config
    ) {
        BlockPos.Mutable mutable = new BlockPos.Mutable(pos.getX(), pos.getY() + 1, pos.getZ());
        if (!config.validBlocks.contains(world.getBlockState(mutable).getBlock())) {
            return false;
        }
        mutable.setY(pos.getY() - 1);
        if (config.requiresBlockBelow && !config.validBlocks.contains(world.getBlockState(mutable).getBlock())) {
            return false;
        }
        BlockState state = world.getBlockState(pos);
        if (!state.isAir() && !config.validBlocks.contains(state.getBlock())) {
            return false;
        }
        int valid = 0;
        int air = 0;

        state = world.getBlockState(mutable.set(pos.getX() - 1, pos.getY(), pos.getZ()));
        if (config.validBlocks.contains(state.getBlock())) {
            valid++;
        }
        if (state.isAir()) {
            air++;
        }
        if (valid > config.rockCount || air > config.holeCount || valid + 4 < config.rockCount || air + 4 < config.holeCount) {
            return false;
        }

        state = world.getBlockState(mutable.set(pos.getX() + 1, pos.getY(), pos.getZ()));
        if (config.validBlocks.contains(state.getBlock())) {
            valid++;
        }
        if (state.isAir()) {
            air++;
        }
        if (valid > config.rockCount || air > config.holeCount || valid + 3 < config.rockCount || air + 3 < config.holeCount) {
            return false;
        }

        state = world.getBlockState(mutable.set(pos.getX(), pos.getY(), pos.getZ() - 1));
        if (config.validBlocks.contains(state.getBlock())) {
            valid++;
        }
        if (state.isAir()) {
            air++;
        }
        if (valid > config.rockCount || air > config.holeCount || valid + 2 < config.rockCount || air + 2 < config.holeCount) {
            return false;
        }

        state = world.getBlockState(mutable.set(pos.getX(), pos.getY(), pos.getZ() + 1));
        if (config.validBlocks.contains(state.getBlock())) {
            valid++;
        }
        if (state.isAir()) {
            air++;
        }
        if (valid > config.rockCount || air > config.holeCount || valid + 1 < config.rockCount || air + 1 < config.holeCount) {
            return false;
        }

        state = world.getBlockState(mutable.set(pos.getX(), pos.getY() - 1, pos.getZ()));
        if (config.validBlocks.contains(state.getBlock())) {
            valid++;
        }
        if (state.isAir()) {
            air++;
        }

        if (valid == config.rockCount && air == config.holeCount) {
            world.setBlockState(pos, config.state.getBlockState(), 2);
            world.getFluidTickScheduler().schedule(pos, config.state.getFluid(), 0);
            return true;
        }
        return false;
    }
}
