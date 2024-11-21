package me.contaria.glacier.mixin.memory.trim_lists.sound;

import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.WeightedSoundSet;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;

@Mixin(WeightedSoundSet.class)
public abstract class WeightedSoundSetMixin implements GlacierTrimmable {

    @Shadow
    @Final
    private List<SoundContainer<Sound>> sounds;

    @Override
    public void glacier$trim() {
        ((ArrayList<?>) this.sounds).trimToSize();
    }
}
