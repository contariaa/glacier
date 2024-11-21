package me.contaria.glacier.mixin.chunk.initial_capacity;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Because of their small initial size, chunk and entity sets are rehashed a lot during world load.
 * This optimization initializes these sets with reasonable initial capacity.
 */
@Mixin(ThreadedAnvilChunkStorage.class)
public abstract class ThreadedAnvilChunkStorageMixin {

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Lit/unimi/dsi/fastutil/longs/Long2ObjectLinkedOpenHashMap;",
                    ordinal = 0,
                    remap = false
            )
    )
    private Long2ObjectLinkedOpenHashMap<?> setCapacityForChunkHolders() {
        return new Long2ObjectLinkedOpenHashMap<>(500);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Lit/unimi/dsi/fastutil/longs/LongOpenHashSet;",
                    ordinal = 0,
                    remap = false
            )
    )
    private LongOpenHashSet setCapacityForLoadedChunks() {
        return new LongOpenHashSet(500);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Lit/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap;",
                    remap = false
            )
    )
    private Int2ObjectOpenHashMap<?> setCapacityForEntityTrackers() {
        return new Int2ObjectOpenHashMap<>(100);
    }

    @Redirect(
            method = "<init>",
            at = @At(
                    value = "NEW",
                    target = "()Lit/unimi/dsi/fastutil/longs/Long2ByteOpenHashMap;",
                    remap = false
            )
    )
    private Long2ByteOpenHashMap setCapacityForField_23786() {
        return new Long2ByteOpenHashMap(500);
    }
}
