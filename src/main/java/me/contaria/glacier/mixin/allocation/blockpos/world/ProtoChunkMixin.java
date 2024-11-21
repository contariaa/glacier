package me.contaria.glacier.mixin.allocation.blockpos.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ProtoChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin {

    @Redirect(
            method = "setBlockState",
            at = @At(
                    value = "NEW",
                    target = "(III)Lnet/minecraft/util/math/BlockPos;"
            )
    )
    private BlockPos reuseImmutableBlockPos(int x, int y, int z, BlockPos pos) {
        if (pos.getX() == x && pos.getY() == y && pos.getZ() == z) {
            return pos.toImmutable();
        }
        return new BlockPos(x, y, z);
    }
}
