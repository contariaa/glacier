package me.contaria.glacier.mixin.allocation.blockpos.gen.trunk;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(LargeOakTrunkPlacer.class)
public abstract class LargeOakTrunkPlacerMixin {

    @Inject(
            method = "generate",
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfoReturnable<Boolean> cir, @Share("mutable") LocalRef<BlockPos.Mutable> mutable, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        mutable.set(new BlockPos.Mutable());
        mutable2.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;add(DDD)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, double x, double y, double z, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(MathHelper.floor(pos.getX() + x), MathHelper.floor(pos.getY() + y), MathHelper.floor(pos.getZ() + z));
    }

    @Redirect(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;up(I)Lnet/minecraft/util/math/BlockPos;",
                    ordinal = 1
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, int distance, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        return mutable2.get().set(pos.getX(), pos.getY() + distance, pos.getZ());
    }

    @ModifyArg(
            method = "generate",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/gen/trunk/LargeOakTrunkPlacer$BranchPosition;<init>(Lnet/minecraft/util/math/BlockPos;I)V",
                    ordinal = 1
            )
    )
    private BlockPos toImmutableBlockPos(BlockPos pos) {
        return pos.toImmutable();
    }

    @Redirect(
            method = "makeOrCheckBranch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos createMutableBlockPos(BlockPos pos, int x, int y, int z) {
        return new BlockPos.Mutable(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    @Redirect(
            method = "makeOrCheckBranch",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;add(DDD)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, double x, double y, double z, @Local(ordinal = 2) BlockPos mutable) {
        return ((BlockPos.Mutable) mutable).set(MathHelper.floor(pos.getX() + x), MathHelper.floor(pos.getY() + y), MathHelper.floor(pos.getZ() + z));
    }

    @Inject(
            method = "makeBranches",
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfo ci, @Local(argsOnly = true) List<?> branches, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        if (!branches.isEmpty()) {
            mutable.set(new BlockPos.Mutable());
        }
    }

    @Redirect(
            method = "makeBranches",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(int x, int y, int z, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(x, y, z);
    }
}
