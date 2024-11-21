package me.contaria.glacier.mixin.gen.structure;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.loot.LootTables;
import net.minecraft.structure.BuriedTreasureGenerator;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Random;

@Mixin(BuriedTreasureGenerator.Piece.class)
public abstract class BuriedTreasureGenerator$PieceMixin extends StructurePiece {

    @Shadow
    protected abstract boolean isLiquid(BlockState state);

    protected BuriedTreasureGenerator$PieceMixin(StructurePieceType type, int length) {
        super(type, length);
    }

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
            BlockBox boundingBox,
            ChunkPos chunkPos,
            BlockPos blockPos
    ) {
        int topY = world.getTopY(Heightmap.Type.OCEAN_FLOOR_WG, this.boundingBox.minX, this.boundingBox.minZ);
        if (topY <= 0) {
            return false;
        }

        BlockPos.Mutable mutable = new BlockPos.Mutable(this.boundingBox.minX, topY, this.boundingBox.minZ);
        BlockPos.Mutable mutable2 = new BlockPos.Mutable();
        BlockState stateDown = world.getBlockState(mutable);

        while (mutable.getY() > 0) {
            BlockState state = stateDown;
            stateDown = world.getBlockState(mutable2.set(mutable.getX(), mutable.getY() - 1, mutable.getZ()));
            if (stateDown == Blocks.SANDSTONE.getDefaultState()
                    || stateDown == Blocks.STONE.getDefaultState()
                    || stateDown == Blocks.ANDESITE.getDefaultState()
                    || stateDown == Blocks.GRANITE.getDefaultState()
                    || stateDown == Blocks.DIORITE.getDefaultState()) {
                BlockState blockState3 = !state.isAir() && !this.isLiquid(state) ? state : Blocks.SAND.getDefaultState();

                for (Direction direction : Direction.values()) {
                    mutable2.set(mutable, direction);
                    BlockState blockState4 = world.getBlockState(mutable2);
                    if (blockState4.isAir() || this.isLiquid(blockState4)) {
                        mutable2.setY(mutable2.getY() - 1);
                        BlockState blockState5 = world.getBlockState(mutable2);
                        mutable2.setY(mutable2.getY() + 1);
                        if ((blockState5.isAir() || this.isLiquid(blockState5)) && direction != Direction.UP) {
                            world.setBlockState(mutable2, stateDown, 3);
                        } else {
                            world.setBlockState(mutable2, blockState3, 3);
                        }
                    }
                }

                this.boundingBox = new BlockBox(mutable.getX(), mutable.getY(), mutable.getZ(), mutable.getX(), mutable.getY(), mutable.getZ());
                return this.addChest(world, boundingBox, random, mutable, LootTables.BURIED_TREASURE_CHEST, null);
            }

            mutable.setY(mutable.getY() - 1);
        }

        return false;
    }
}
