package me.contaria.glacier.mixin.memory.entity.navigation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.BreakDoorGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Predicate;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin {

    // this is necessary because the check in DoorInteractGoal#method_30146 fails when we set navigation to null clientside
    @WrapOperation(
            method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/entity/mob/MobEntity;Ljava/util/function/Predicate;)Lnet/minecraft/entity/ai/goal/BreakDoorGoal;"
            )
    )
    private BreakDoorGoal noClientSideBreakDoorGoal(MobEntity entity, Predicate<Difficulty> difficultySufficientPredicate, Operation<BreakDoorGoal> original, EntityType<? extends ZombieEntity> entityType, World world) {
        if (world.isClient) {
            return null;
        }
        return original.call(entity, difficultySufficientPredicate);
    }
}
