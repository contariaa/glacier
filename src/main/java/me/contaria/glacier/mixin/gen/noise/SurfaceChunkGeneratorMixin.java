package me.contaria.glacier.mixin.gen.noise;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import net.minecraft.block.BlockState;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.math.*;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.Heightmap;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.biome.source.TheEndBiomeSource;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.*;
import net.minecraft.world.gen.feature.StructureFeature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(SurfaceChunkGenerator.class)
public abstract class SurfaceChunkGeneratorMixin extends ChunkGenerator {

    @Shadow
    @Final
    private static float[] field_16649;

    @Shadow
    @Final
    private static float[] field_24775;

    @Shadow
    @Final
    private static BlockState AIR;

    @Shadow
    @Final
    protected ChunkGeneratorType field_24774;

    @Shadow
    @Final
    @Nullable
    private SimplexNoiseSampler field_24777;

    @Shadow
    @Final
    private int noiseSizeX;

    @Shadow
    @Final
    private int noiseSizeY;

    @Shadow
    @Final
    private int noiseSizeZ;

    @Shadow
    @Final
    private int verticalNoiseResolution;

    @Shadow
    @Final
    private int horizontalNoiseResolution;

    @Shadow
    protected abstract BlockState getBlockState(double density, int y);

    @Shadow
    protected abstract double method_28553(int i, int j);

    @Shadow
    protected abstract double sampleNoise(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch);

    @Shadow
    public abstract int getSeaLevel();

    public SurfaceChunkGeneratorMixin(BiomeSource biomeSource, StructuresConfig structuresConfig) {
        super(biomeSource, structuresConfig);
    }

    /**
     * Replace iterator usage with traditional loops.
     * Flatten the buffer array.
     * Simplify and reduce calculations.
     *
     * @author contaria
     * @reason Optimize and simplify noise population.
     */
    @SuppressWarnings("ForLoopReplaceableByForEach")
    @Overwrite
    public void populateNoise(WorldAccess world, StructureAccessor accessor, Chunk chunk) {
        ObjectList<StructurePiece> pieces = new ObjectArrayList<>(10);
        ObjectList<JigsawJunction> junctions = new ObjectArrayList<>(32);
        ChunkPos chunkPos = chunk.getPos();
        int chunkX = chunkPos.x;
        int chunkZ = chunkPos.z;
        int blockX = chunkX << 4;
        int blockZ = chunkZ << 4;

        for (int i = 0; i < StructureFeature.JIGSAW_STRUCTURES.size(); i++) {
            accessor.getStructuresWithChildren(ChunkSectionPos.from(chunkPos, 0), StructureFeature.JIGSAW_STRUCTURES.get(i)).forEach(start -> {
                List<StructurePiece> children = start.getChildren();
                for (int j = 0; j < children.size(); j++) {
                    StructurePiece piece = children.get(j);
                    if (piece.intersectsChunk(chunkPos, 12)) {
                        if (piece instanceof PoolStructurePiece) {
                            PoolStructurePiece poolStructurePiece = (PoolStructurePiece) piece;
                            StructurePool.Projection projection = poolStructurePiece.getPoolElement().getProjection();
                            if (projection == StructurePool.Projection.RIGID) {
                                pieces.add(poolStructurePiece);
                            }

                            List<JigsawJunction> pieceJunctions = poolStructurePiece.getJunctions();
                            for (int k = 0; k < pieceJunctions.size(); k++) {
                                JigsawJunction junction = pieceJunctions.get(k);
                                int x = junction.getSourceX();
                                int z = junction.getSourceZ();
                                if (x > blockX - 12 && z > blockZ - 12 && x < blockX + 15 + 12 && z < blockZ + 15 + 12) {
                                    junctions.add(junction);
                                }
                            }
                        } else {
                            pieces.add(piece);
                        }
                    }
                }
            });
        }

        final int noiseSizeX = this.noiseSizeX;
        final int noiseSizeY = this.noiseSizeY;
        final int noiseSizeZ = this.noiseSizeZ;

        final int verticalNoiseResolution = this.verticalNoiseResolution;
        final int horizontalNoiseResolution = this.horizontalNoiseResolution;

        int bufferOffset = noiseSizeY + 1;
        double[][] buffer = new double[2][(noiseSizeZ + 1) * (noiseSizeY + 1)];

        for (int noiseZ = 0; noiseZ < noiseSizeZ + 1; noiseZ++) {
            this.sampleNoiseColumn(buffer[0], bufferOffset * noiseZ, chunkX * noiseSizeX, chunkZ * noiseSizeZ + noiseZ, chunk);
        }

        ProtoChunk protoChunk = (ProtoChunk) chunk;
        Heightmap oceanFloor = protoChunk.getHeightmap(Heightmap.Type.OCEAN_FLOOR_WG);
        Heightmap worldSurface = protoChunk.getHeightmap(Heightmap.Type.WORLD_SURFACE_WG);

        for (int noiseX = 0; noiseX < noiseSizeX; noiseX++) {
            for (int noiseZ = 0; noiseZ < noiseSizeZ + 1; noiseZ++) {
                this.sampleNoiseColumn(buffer[1], bufferOffset * noiseZ, chunkX * noiseSizeX + noiseX + 1, chunkZ * noiseSizeZ + noiseZ, chunk);
            }

            for (int noiseZ = 0; noiseZ < noiseSizeZ; noiseZ++) {
                ChunkSection chunkSection = protoChunk.getSection(15);
                chunkSection.lock();

                double noise = buffer[0][noiseZ * bufferOffset + noiseSizeY];
                double noiseSouth = buffer[0][(noiseZ + 1) * bufferOffset + noiseSizeY];
                double noiseNorth = buffer[1][noiseZ * bufferOffset + noiseSizeY];
                double noiseNorthSouth = buffer[1][(noiseZ + 1) * bufferOffset + noiseSizeY];

                for (int noiseY = noiseSizeY - 1; noiseY >= 0; noiseY--) {
                    double noiseUp = noise;
                    double noiseSouthUp = noiseSouth;
                    double noiseNorthUp = noiseNorth;
                    double noiseNorthSouthUp = noiseNorthSouth;
                    noise = buffer[0][noiseZ * bufferOffset + noiseY];
                    noiseSouth = buffer[0][(noiseZ + 1) * bufferOffset + noiseY];
                    noiseNorth = buffer[1][noiseZ * bufferOffset + noiseY];
                    noiseNorthSouth = buffer[1][(noiseZ + 1) * bufferOffset + noiseY];

                    for (int resY = verticalNoiseResolution - 1; resY >= 0; resY--) {
                        int y = noiseY * verticalNoiseResolution + resY;
                        int yInSection = y & 15;
                        int chunkY = y >> 4;
                        if (chunkSection.getYOffset() >> 4 != chunkY) {
                            chunkSection.unlock();
                            chunkSection = protoChunk.getSection(chunkY);
                            chunkSection.lock();
                        }

                        double delta = (double) resY / (double) verticalNoiseResolution;
                        double noiseLerpVertical = MathHelper.lerp(delta, noise, noiseUp);
                        double noiseNorthLerpVertical = MathHelper.lerp(delta, noiseNorth, noiseNorthUp);
                        double noiseSouthLerpVertical = MathHelper.lerp(delta, noiseSouth, noiseSouthUp);
                        double noiseNorthSouthLerpVertical = MathHelper.lerp(delta, noiseNorthSouth, noiseNorthSouthUp);

                        for (int resX = 0; resX < horizontalNoiseResolution; resX++) {
                            int x = blockX + noiseX * horizontalNoiseResolution + resX;
                            int xInSection = x & 15;
                            double delta1 = (double) resX / (double) horizontalNoiseResolution;
                            double noiseLerp = MathHelper.lerp(delta1, noiseLerpVertical, noiseNorthLerpVertical);
                            double noiseSouthLerp = MathHelper.lerp(delta1, noiseSouthLerpVertical, noiseNorthSouthLerpVertical);

                            for (int resZ = 0; resZ < horizontalNoiseResolution; resZ++) {
                                int z = blockZ + noiseZ * horizontalNoiseResolution + resZ;
                                double density = MathHelper.clamp(MathHelper.lerp((double) resZ / (double) horizontalNoiseResolution, noiseLerp, noiseSouthLerp) / 200.0, -1.0, 1.0);
                                density = density / 2.0 - density * density * density / 24.0;

                                for (int i = 0; i < pieces.size(); i++) {
                                    StructurePiece structurePiece = pieces.get(i);
                                    BlockBox blockBox = structurePiece.getBoundingBox();
                                    density += method_16572(Math.max(0, Math.max(blockBox.minX - x, x - blockBox.maxX)), y - (blockBox.minY + (structurePiece instanceof PoolStructurePiece ? ((PoolStructurePiece) structurePiece).getGroundLevelDelta() : 0)), Math.max(0, Math.max(blockBox.minZ - z, z - blockBox.maxZ))) * 0.8;
                                }

                                for (int i = 0; i < junctions.size(); i++) {
                                    JigsawJunction jigsawJunction = junctions.get(i);
                                    density += method_16572(x - jigsawJunction.getSourceX(), y - jigsawJunction.getSourceGroundY(), z - jigsawJunction.getSourceZ()) * 0.4;
                                }

                                BlockState blockState = this.getBlockState(density, y);
                                if (blockState != AIR) {
                                    if (blockState.getLuminance() != 0) {
                                        protoChunk.addLightSource(new BlockPos(x, y, z));
                                    }

                                    int zInSection = z & 15;
                                    chunkSection.setBlockState(xInSection, yInSection, zInSection, blockState, false);
                                    oceanFloor.trackUpdate(xInSection, y, zInSection, blockState);
                                    worldSurface.trackUpdate(xInSection, y, zInSection, blockState);
                                }
                            }
                        }
                    }
                }

                chunkSection.unlock();
            }

            double[] b = buffer[0];
            buffer[0] = buffer[1];
            buffer[1] = b;
        }
    }

    // [VanillaCopy] SurfaceChunkGenerator#sampleNoiseColumn
    // Copy of the vanilla method with added bufferOffset
    // and skipping sampling if noise is interpolated out anyway
    @Unique
    private void sampleNoiseColumn(double[] buffer, int bufferOffset, int x, int z, Chunk chunk) {
        NoiseConfig noiseConfig = this.field_24774.method_28559();
        double biomeNoise;
        double biomeNoiseFactor;
        if (this.field_24777 != null) {
            biomeNoise = TheEndBiomeSource.getNoiseAt(this.field_24777, x, z) - 8.0F;
            if (biomeNoise > 0.0) {
                biomeNoiseFactor = 0.25;
            } else {
                biomeNoiseFactor = 1.0;
            }
        } else {
            float f = 0.0F;
            float g = 0.0F;
            float h = 0.0F;
            int j = this.getSeaLevel();
            float depthToCompareTo = this.getBiomeForNoiseGen(chunk, x, j, z).getDepth();

            for (int l = -2; l <= 2; l++) {
                for (int m = -2; m <= 2; m++) {
                    Biome biome = this.getBiomeForNoiseGen(chunk, x + l, j, z + m);
                    float depth = biome.getDepth();
                    float scale = biome.getScale();
                    float amplifiedDepth;
                    float amplifiedScale;
                    if (noiseConfig.isAmplified() && depth > 0.0F) {
                        amplifiedDepth = 1.0F + depth * 2.0F;
                        amplifiedScale = 1.0F + scale * 4.0F;
                    } else {
                        amplifiedDepth = depth;
                        amplifiedScale = scale;
                    }

                    float r = depth > depthToCompareTo ? 0.5F : 1.0F;
                    float s = r * field_24775[l + 2 + (m + 2) * 5] / (amplifiedDepth + 2.0F);
                    f += amplifiedScale * s;
                    g += amplifiedDepth * s;
                    h += s;
                }
            }

            float t = g / h;
            float u = f / h;
            double v = t * 0.5F - 0.125F;
            double w = u * 0.9F + 0.1F;
            biomeNoise = v * 0.265625;
            biomeNoiseFactor = 96.0 / w;
        }

        double horizontalScale = 684.412 * noiseConfig.getSampling().getXZScale();
        double verticalScale = 684.412 * noiseConfig.getSampling().getYScale();
        double horizontalStretch = horizontalScale / noiseConfig.getSampling().getXZFactor();
        double verticalStretch = verticalScale / noiseConfig.getSampling().getYFactor();
        double topTarget = noiseConfig.getTopSlide().getTarget();
        double topSize = noiseConfig.getTopSlide().getSize();
        double topOffset = noiseConfig.getTopSlide().getOffset();
        double bottomTarget = noiseConfig.getBottomSlide().getTarget();
        double bottomSize = noiseConfig.getBottomSlide().getSize();
        double bottomOffset = noiseConfig.getBottomSlide().getOffset();
        double densityRandomOffset = noiseConfig.hasRandomDensityOffset() ? this.method_28553(x, z) : 0.0;
        double densityFactor = noiseConfig.getDensityFactor();
        double densityOffset = noiseConfig.getDensityOffset();

        int y = 0;
        int topY = this.noiseSizeY;

        // skip sampling noise if it's interpolated out anyway
        if (bottomSize > 0.0) {
            for (; y <= topY && ((double) y - bottomOffset) / bottomSize <= 0.0; y++) {
                buffer[bufferOffset + y] = bottomTarget;
            }
        }
        if (topSize > 0.0) {
            for (; topY >= y && ((double) topY - topOffset) / topSize <= 0.0; topY--) {
                buffer[bufferOffset + topY] = topTarget;
            }
        }

        for (; y <= topY; y++) {
            double noise = this.sampleNoise(x, y, z, horizontalScale, verticalScale, horizontalStretch, verticalStretch);
            double am = 1.0 - (double) y * 2.0 / (double) this.noiseSizeY + densityRandomOffset;
            double an = am * densityFactor + densityOffset;
            double density = (an + biomeNoise) * biomeNoiseFactor;
            if (density > 0.0) {
                noise += density * 4.0;
            } else {
                noise += density;
            }

            if (topSize > 0.0) {
                noise = MathHelper.clampedLerp(topTarget, noise, ((double) (this.noiseSizeY - y) - topOffset) / topSize);
            }

            if (bottomSize > 0.0) {
                noise = MathHelper.clampedLerp(bottomTarget, noise, ((double) y - bottomOffset) / bottomSize);
            }

            buffer[bufferOffset + y] = noise;
        }
    }

    @Unique
    private Biome getBiomeForNoiseGen(Chunk chunk, int biomeX, int biomeY, int biomeZ) {
        int relativeX = biomeX - (chunk.getPos().x << 2);
        int relativeZ = biomeZ - (chunk.getPos().z << 2);
        if (chunk.getBiomeArray() != null && relativeX >= 0 && relativeX < 4 && relativeZ >= 0 && relativeZ < 4) {
            return chunk.getBiomeArray().getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
        }
        return this.biomeSource.getBiomeForNoiseGen(biomeX, biomeY, biomeZ);
    }

    /**
     * @author contaria
     * @reason Simplify
     */
    @Overwrite
    private static double method_16572(int x, int y, int z) {
        if (x >= -12 && x < 12 && y >= -12 && y < 12 && z >= -12 && z < 12) {
            return field_16649[(z + 12) * 24 * 24 + (x + 12) * 24 + y + 12];
        }
        return 0.0;
    }
}
