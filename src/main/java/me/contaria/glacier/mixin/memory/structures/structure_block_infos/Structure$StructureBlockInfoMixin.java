package me.contaria.glacier.mixin.memory.structures.structure_block_infos;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.glacier.optimization.memory.structure_block_infos.GlacierStructureBlockInfoIterator;
import me.contaria.glacier.optimization.memory.structure_block_infos.MutableStructureBlockInfo;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Structure.StructureBlockInfo.class)
public abstract class Structure$StructureBlockInfoMixin implements MutableStructureBlockInfo {
    @Shadow
    @Final
    public BlockPos pos;

    @Mutable
    @Shadow
    @Final
    public BlockState state;

    @Mutable
    @Shadow
    @Final
    public CompoundTag tag;

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/structure/Structure$StructureBlockInfo;pos:Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private void posToImmutable(Structure.StructureBlockInfo info, BlockPos pos, Operation<Void> original) {
        if (!(info instanceof GlacierStructureBlockInfoIterator.MutableInfo)) {
            pos = pos.toImmutable();
        }
        original.call(info, pos);
    }

    @Override
    public void glacier$set(int x, int y, int z, BlockState state, CompoundTag tag) {
        if (!(this.pos instanceof BlockPos.Mutable)) {
            throw new IllegalStateException("StructureBlockInfo is not mutable!");
        }
        ((BlockPos.Mutable) this.pos).set(x, y, z);
        this.state = state;
        this.tag = tag;
    }
}
