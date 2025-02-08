package me.contaria.glacier.mixin.memory.entity.item_stack;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin {

    @WrapOperation(
            method = "readCustomDataFromTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;fromTag(Lnet/minecraft/nbt/CompoundTag;)Lnet/minecraft/item/ItemStack;"
            )
    )
    private ItemStack deduplicateEmptyItemStacks(CompoundTag tag, Operation<ItemStack> original) {
        if (tag.isEmpty()) {
            System.out.println("test");
            return ItemStack.EMPTY;
        }
        return original.call(tag);
    }
}
