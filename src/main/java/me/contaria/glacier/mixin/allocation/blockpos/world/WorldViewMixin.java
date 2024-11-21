package me.contaria.glacier.mixin.allocation.blockpos.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldView.class)
public interface WorldViewMixin {

    @Redirect(
            method = "isSkyVisibleAllowingSea",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    default BlockPos createMutableBlockPos(int x, int y, int z) {
        return new BlockPos.Mutable(x, y, z);
    }

    @Redirect(
            method = "isSkyVisibleAllowingSea",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    default BlockPos useMutableBlockPos(BlockPos pos) {
        ((BlockPos.Mutable) pos).setY(pos.getY() - 1);
        return pos;
    }
}
