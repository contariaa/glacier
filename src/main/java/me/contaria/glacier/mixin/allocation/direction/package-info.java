/**
 * {@link net.minecraft.util.math.Direction#values()} allocates a new array on every invocation, producing a lot of allocations during world generation.
 * Replacing these calls with a cached {@link me.contaria.glacier.optimization.allocation.direction.DirectionValues#DIRECTIONS} array significantly reduces allocation rate.
 */
package me.contaria.glacier.mixin.allocation.direction;