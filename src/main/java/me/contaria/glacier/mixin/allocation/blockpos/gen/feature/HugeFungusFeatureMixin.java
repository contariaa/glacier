package me.contaria.glacier.mixin.allocation.blockpos.gen.feature;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.HugeFungusFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HugeFungusFeature.class)
public abstract class HugeFungusFeatureMixin {

    @Redirect(
            method = {
                    "generateStem",
                    "generateHat"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos.Mutable mutable) {
        mutable.setY(mutable.getY() - 1);
        return mutable;
    }

    @ModifyVariable(
            method = {
                    "generateStem",
                    "generateHat"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/WorldAccess;getBlockState(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/block/BlockState;",
                    shift = At.Shift.AFTER
            )
    )
    private BlockPos.Mutable resetMutableBlockPos(BlockPos.Mutable mutable) {
        mutable.setY(mutable.getY() + 1);
        return mutable;
    }

    @ModifyArg(
            method = "getStartPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;setY(I)V"
            )
    )
    private static int keepDownMutableBlockPos(int y) {
        return y - 1;
    }

    @Redirect(
            method = "getStartPos",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private static BlockPos useDownMutableBlockPos(BlockPos.Mutable mutable) {
        return mutable;
    }

    @ModifyReturnValue(
            method = "getStartPos",
            at = @At(
                    value = "RETURN",
                    ordinal = 0
            )
    )
    private static BlockPos.Mutable upMutableBlockPos(BlockPos.Mutable mutable) {
        mutable.setY(mutable.getY() + 1);
        return mutable;
    }
}
