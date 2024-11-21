package me.contaria.glacier.optimization.gen.structure;

import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;

public interface GlacierStructureBlockInfo {

    StructurePool glacier$getPool();

    int glacier$getHighestY(StructureManager structureManager);
}
