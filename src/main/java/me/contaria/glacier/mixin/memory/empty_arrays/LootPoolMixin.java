package me.contaria.glacier.mixin.memory.empty_arrays;

import me.contaria.glacier.Glacier;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LootPool.class)
public abstract class LootPoolMixin {

    @ModifyVariable(
            method = "<init>([Lnet/minecraft/loot/entry/LootPoolEntry;[Lnet/minecraft/loot/condition/LootCondition;[Lnet/minecraft/loot/function/LootFunction;Lnet/minecraft/loot/LootTableRange;Lnet/minecraft/loot/UniformLootTableRange;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static LootCondition[] deduplicateEmptyConditionArray(LootCondition[] conditions) {
        if (conditions != null && conditions.length == 0) {
            return Glacier.EMPTY_CONDITIONS;
        }
        return conditions;
    }

    @ModifyVariable(
            method = "<init>([Lnet/minecraft/loot/entry/LootPoolEntry;[Lnet/minecraft/loot/condition/LootCondition;[Lnet/minecraft/loot/function/LootFunction;Lnet/minecraft/loot/LootTableRange;Lnet/minecraft/loot/UniformLootTableRange;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static LootFunction[] deduplicateEmptyFunctionArray(LootFunction[] functions) {
        if (functions != null && functions.length == 0) {
            return Glacier.EMPTY_FUNCTIONS;
        }
        return functions;
    }
}
