package me.contaria.glacier.optimization.memory.structures;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;

/**
 * Duck interface for mutable instances of {@link Structure.StructureBlockInfo}.
 * Instances are marked as mutable by having a {@link BlockPos.Mutable} as their position.
 */
public interface MutableStructureBlockInfo {
    void glacier$set(int x, int y, int z, BlockState state, CompoundTag tag);
}
