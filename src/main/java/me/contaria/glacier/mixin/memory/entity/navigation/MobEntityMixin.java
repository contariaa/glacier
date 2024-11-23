package me.contaria.glacier.mixin.memory.entity.navigation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.glacier.optimization.memory.navigation.DummyMobNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Minecraft creates an {@link EntityNavigation} for every entity.
 * Because they are only used serverside, we return a dummy instance on the client instead.
 */
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
            return DummyMobNavigation.INSTANCE;
        }
        return original.call(entity, world);
    }
}
