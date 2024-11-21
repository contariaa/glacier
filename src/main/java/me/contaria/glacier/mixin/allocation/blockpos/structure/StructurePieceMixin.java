package me.contaria.glacier.mixin.allocation.blockpos.structure;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FluidState;
import net.minecraft.structure.StructurePiece;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(StructurePiece.class)
public abstract class StructurePieceMixin {

    @Shadow
    @Final
    private static Set<Block> BLOCKS_NEEDING_POST_PROCESSING;

    @Shadow
    private BlockMirror mirror;

    @Shadow
    private BlockRotation rotation;

    @Shadow
    protected abstract int applyXTransform(int i, int j);

    @Shadow
    protected abstract int applyYTransform(int i);

    @Shadow
    protected abstract int applyZTransform(int i, int j);

    @Inject(
            method = {
                    "fill",
                    "fillWithOutline(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V",
                    "fillWithOutline(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIIZLjava/util/Random;Lnet/minecraft/structure/StructurePiece$BlockRandomizer;)V",
                    "fillWithOutlineUnderSeaLevel",
                    "method_14919"
            },
            at = @At("HEAD")
    )
    private void createMutableBlockPos(CallbackInfo ci, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        mutable.set(new BlockPos.Mutable());
    }

    @Redirect(
            method = {
                    "fill",
                    "fillWithOutline(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIILnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Z)V",
                    "fillWithOutline(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockBox;IIIIIIZLjava/util/Random;Lnet/minecraft/structure/StructurePiece$BlockRandomizer;)V",
                    "fillWithOutlineUnderSeaLevel",
                    "method_14919"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/structure/StructurePiece;addBlock(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/block/BlockState;IIILnet/minecraft/util/math/BlockBox;)V"
            )
    )
    private void redirectAddBlockWithMutableBlockPos(StructurePiece piece, WorldAccess world, BlockState block, int x, int y, int z, BlockBox blockBox, @Share("mutable") LocalRef<BlockPos.Mutable> mutable) {
        this.addBlock(world, block, x, y, z, blockBox, mutable.get());
    }

    // [VanillaCopy] StructurePieceMixin#addBlock
    // Use the passed BlockPos.Mutable instead of creating a new BlockPos instance everytime
    @Unique
    private void addBlock(WorldAccess world, BlockState block, int x, int y, int z, BlockBox blockBox, BlockPos.Mutable mutable) {
        mutable.set(this.applyXTransform(x, z), this.applyYTransform(y), this.applyZTransform(x, z));
        if (blockBox.contains(mutable)) {
            if (this.mirror != BlockMirror.NONE) {
                block = block.mirror(this.mirror);
            }

            if (this.rotation != BlockRotation.NONE) {
                block = block.rotate(this.rotation);
            }

            world.setBlockState(mutable, block, 2);
            FluidState fluidState = world.getFluidState(mutable);
            if (!fluidState.isEmpty()) {
                world.getFluidTickScheduler().schedule(mutable, fluidState.getFluid(), 0);
            }

            if (BLOCKS_NEEDING_POST_PROCESSING.contains(block.getBlock())) {
                world.getChunk(mutable).markBlockForPostProcessing(mutable);
            }
        }
    }
}
