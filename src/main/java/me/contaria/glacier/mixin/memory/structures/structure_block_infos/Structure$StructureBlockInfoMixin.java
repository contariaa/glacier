package me.contaria.glacier.mixin.memory.structures.structure_block_infos;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.glacier.optimization.memory.structure_block_infos.GlacierStructureBlockInfoList;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Structure.StructureBlockInfo.class)
public abstract class Structure$StructureBlockInfoMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/structure/Structure$StructureBlockInfo;pos:Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private void posToImmutable(Structure.StructureBlockInfo info, BlockPos pos, Operation<Void> original) {
        if (!(info instanceof GlacierStructureBlockInfoList.MutableStructureBlockInfo)) {
            pos = pos.toImmutable();
        }
        original.call(info, pos);
    }
}
