package me.contaria.glacier.mixin.allocation.blockpos.structure;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.structure.RuinedPortalStructurePiece;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RuinedPortalStructurePiece.class)
public abstract class RuinedPortalStructurePieceMixin {

    @Inject(
            method = "updateNetherracksInBound",
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfo ci, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "updateNetherracksInBound",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(int x, int y, int z, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(x, y, z);
    }

    @Redirect(
            method = "updateNetherracksInBound",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX(), pos.getY() - 1, pos.getZ());
    }

    @Redirect(
            method = "placeNetherrackBase",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos$Mutable;down()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos.Mutable mutable) {
        mutable.setY(mutable.getY() - 1);
        return mutable;
    }
}
