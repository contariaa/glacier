package me.contaria.glacier.mixin.memory.empty_arrays;

import me.contaria.glacier.optimization.memory.empty_arrays.LootFunctionArray;
import me.contaria.glacier.optimization.memory.empty_arrays.LootPoolArray;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LootTable.class)
public abstract class LootTableMixin {

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/loot/context/LootContextType;[Lnet/minecraft/loot/LootPool;[Lnet/minecraft/loot/function/LootFunction;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static LootPool[] deduplicateEmptyPoolArray(LootPool[] pools) {
        if (pools != null && pools.length == 0) {
            return LootPoolArray.EMPTY;
        }
        return pools;
    }

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/loot/context/LootContextType;[Lnet/minecraft/loot/LootPool;[Lnet/minecraft/loot/function/LootFunction;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static LootFunction[] deduplicateEmptyFunctionArray(LootFunction[] functions) {
        if (functions != null && functions.length == 0) {
            return LootFunctionArray.EMPTY;
        }
        return functions;
    }
}
