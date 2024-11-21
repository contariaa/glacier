package me.contaria.glacier.mixin.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin {

    /**
     * @author contaria
     * @reason Reduce BlockState lookups.
     */
    @Overwrite
    public static boolean canTreeReplace(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES) || state.getMaterial() == Material.REPLACEABLE_PLANT || state.isOf(Blocks.WATER) || state.isIn(BlockTags.LOGS));
    }

    /**
     * @author contaria
     * @reason Reduce BlockState lookups.
     */
    @Overwrite
    public static boolean canReplace(TestableWorld world, BlockPos pos) {
        return world.testBlockState(pos, state -> state.isAir() || state.isIn(BlockTags.LEAVES) || state.getMaterial() == Material.REPLACEABLE_PLANT || state.isOf(Blocks.WATER));
    }
}
