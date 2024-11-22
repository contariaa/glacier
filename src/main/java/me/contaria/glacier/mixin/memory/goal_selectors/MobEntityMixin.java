package me.contaria.glacier.mixin.memory.goal_selectors;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Supplier;

/**
 * Minecraft creates two {@link GoalSelector}'s for every entity.
 * Because they are only used serverside, we return null on the client instead.
 */
@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "(Ljava/util/function/Supplier;)Lnet/minecraft/entity/ai/goal/GoalSelector;"
            )
    )
    private GoalSelector noClientSideGoalSelector(Supplier<Profiler> profiler, Operation<GoalSelector> original, EntityType<? extends MobEntity> entityType, World world) {
        if (world.isClient) {
            return null;
        }
        return original.call(profiler);
    }
}
