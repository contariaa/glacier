package me.contaria.glacier.optimization.memory.navigation;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.pathing.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class DummyMobNavigation extends MobNavigation {
    public static final DummyMobNavigation INSTANCE = new DummyMobNavigation();

    private DummyMobNavigation() {
        super(null, null);
    }

    @Override
    public void setCanSwim(boolean canSwim) {
    }

    @Override
    public boolean canSwim() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canEnterOpenDoors() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setCanPathThroughDoors(boolean canPathThroughDoors) {
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

    @Override
    public Path findPathTo(BlockPos target, int distance) {
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

    @Override
    protected boolean canWalkOnPath(PathNodeType pathType) {
        throw new UnsupportedOperationException();
    }
}
