package me.contaria.glacier.mixin.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.ForestRockFeature;
import net.minecraft.world.gen.feature.ForestRockFeatureConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.util.Random;

@Mixin(ForestRockFeature.class)
public abstract class ForestRockFeatureMixin extends Feature<ForestRockFeatureConfig> {

    public ForestRockFeatureMixin(Codec<ForestRockFeatureConfig> configCodec) {
        super(configCodec);
    }

    /**
     * @author contaria
     * @reason Avoid looking up the same BlockState twice and reduce BlockPos allocations
     */
    @Overwrite
    public boolean generate(
            ServerWorldAccess world,
            StructureAccessor structures,
            ChunkGenerator generator,
            Random random,
            BlockPos mutable,
            ForestRockFeatureConfig config
    ) {
        mutable = new BlockPos.Mutable(mutable.getX(), mutable.getY() - 1, mutable.getZ());
        while (mutable.getY() > 3) {
            ((BlockPos.Mutable) mutable).setY(mutable.getY() - 1);
            BlockState state = world.getBlockState(mutable);
            if (!state.isAir()) {
                Block block = state.getBlock();
                if (isSoil(block) || isStone(block)) {
                    ((BlockPos.Mutable) mutable).setY(mutable.getY() + 1);
                    break;
                }
            }
        }

        if (mutable.getY() <= 3) {
            return false;
        }

        int radius = config.startRadius;
        for (int i = 0; radius >= 0 && i < 3; i++) {
            int xRadius = radius + random.nextInt(2);
            int yRadius = radius + random.nextInt(2);
            int zRadius = radius + random.nextInt(2);
            float f = (float) (xRadius + yRadius + zRadius) * 0.333F + 0.5F;

            for (BlockPos pos : BlockPos.iterate(mutable.getX() - xRadius, mutable.getY() - yRadius, mutable.getZ() - zRadius, mutable.getX() + xRadius, mutable.getY() + yRadius, mutable.getZ() + zRadius)) {
                if (pos.getSquaredDistance(mutable) <= (double) (f * f)) {
                    world.setBlockState(pos, config.state, 4);
                }
            }

            ((BlockPos.Mutable) mutable).move(-(radius + 1) + random.nextInt(2 + radius * 2), random.nextInt(2), -(radius + 1) + random.nextInt(2 + radius * 2));
        }

        return true;
    }
}
