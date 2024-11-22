package me.contaria.glacier.mixin.memory.entity.navigation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;createNavigation(Lnet/minecraft/world/World;)Lnet/minecraft/entity/ai/pathing/EntityNavigation;"
            )
    )
    private EntityNavigation noClientSideEntityNavigation(MobEntity entity, World world, Operation<EntityNavigation> original) {
        if (world.isClient) {
            return null;
        }
        return original.call(entity, world);
    }
}
