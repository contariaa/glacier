package me.contaria.glacier.mixin.memory.model.voxel_array;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Reuse the same shape instances for all fences and glass panes.
 */
@Mixin(HorizontalConnectingBlock.class)
public abstract class HorizontalConnectingBlockMixin {
    @Unique
    private static VoxelShape[] PANE_SHAPES;

    @Unique
    private static VoxelShape[] FENCE_COLLISION_SHAPES;
    @Unique
    private static VoxelShape[] FENCE_BOUNDING_SHAPES;

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/HorizontalConnectingBlock;createShapes(FFFFF)[Lnet/minecraft/util/shape/VoxelShape;"
            )
    )
    private VoxelShape[] cacheShapes(HorizontalConnectingBlock block, float radius1, float radius2, float height1, float offset2, float height2, Operation<VoxelShape[]> original) {
        if (radius1 == 1.0F && radius2 == 1.0F && height1 == 16.0F && offset2 == 0.0F && height2 == 16.0F) {
            if (PANE_SHAPES == null) {
                PANE_SHAPES = original.call(block, radius1, radius2, height1, offset2, height2);
            }
            return PANE_SHAPES;
        }
        if (radius1 == 2.0F && radius2 == 2.0F && offset2 == 0.0F) {
            if (height1 == 24.0F && height2 == 24.0F) {
                if (FENCE_COLLISION_SHAPES == null) {
                    FENCE_COLLISION_SHAPES = original.call(block, radius1, radius2, height1, offset2, height2);
                }
                return FENCE_COLLISION_SHAPES;
            }
            if (height1 == 16.0F && height2 == 16.0F) {
                if (FENCE_BOUNDING_SHAPES == null) {
                    FENCE_BOUNDING_SHAPES = original.call(block, radius1, radius2, height1, offset2, height2);
                }
                return FENCE_BOUNDING_SHAPES;
            }
        }
        return original.call(block, radius1, radius2, height1, offset2, height2);
    }
}
