package me.contaria.glacier.mixin.allocation.blockpos.gen.feature;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.feature.GlowstoneBlobFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GlowstoneBlobFeature.class)
public abstract class GlowstoneBlobFeatureMixin {

    @Redirect(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos createAndUseMutableBlockPos(BlockPos pos, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable(pos.getX(), pos.getY() + 1, pos.getZ()));
        return mutable.get();
    }

    @Inject(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/ServerWorldAccess;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
                    ordinal = 0
            )
    )
    private void createMutableBlockPos(CallbackInfoReturnable<Boolean> cir, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        mutable2.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;add(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, int x, int y, int z, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        return mutable.get().set(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
    }

    @Redirect(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;offset(Lnet/minecraft/util/math/Direction;)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos, Direction direction, @Share("mutable2") LocalRef<BlockPos.Mutable> mutable2) {
        return mutable2.get().set(pos, direction);
    }
}
