package me.contaria.glacier.mixin.memory.trim_lists.structure;

import net.minecraft.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Structure.PalettedBlockInfoList.class)
public abstract class Structure$PalettedBlockInfoListMixin {

    @Inject(
            method = "<init>(Ljava/util/List;)V",
            at = @At("TAIL")
    )
    private void trimInfos(List<?> infos, CallbackInfo ci) {
        ((ArrayList<?>) infos).trimToSize();
    }
}
