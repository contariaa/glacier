package me.contaria.glacier.mixin.gen.structure;

import me.contaria.glacier.optimization.gen.structure.GlacierStructurePool;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(StructurePool.class)
public abstract class StructurePoolMixin implements GlacierStructurePool {
    @Shadow
    @Final
    private Identifier terminatorsId;

    @Unique
    private StructurePool terminatorPool;

    @Override
    public StructurePool glacier$getTerminatorPool() {
        if (this.terminatorPool == null) {
            this.terminatorPool = StructurePoolBasedGenerator.REGISTRY.get(this.terminatorsId);
        }
        return this.terminatorPool;
    }
}
