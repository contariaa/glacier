package me.contaria.glacier;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.chunk.ProtoChunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Map;

public class Glacier {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final Direction[] DIRECTIONS = Direction.values();
    public static final Identifier[] EMPTY_IDENTIFIERS = new Identifier[0];
    public static final LootCondition[] EMPTY_CONDITIONS = new LootCondition[0];
    public static final LootFunction[] EMPTY_FUNCTIONS = new LootFunction[0];
    public static final LootPool[] EMPTY_POOLS = new LootPool[0];

    /**
     * Mutable joinBlockPos to reduce BlockPos allocations.
     *
     * @see ProtoChunk#joinBlockPos
     */
    public static BlockPos.Mutable joinBlockPos(BlockPos.Mutable mutable, short sectionRel, int sectionY, ChunkPos chunkPos) {
        return mutable.set((sectionRel & 15) + (chunkPos.x << 4), (sectionRel >>> 4 & 15) + (sectionY << 4), (sectionRel >>> 8 & 15) + (chunkPos.z << 4));
    }

    public static void deduplicateTag(Map<String, String> keys, Map<Tag, Tag> tags, CompoundTag tag) {
        for (String key : new HashSet<>(tag.getKeys())) {
            Tag element = tag.get(key);

            String deduplicatedKey = keys.putIfAbsent(key, key);
            if (deduplicatedKey != null) {
                tag.remove(key);
                tag.put(deduplicatedKey, element);
            }

            Tag deduplicatedElement = tags.putIfAbsent(element, element);
            if (deduplicatedElement != null) {
                tag.put(key, deduplicatedElement);
            }
        }
    }
}
