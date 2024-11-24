package me.contaria.glacier.mixin.memory.structures.structure_block_infos;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.glacier.Glacier;
import me.contaria.glacier.optimization.memory.structure_block_infos.GlacierCompressionException;
import me.contaria.glacier.optimization.memory.structure_block_infos.GlacierPalettedBlockInfoList;
import net.minecraft.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import java.util.List;

@Mixin(Structure.class)
public abstract class StructureMixin {

    @WrapOperation(
            method = "loadPalettedBlockInfo",
            at = @At(
                    value = "NEW",
                    target = "Lnet/minecraft/structure/Structure$PalettedBlockInfoList;"
            )
    )
    private Structure.PalettedBlockInfoList compressPalettedBlockInfoList(List<Structure.StructureBlockInfo> infos, @Coerce Object arg, Operation<Structure.PalettedBlockInfoList> original) {
        try {
            return new GlacierPalettedBlockInfoList(infos);
        } catch (GlacierCompressionException e) {
            Glacier.LOGGER.warn("Failed to compress PalettedBlockInfoList!", e);
            return original.call(infos, arg);
        }
    }
}
