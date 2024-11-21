package me.contaria.glacier.mixin.allocation.blockpos.fluid;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(FlowableFluid.class)
public abstract class FlowableFluidMixin {

    @Inject(
            method = {
                    "getUpdatedState",
                    "method_15742",
                    "method_15740",
                    "getSpread"
            },
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfoReturnable<FluidState> ci, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable());
    }

    @Inject(
            method = "method_15744",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/fluid/FlowableFluid;getSpread(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Ljava/util/Map;"
            )
    )
    private void createMutableBlockPos(CallbackInfo ci, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "getVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosDown(BlockPos.Mutable mutable) {
        mutable.setY(mutable.getY() - 1);
        return mutable;
    }

    @Redirect(
            method = "getVelocity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;up()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosUp(BlockPos.Mutable mutable) {
        mutable.setY(mutable.getY() + 1);
        return mutable;
    }

    @Redirect(
            method = {
                    "method_15744",
                    "getUpdatedState",
                    "method_15742",
                    "method_15740",
                    "getSpread"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, Direction direction, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos, direction);
    }

    @Redirect(
            method = "getUpdatedState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosDown(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX(), pos.getY() - 1, pos.getZ());
    }

    @Redirect(
            method = "getUpdatedState",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPosUp(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX(), pos.getY() + 1, pos.getZ());
    }

    @Inject(
            method = "getSpread",
            at = @At("HEAD")
    )
    private void createMutableBlockPos2(CallbackInfoReturnable<Map<Direction, FluidState>> cir, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        mutable2.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "getSpread",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos2(BlockPos pos, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        return mutable2.get().set(pos.getX(), pos.getY() - 1, pos.getZ());
    }
}
