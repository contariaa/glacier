package me.contaria.glacier.mixin.memory.empty_arrays;

import me.contaria.glacier.Glacier;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LeafEntry.class)
public abstract class LeafEntryMixin {

    @ModifyVariable(
            method = "<init>",
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
