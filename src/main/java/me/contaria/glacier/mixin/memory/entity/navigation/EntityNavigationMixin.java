package me.contaria.glacier.mixin.memory.entity.navigation;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.contaria.glacier.optimization.memory.navigation.DummyMobNavigation;
import me.contaria.glacier.optimization.memory.navigation.DummySwimNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * @see MobEntityMixin
 */
@Mixin(EntityNavigation.class)
public abstract class EntityNavigationMixin {

    @WrapOperation(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/MobEntity;getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D"
            )
    )
    private double dummyEntityAttribute(MobEntity entity, EntityAttribute attribute, Operation<Double> original) {
        //noinspection ConstantValue
        if ((Object) this instanceof DummyMobNavigation || (Object) this instanceof DummySwimNavigation) {
            return 0;
        }
        return original.call(entity, attribute);
    }
}
