package me.contaria.glacier.mixin.allocation.blockpos.gen.feature;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.IcebergFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IcebergFeature.class)
public abstract class IcebergFeatureMixin {

    @Inject(
            method = "smooth",
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfo ci, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "smooth",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, int x, int y, int z, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    @Redirect(
            method = "isAirBelow",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos mutable) {
        ((BlockPos.Mutable) mutable).setY(mutable.getY() - 1);
        return mutable;
    }

    @ModifyVariable(
            method = "isAirBelow",
            at = @At("TAIL"),
            argsOnly = true
    )
    private BlockPos resetMutableBlockPos(BlockPos mutable) {
        ((BlockPos.Mutable) mutable).setY(mutable.getY() + 1);
        return mutable;
    }
}
