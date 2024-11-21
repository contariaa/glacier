package me.contaria.glacier.mixin.memory.trim_lists.sound;

import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SoundManager.SoundList.class)
public abstract class SoundManager$SoundListMixin {

    @ModifyVariable(
            method = "register",
            at = @At("TAIL")
    )
    private WeightedSoundSet trimWeightedSoundSets(WeightedSoundSet weightedSoundSet) {
        ((GlacierTrimmable) weightedSoundSet).glacier$trim();
        return weightedSoundSet;
    }
}
