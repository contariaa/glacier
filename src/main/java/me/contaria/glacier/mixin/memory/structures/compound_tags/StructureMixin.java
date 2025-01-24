package me.contaria.glacier.mixin.memory.structures.compound_tags;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.contaria.glacier.Glacier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.structure.Structure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;

@Mixin(Structure.class)
public abstract class StructureMixin {
    @Unique
    private static final ThreadLocal<Map<String, String>> KEYS = new ThreadLocal<>();
    @Unique
    private static final ThreadLocal<Map<Tag, Tag>> TAGS = new ThreadLocal<>();

    @WrapMethod(
            method = "fromTag"
    )
    private void deduplicateStringTags(CompoundTag tag, Operation<Void> original) {
        KEYS.set(new Object2ObjectOpenHashMap<String, String>() {{
            put("id", "id");
            put("author", "author");
            put("sizeX", "sizeX");
            put("sizeY", "sizeY");
            put("sizeZ", "sizeZ");
        }});
        TAGS.set(new Object2ObjectOpenHashMap<>());

        try {
            original.call(tag);
        } finally {
            KEYS.remove();
            TAGS.remove();
        }
    }

    @ModifyArg(
            method = "fromTag",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/structure/Structure$StructureEntityInfo;<init>(Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/nbt/CompoundTag;)V"
            ),
            index = 2
    )
    private CompoundTag deduplicateEntityInfoTag(CompoundTag tag) {
        if (tag != null) {
            Glacier.deduplicateTag(KEYS.get(), TAGS.get(), tag);
        }
        return tag;
    }

    @ModifyArg(
            method = "loadPalettedBlockInfo",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/structure/Structure$StructureBlockInfo;<init>(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/nbt/CompoundTag;)V"
            ),
            index = 2
    )
    private CompoundTag deduplicateBlockInfoTag(CompoundTag tag) {
        if (tag != null) {
            Glacier.deduplicateTag(KEYS.get(), TAGS.get(), tag);
        }
        return tag;
    }
}
