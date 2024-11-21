package me.contaria.glacier.mixin.memory.trim_lists.structure;

import net.minecraft.structure.Structure;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Structure.class)
public abstract class StructureMixin {

    @Shadow
    @Final
    private List<Structure.PalettedBlockInfoList> blockInfoLists;

    @Shadow
    @Final
    private List<Structure.StructureEntityInfo> entities;

    @Inject(
            method = {
                    "saveFromWorld",
                    "fromTag"
            },
            at = @At("RETURN")
    )
    private void trimStructureLists(CallbackInfo ci) {
        ((ArrayList<?>) this.blockInfoLists).trimToSize();
        ((ArrayList<?>) this.entities).trimToSize();
    }
}
