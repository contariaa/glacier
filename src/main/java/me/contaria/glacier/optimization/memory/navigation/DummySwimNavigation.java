package me.contaria.glacier.optimization.memory.navigation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * A no-op, unmodifiable subclass of {@link SwimNavigation}, used by client-side entities who do not make use of it anyway.
 * Throws {@link UnsupportedOperationException} for any methods that shouldn't be called by a client-side entity.
 */
public class DummySwimNavigation extends SwimNavigation {
    public static final DummySwimNavigation INSTANCE = new DummySwimNavigation();

    private DummySwimNavigation() {
        super(null, null);
    }

    @Override
    public boolean canSwim() {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockPos getTargetPos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean shouldRecalculatePath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void recalculatePath() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    protected Path findPathToAny(Set<BlockPos> positions, int range, boolean bl, int distance) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startMovingTo(double x, double y, double z, double speed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startMovingTo(Entity entity, double speed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean startMovingAlong(@Nullable Path path, double speed) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public Path getCurrentPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void tick() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void continueFollowingPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void checkTimeouts(Vec3d currentPos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isIdle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFollowingPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void stop() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean isInLiquid() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void adjustPath() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isValidPosition(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    public PathNodeMaker getNodeMaker() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onBlockChanged(BlockPos pos) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected PathNodeNavigator createPathNodeNavigator(int range) {
        // set PathNodeNavigator to null
        // see EntityNavigation#<init>
        return null;
    }

    @Override
    protected Vec3d getPos() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean isAtValidPosition() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected boolean canPathDirectlyThrough(Vec3d origin, Vec3d target, int sizeX, int sizeY, int sizeZ) {
        throw new UnsupportedOperationException();
    }
}
