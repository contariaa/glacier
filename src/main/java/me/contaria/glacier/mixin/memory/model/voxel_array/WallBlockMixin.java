package me.contaria.glacier.mixin.memory.model.voxel_array;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.WallShape;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Reuse the same shape instances for all wall blocks.
 */
@Mixin(WallBlock.class)
public abstract class WallBlockMixin extends Block {
    @Unique
    private static final List<VoxelShape> SHAPES = getShapes(4.0F, 3.0F, 16.0F, 0.0F, 14.0F, 16.0F);
    @Unique
    private static final List<VoxelShape> COLLISION_SHAPES = getShapes(4.0F, 3.0F, 24.0F, 0.0F, 24.0F, 24.0F);

    public WallBlockMixin(Settings settings) {
        super(settings);
    }

    @Shadow
    private static VoxelShape method_24426(VoxelShape voxelShape, WallShape wallShape, VoxelShape voxelShape2, VoxelShape voxelShape3) {
        throw new RuntimeException();
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/WallBlock;getShapeMap(FFFFFF)Ljava/util/Map;",
                    ordinal = 0
            )
    )
    private Map<BlockState, VoxelShape> cacheShapeMap(WallBlock wallBlock, float f, float g, float h, float i, float j, float k) {
        return this.getShapeMap(SHAPES);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/WallBlock;getShapeMap(FFFFFF)Ljava/util/Map;",
                    ordinal = 1
            )
    )
    private Map<BlockState, VoxelShape> cacheCollisionShapeMap(WallBlock wallBlock, float f, float g, float h, float i, float j, float k) {
        return this.getShapeMap(COLLISION_SHAPES);
    }

    @Unique
    private Map<BlockState, VoxelShape> getShapeMap(List<VoxelShape> shapes) {
        ImmutableMap.Builder<BlockState, VoxelShape> builder = ImmutableMap.builder();

        int i = 0;
        for (Boolean boolean_ : WallBlock.UP.getValues()) {
            for (WallShape wallShape : WallBlock.EAST_SHAPE.getValues()) {
                for (WallShape wallShape2 : WallBlock.NORTH_SHAPE.getValues()) {
                    for (WallShape wallShape3 : WallBlock.WEST_SHAPE.getValues()) {
                        for (WallShape wallShape4 : WallBlock.SOUTH_SHAPE.getValues()) {
                            VoxelShape shape = shapes.get(i);
                            BlockState blockState = this.getDefaultState()
                                    .with(WallBlock.UP, boolean_)
                                    .with(WallBlock.EAST_SHAPE, wallShape)
                                    .with(WallBlock.WEST_SHAPE, wallShape3)
                                    .with(WallBlock.NORTH_SHAPE, wallShape2)
                                    .with(WallBlock.SOUTH_SHAPE, wallShape4);
                            builder.put(blockState.with(WallBlock.WATERLOGGED, Boolean.FALSE), shape);
                            builder.put(blockState.with(WallBlock.WATERLOGGED, Boolean.TRUE), shape);
                        }
                    }
                }
            }
        }

        return builder.build();
    }

    @Unique
    private static List<VoxelShape> getShapes(float f, float g, float h, float i, float j, float k) {
        float l = 8.0F - f;
        float m = 8.0F + f;
        float n = 8.0F - g;
        float o = 8.0F + g;
        VoxelShape shape1 = Block.createCuboidShape(l, 0.0, l, m, h, m);
        VoxelShape shape2 = Block.createCuboidShape(n, i, 0.0, o, j, o);
        VoxelShape shape3 = Block.createCuboidShape(n, i, n, o, j, 16.0);
        VoxelShape shape4 = Block.createCuboidShape(0.0, i, n, o, j, o);
        VoxelShape shape5 = Block.createCuboidShape(n, i, n, 16.0, j, o);
        VoxelShape shape6 = Block.createCuboidShape(n, i, 0.0, o, k, o);
        VoxelShape shape7 = Block.createCuboidShape(n, i, n, o, k, 16.0);
        VoxelShape shape8 = Block.createCuboidShape(0.0, i, n, o, k, o);
        VoxelShape shape9 = Block.createCuboidShape(n, i, n, 16.0, k, o);

        List<VoxelShape> shapes = new ArrayList<>(162);
        for (Boolean up : WallBlock.UP.getValues()) {
            for (WallShape east : WallBlock.EAST_SHAPE.getValues()) {
                for (WallShape north : WallBlock.NORTH_SHAPE.getValues()) {
                    for (WallShape west : WallBlock.WEST_SHAPE.getValues()) {
                        for (WallShape south : WallBlock.SOUTH_SHAPE.getValues()) {
                            VoxelShape shape = VoxelShapes.empty();
                            shape = method_24426(shape, east, shape5, shape9);
                            shape = method_24426(shape, west, shape4, shape8);
                            shape = method_24426(shape, north, shape2, shape6);
                            shape = method_24426(shape, south, shape3, shape7);
                            if (up) {
                                shape = VoxelShapes.union(shape, shape1);
                            }
                            shapes.add(shape);
                        }
                    }
                }
            }
        }
        return shapes;
    }
}
