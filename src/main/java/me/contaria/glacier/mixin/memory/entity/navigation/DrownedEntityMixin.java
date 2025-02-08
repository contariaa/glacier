package me.contaria.glacier.mixin.memory.entity.navigation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.glacier.optimization.memory.navigation.DummyMobNavigation;
import me.contaria.glacier.optimization.memory.navigation.DummySwimNavigation;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @see MobEntityMixin
 */
@Mixin(DrownedEntity.class)
public abstract class DrownedEntityMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/world/World;)Lnet/minecraft/entity/ai/pathing/SwimNavigation;"
            )
    )
    private SwimNavigation noClientSideSwimNavigation(MobEntity entity, World world, Operation<SwimNavigation> original) {
        if (world.isClient) {
            return DummySwimNavigation.INSTANCE;
        }
        return original.call(entity, world);
    }

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/world/World;)Lnet/minecraft/entity/ai/pathing/MobNavigation;"
            )
    )
    private MobNavigation noClientSideMobNavigation(MobEntity entity, World world, Operation<MobNavigation> original) {
        if (world.isClient) {
            return DummyMobNavigation.INSTANCE;
        }
        return original.call(entity, world);
    }
}
