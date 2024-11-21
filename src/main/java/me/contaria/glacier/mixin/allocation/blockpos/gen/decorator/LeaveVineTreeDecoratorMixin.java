package me.contaria.glacier.mixin.allocation.blockpos.gen.decorator;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LeaveVineTreeDecorator.class)
public abstract class LeaveVineTreeDecoratorMixin {

    @Redirect(
            method = "method_23467",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;",
                    ordinal = 0
            )
    )
    private BlockPos createMutableBlockPos(BlockPos pos) {
        return new BlockPos.Mutable(pos.getX(), pos.getY() - 1, pos.getZ());
    }

    @Redirect(
            method = "method_23467",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;",
                    ordinal = 1
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos) {
        ((BlockPos.Mutable) pos).setY(pos.getY() - 1);
        return pos;
    }
}
