package me.contaria.glacier.mixin.allocation.blockpos.structure;

import net.minecraft.block.BlockState;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Structure.class)
public abstract class StructureMixin {

    /**
     * @author contaria
     * @reason Reduce BlockPos allocations.
     */
    @Overwrite
    public static void updateCorner(WorldAccess world, int flags, VoxelSet voxelSet, int startX, int startY, int startZ) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        BlockPos.Mutable facing = new BlockPos.Mutable();
        voxelSet.forEachDirection((direction, m, n, o) -> {
            pos.set(startX + m, startY + n, startZ + o);
            facing.set(pos, direction);
            BlockState blockState = world.getBlockState(pos);
            BlockState blockState2 = world.getBlockState(facing);
            BlockState blockState3 = blockState.getStateForNeighborUpdate(direction, blockState2, world, pos, facing);
            if (blockState != blockState3) {
                world.setBlockState(pos, blockState3, flags & -2);
            }

            BlockState blockState4 = blockState2.getStateForNeighborUpdate(direction.getOpposite(), blockState3, world, facing, pos);
            if (blockState2 != blockState4) {
                world.setBlockState(facing, blockState4, flags & -2);
            }
        });
    }
}
