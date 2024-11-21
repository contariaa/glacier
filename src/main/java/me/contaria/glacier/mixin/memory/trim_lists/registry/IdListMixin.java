package me.contaria.glacier.mixin.memory.trim_lists.registry;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import me.contaria.glacier.optimization.memory.trim_lists.GlacierTrimmable;
import net.minecraft.util.collection.IdList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

@Mixin(IdList.class)
public abstract class IdListMixin<T> implements GlacierTrimmable {

    @Shadow
    @Final
    private List<T> list;

    @Shadow
    private int nextId;

    @Unique
    private Reference2IntOpenHashMap<T> glacierIdMap;

    @WrapOperation(
            method = "<init>(I)V",
            at = @At(
                    value = "NEW",
                    target = "(I)Ljava/util/IdentityHashMap;",
                    remap = false
            )
    )
    private IdentityHashMap<T, Integer> replaceIdentityHashMap(int expected, Operation<IdentityHashMap<T, Integer>> original) {
        this.glacierIdMap = new Reference2IntOpenHashMap<>(expected);
        return null;
    }

    /**
     * @author contaria
     * @reason Replace with Reference2IntOpenHashMap
     */
    @Overwrite
    public void set(T value, int id) {
        this.glacierIdMap.put(value, id);

        while (this.list.size() <= id) {
            this.list.add(null);
        }

        this.list.set(id, value);
        if (this.nextId <= id) {
            this.nextId = id + 1;
        }
    }

    /**
     * @author contaria
     * @reason Replace with Reference2IntOpenHashMap
     */
    @Overwrite
    public int getId(T value) {
        return this.glacierIdMap.getOrDefault(value, -1);
    }

    /**
     * @author contaria
     * @reason Replace with Reference2IntOpenHashMap
     */
    @Overwrite
    public int size() {
        return this.glacierIdMap.size();
    }

    @Override
    public void glacier$trim() {
        this.glacierIdMap.trim();
        ((ArrayList<T>) this.list).trimToSize();
    }
}
