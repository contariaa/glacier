/**
 * Minecraft creates a lot of immutable {@link net.minecraft.util.math.BlockPos BlockPos} objects, especially during world generation.
 * These optimizations replace a lot of these with {@link net.minecraft.util.math.BlockPos.Mutable BlockPos.Mutable}'s to reduce allocation rate.
 */
package me.contaria.glacier.mixin.allocation.blockpos;