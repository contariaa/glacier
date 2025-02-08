package me.contaria.glacier.mixin.memory.model.voxel_array;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.FenceBlock;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Reuse the same shapes for all fences.
 */
@Mixin(FenceBlock.class)
public abstract class FenceBlockMixin {

    @Unique
    private static VoxelShape[] CULLING_SHAPES;

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/FenceBlock;createShapes(FFFFF)[Lnet/minecraft/util/shape/VoxelShape;"
            )
    )
    private VoxelShape[] cacheShapes(FenceBlock block, float radius1, float radius2, float height1, float offset2, float height2, Operation<VoxelShape[]> original) {
        if (CULLING_SHAPES == null) {
            CULLING_SHAPES = original.call(block, radius1, radius2, height1, offset2, height2);
        }
        return CULLING_SHAPES;
    }
}
