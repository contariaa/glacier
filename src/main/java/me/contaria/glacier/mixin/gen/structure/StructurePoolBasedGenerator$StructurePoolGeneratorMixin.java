package me.contaria.glacier.mixin.gen.structure;

import com.google.common.collect.Lists;
import me.contaria.glacier.optimization.gen.structure.GlacierStructureBlockInfo;
import me.contaria.glacier.optimization.gen.structure.GlacierStructurePool;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.JigsawBlockEntity;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.*;

import java.util.Deque;
import java.util.List;
import java.util.Random;

@Mixin(targets = "net/minecraft/structure/pool/StructurePoolBasedGenerator$StructurePoolGenerator")
public abstract class StructurePoolBasedGenerator$StructurePoolGeneratorMixin {

    @Shadow
    @Final
    private StructureManager structureManager;

    @Shadow
    @Final
    private Random random;

    @Shadow
    @Final
    private int maxSize;

    @Shadow
    @Final
    private ChunkGenerator chunkGenerator;

    @Shadow
    @Final
    private StructurePoolBasedGenerator.PieceFactory pieceFactory;

    @Shadow
    @Final
    private List<? super PoolStructurePiece> children;

    @Shadow
    @Final
    private Deque<StructurePoolBasedGenerator.ShapedPoolStructurePiece> structurePieces;

    /**
     * @author contaria
     * @reason Optimize village generation by caching highest y lookup
     */
    @Overwrite
    private void generatePiece(PoolStructurePiece piece, MutableObject<VoxelShape> voxelShape, int minY, int currentSize, boolean bl) {
        StructureManager manager = this.structureManager;
        Random random = this.random;
        ChunkGenerator generator = this.chunkGenerator;

        StructurePoolElement element = piece.getPoolElement();
        StructurePool.Projection projection = element.getProjection();
        boolean rigid = projection == StructurePool.Projection.RIGID;
        MutableObject<VoxelShape> shape = new MutableObject<>();
        BlockBox box = piece.getBoundingBox();
        int minY1 = box.minY;

        // use mutable to avoid lots of BlockPos allocations
        BlockPos.Mutable position = new BlockPos.Mutable();
        BlockPos.Mutable mutable = new BlockPos.Mutable();

        iteration:
        for (Structure.StructureBlockInfo info : element.getStructureBlockInfos(
                manager, piece.getPos(), piece.getRotation(), random
        )) {
            // get cached pool to avoid registry lookup
            StructurePool pool = ((GlacierStructureBlockInfo) info).glacier$getPool();
            if (pool == StructurePool.INVALID || (pool != StructurePool.EMPTY && pool.getElementCount() == 0)) {
                StructurePoolBasedGenerator.LOGGER.warn("Empty or none existent pool: {}", info.tag.getString("pool"));
                continue;
            }

            // only get these once to speed up JigsawBlock#attachmentMatches
            final Direction facing = JigsawBlock.getFacing(info.state);
            final Direction rotation = JigsawBlock.getRotation(info.state);
            final boolean rollable = JigsawBlockEntity.Joint.byName(info.tag.getString("joint"))
                    .orElseGet(() -> facing.getAxis().isHorizontal() ? JigsawBlockEntity.Joint.ALIGNED : JigsawBlockEntity.Joint.ROLLABLE) == JigsawBlockEntity.Joint.ROLLABLE;
            final String target = info.tag.getString("target");

            BlockPos pos = info.pos;
            position.set(pos, facing);
            int height = pos.getY() - minY1;
            int groundY = -1;

            MutableObject<VoxelShape> shape2;
            int minY2;
            if (box.contains(position)) {
                shape2 = shape;
                minY2 = minY1;
                if (shape.getValue() == null) {
                    shape.setValue(VoxelShapes.cuboid(Box.from(box)));
                }
            } else {
                shape2 = voxelShape;
                minY2 = minY;
            }

            List<StructurePoolElement> elements = Lists.newArrayList();
            if (currentSize != this.maxSize) {
                elements.addAll(pool.getElementIndicesInRandomOrder(random));
            }
            elements.addAll(((GlacierStructurePool) pool).glacier$getTerminatorPool().getElementIndicesInRandomOrder(random));

            for (StructurePoolElement element2 : elements) {
                if (element2 == EmptyPoolElement.INSTANCE) {
                    break;
                }

                for (BlockRotation rotation2 : BlockRotation.randomRotationOrder(random)) {
                    List<Structure.StructureBlockInfo> infos2 = element2.getStructureBlockInfos(
                            manager, BlockPos.ORIGIN, rotation2, random
                    );
                    BlockBox box2 = element2.getBoundingBox(manager, BlockPos.ORIGIN, rotation2);

                    int highestY = 0;
                    if (bl && box2.getBlockCountY() <= 16) {
                        // avoid using streams and get cached highestY to avoid registry lookups
                        for (Structure.StructureBlockInfo info2 : infos2) {
                            if (!box2.contains(info2.pos.offset(JigsawBlock.getFacing(info2.state)))) {
                                continue;
                            }

                            int infoHighestY = ((GlacierStructureBlockInfo) info2).glacier$getHighestY(manager);
                            if (infoHighestY > highestY) {
                                highestY = infoHighestY;
                            }
                        }
                    }

                    for (Structure.StructureBlockInfo info2 : infos2) {
                        if (!attachmentMatches(facing, rotation, rollable, target, info2)) {
                            continue;
                        }
                        BlockPos pos3 = info2.pos;
                        mutable.set(position, -pos3.getX(), -pos3.getY(), -pos3.getZ());
                        BlockBox box3 = element2.getBoundingBox(manager, mutable, rotation2);
                        StructurePool.Projection projection2 = element2.getProjection();
                        boolean rigid3 = projection2 == StructurePool.Projection.RIGID;
                        int y3 = pos3.getY();
                        int y3Offset = height - y3 + JigsawBlock.getFacing(info.state).getOffsetY();
                        int q;
                        if (rigid && rigid3) {
                            q = minY1 + y3Offset;
                        } else {
                            if (groundY == -1) {
                                groundY = generator.getHeightOnGround(pos.getX(), pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                            }
                            q = groundY - y3;
                        }

                        int yOffset = q - box3.minY;
                        BlockBox box3Offset = box3.translated(0, yOffset, 0);
                        if (highestY > 0) {
                            box3Offset.maxY = Math.max(box3Offset.minY + highestY + 1, box3Offset.maxY);
                        }

                        if (VoxelShapes.matchesAnywhere(shape2.getValue(), VoxelShapes.cuboid(Box.from(box3Offset).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) {
                            continue;
                        }

                        shape2.setValue(VoxelShapes.combine(shape2.getValue(), VoxelShapes.cuboid(Box.from(box3Offset)), BooleanBiFunction.ONLY_FIRST));
                        int groundDelta = piece.getGroundLevelDelta();
                        int groundDeltaJunction;
                        if (rigid3) {
                            groundDeltaJunction = groundDelta - y3Offset;
                        } else {
                            groundDeltaJunction = element2.getGroundLevelDelta();
                        }

                        PoolStructurePiece nextPiece = this.pieceFactory
                                .create(manager, element2, mutable.add(0, yOffset, 0), groundDeltaJunction, rotation2, box3Offset);
                        int maxY;
                        if (rigid) {
                            maxY = minY1 + height;
                        } else if (rigid3) {
                            maxY = q + y3;
                        } else {
                            if (groundY == -1) {
                                groundY = generator.getHeightOnGround(pos.getX(), pos.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
                            }
                            maxY = groundY + y3Offset / 2;
                        }

                        piece.addJunction(new JigsawJunction(position.getX(), maxY - height + groundDelta, position.getZ(), y3Offset, projection2));
                        nextPiece.addJunction(new JigsawJunction(pos.getX(), maxY - y3 + groundDeltaJunction, pos.getZ(), -y3Offset, projection));
                        this.children.add(nextPiece);
                        if (currentSize < this.maxSize) {
                            this.structurePieces.addLast(new StructurePoolBasedGenerator.ShapedPoolStructurePiece(nextPiece, shape2, minY2, currentSize + 1));
                        }
                        continue iteration;
                    }
                }
            }
        }
    }

    @Unique
    private static boolean attachmentMatches(Direction facing, Direction rotation, boolean rollable, String target, Structure.StructureBlockInfo info2) {
        return facing == JigsawBlock.getFacing(info2.state).getOpposite() && (rollable || rotation == JigsawBlock.getRotation(info2.state)) && target.equals(info2.tag.getString("name"));
    }
}
