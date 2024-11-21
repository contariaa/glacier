package me.contaria.glacier.mixin.memory.empty_arrays;

import me.contaria.glacier.Glacier;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AdvancementRewards.class)
public abstract class AdvancementRewardsMixin {

    @ModifyVariable(
            method = "<init>",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private static Identifier[] deduplicateEmptyLootArray(Identifier[] loot) {
        if (loot != null && loot.length == 0) {
            return Glacier.EMPTY_IDENTIFIERS;
        }
        return loot;
    }

    @ModifyVariable(
            method = "<init>",
            at = @At("HEAD"),
            ordinal = 1,
            argsOnly = true
    )
    private static Identifier[] deduplicateEmptyRecipesArray(Identifier[] recipes) {
        if (recipes != null && recipes.length == 0) {
            return Glacier.EMPTY_IDENTIFIERS;
        }
        return recipes;
    }
}
