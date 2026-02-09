package me.contaria.glacier.mixin.memory.empty_arrays;

import me.contaria.glacier.optimization.memory.empty_arrays.LootConditionArray;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootPoolEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LootPoolEntry.class)
public abstract class LootPoolEntryMixin {

    @ModifyVariable(
            method = "<init>",
            at = @At("HEAD"),
            argsOnly = true
    )
    private static LootCondition[] deduplicateEmptyConditionArray(LootCondition[] conditions) {
        if (conditions != null && conditions.length == 0) {
            return LootConditionArray.EMPTY;
        }
        return conditions;
    }
}
