package me.contaria.glacier.mixin.allocation.blockpos.world;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
public abstract class WorldMixin {

    @Shadow
    public abstract void updateNeighbor(BlockPos sourcePos, Block sourceBlock, BlockPos neighborPos);

    /**
     * @author contaria
     * @reason Reduce BlockPos allocations
     */
    @Overwrite
    public void updateNeighborsAlways(BlockPos pos, Block block) {
        BlockPos.Mutable mutable = pos.mutableCopy();
        mutable.setX(pos.getX() - 1);
        this.updateNeighbor(pos.west(), block, pos);
        mutable.setX(pos.getX() + 1);
        this.updateNeighbor(pos.east(), block, pos);
        mutable.setX(pos.getX());
        mutable.setY(pos.getY() - 1);
        this.updateNeighbor(pos.down(), block, pos);
        mutable.setY(pos.getY() + 1);
        this.updateNeighbor(pos.up(), block, pos);
        mutable.setY(pos.getY());
        mutable.setZ(pos.getZ() - 1);
        this.updateNeighbor(pos.north(), block, pos);
        mutable.setZ(pos.getZ() + 1);
        this.updateNeighbor(pos.south(), block, pos);
    }

    /**
     * @author contaria
     * @reason Reduce BlockPos allocations
     */
    @Overwrite
    public void updateNeighborsExcept(BlockPos pos, Block sourceBlock, Direction direction) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        if (direction != Direction.WEST) {
            this.updateNeighbor(mutable.set(pos.getX() - 1, pos.getY(), pos.getZ()), sourceBlock, pos);
        }

        if (direction != Direction.EAST) {
            this.updateNeighbor(mutable.set(pos.getX() + 1, pos.getY(), pos.getZ()), sourceBlock, pos);
        }

        if (direction != Direction.DOWN) {
            this.updateNeighbor(mutable.set(pos.getX(), pos.getY() - 1, pos.getZ()), sourceBlock, pos);
        }

        if (direction != Direction.UP) {
            this.updateNeighbor(mutable.set(pos.getX(), pos.getY() + 1, pos.getZ()), sourceBlock, pos);
        }

        if (direction != Direction.NORTH) {
            this.updateNeighbor(mutable.set(pos.getX(), pos.getY(), pos.getZ() - 1), sourceBlock, pos);
        }

        if (direction != Direction.SOUTH) {
            this.updateNeighbor(mutable.set(pos.getX(), pos.getY(), pos.getZ() + 1), sourceBlock, pos);
        }
    }
}
