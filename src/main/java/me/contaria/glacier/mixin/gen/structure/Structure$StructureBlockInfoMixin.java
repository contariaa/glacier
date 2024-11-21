package me.contaria.glacier.mixin.gen.structure;

import me.contaria.glacier.optimization.gen.structure.GlacierStructureBlockInfo;
import me.contaria.glacier.optimization.gen.structure.GlacierStructurePool;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Structure.StructureBlockInfo.class)
public abstract class Structure$StructureBlockInfoMixin implements GlacierStructureBlockInfo {
    @Shadow
    @Final
    public CompoundTag tag;

    @Unique
    private StructurePool pool;

    @Unique
    private int highestY = -1;

    @Override
    public StructurePool glacier$getPool() {
        if (this.pool == null) {
            this.pool = StructurePoolBasedGenerator.REGISTRY.get(new Identifier(this.tag.getString("pool")));
        }
        return this.pool;
    }

    @Override
    public int glacier$getHighestY(StructureManager structureManager) {
        if (this.highestY == -1) {
            StructurePool pool = this.glacier$getPool();
            this.highestY = Math.max(
                    pool.getHighestY(structureManager),
                    ((GlacierStructurePool) pool).glacier$getTerminatorPool().getHighestY(structureManager)
            );
        }
        return this.highestY;
    }
}
