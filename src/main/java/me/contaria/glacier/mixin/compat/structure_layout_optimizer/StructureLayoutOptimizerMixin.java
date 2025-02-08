package me.contaria.glacier.mixin.compat.structure_layout_optimizer;

import me.contaria.glacier.optimization.memory.structures.GlacierPalettedBlockInfoList;
import me.contaria.glacier.optimization.memory.structures.GlacierStructureBlockInfoList;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Pseudo
@Mixin(targets = "telepathicgrunt/structure_layout_optimizer/fabric/utils/StructureTemplateOptimizer")
public abstract class StructureLayoutOptimizerMixin {

    @Dynamic
    @Inject(
            method = "getStructureBlockInfosInBounds",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/structure/StructurePlacementData;getMirror()Lnet/minecraft/util/BlockMirror;",
                    remap = true
            ),
            remap = false,
            cancellable = true
    )
    private static void filterGlacierPalettedBlockInfoList(Structure.PalettedBlockInfoList palette, BlockPos offset, StructurePlacementData placement, CallbackInfoReturnable<List<Structure.StructureBlockInfo>> cir) {
        if (palette instanceof GlacierPalettedBlockInfoList) {
            cir.setReturnValue(((GlacierStructureBlockInfoList) palette.getAll()).filterForSLO(
                    placement.getBoundingBox(),
                    placement.getMirror(),
                    placement.getRotation(),
                    placement.getPosition(),
                    offset
            ));
        }
    }
}
