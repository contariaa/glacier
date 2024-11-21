package me.contaria.glacier.mixin.allocation.blockpos.gen.feature;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.KelpFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(KelpFeature.class)
public abstract class KelpFeatureMixin {

    @Redirect(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos createMutableBlockPos(int x, int y, int z) {
        return new BlockPos.Mutable(x, y, z);
    }

    @Redirect(
            method = "generate(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/gen/StructureAccessor;Lnet/minecraft/world/gen/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/feature/DefaultFeatureConfig;)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;up()Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos useMutableBlockPos(BlockPos pos) {
        return ((BlockPos.Mutable) pos).set(pos.getX(), pos.getY() - 1, pos.getZ());
    }
}
