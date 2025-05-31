package me.contaria.glacier.optimization.memory.biome_array_data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;

/**
 * Specialized replacement for the {@link Biome} array in {@link BiomeArray}.
 * <p>
 * Minecraft stores biomes in an array covering the entire biome view of the chunk.
 * This uses 4KB per chunk (1024 entries), but most chunks only contain 1-4 biomes at most.
 * <p>
 * To store this data more efficiently we instead use {@link SingleBiomeArrayData} for biomes with only a single biome
 * or {@link PalettedBiomeArrayData} to represent a Biome array in a compressed paletted view.
 */
public interface BiomeArrayData {

    /**
     * Writes the data to a new integer array.
     * @see BiomeArray#toIntArray
     */
    int[] toIntArray();

    /**
     * Writes the data to the given {@link PacketByteBuf}.
     * @see BiomeArray#toPacket
     */
    void toPacket(PacketByteBuf buf);

    /**
     * Creates a copy of this {@link BiomeArrayData}.
     */
    BiomeArrayData copy();

    /**
     * @return The {@link Biome} at the given biome coordinates.
     * @see BiomeArray#getBiomeForNoiseGen
     */
    Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ);

    /**
     * @return A {@link BiomeArrayData} from a {@link BiomeSource}.
     */
    static BiomeArrayData create(ChunkPos pos, BiomeSource source, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException();
        }

        int startX = pos.getStartX() >> 2;
        int startZ = pos.getStartZ() >> 2;

        Biome firstBiome = source.getBiomeForNoiseGen(startX, 0, startZ);
        for (int i = 1; i < length; i++) {
            int x = i & BiomeArray.HORIZONTAL_BIT_MASK;
            int y = i >> BiomeArray.HORIZONTAL_SECTION_COUNT + BiomeArray.HORIZONTAL_SECTION_COUNT & BiomeArray.VERTICAL_BIT_MASK;
            int z = i >> BiomeArray.HORIZONTAL_SECTION_COUNT & BiomeArray.HORIZONTAL_BIT_MASK;
            Biome biome = source.getBiomeForNoiseGen(startX + x, y, startZ + z);
            if (biome != firstBiome) {
                return PalettedBiomeArrayData.create(startX, startZ, source, length, i, firstBiome, biome);
            }
        }
        return new SingleBiomeArrayData(firstBiome, length);
    }

    /**
     * @return A {@link BiomeArrayData} from a {@link PacketByteBuf}.
     */
    static BiomeArrayData create(PacketByteBuf buf, int length) {
        if (length <= 0) {
            throw new IllegalArgumentException();
        }

        int firstBiome = buf.readInt();
        for (int i = 1; i < length; i++) {
            int biome = buf.readInt();
            if (biome != firstBiome) {
                return PalettedBiomeArrayData.create(buf, length, i, firstBiome, biome);
            }
        }
        return new SingleBiomeArrayData(Registry.BIOME.get(firstBiome), length);
    }

    /**
     * @return A {@link BiomeArrayData} from raw biome id's.
     */
    static BiomeArrayData create(int[] ids) {
        if (ids.length == 0) {
            throw new IllegalArgumentException();
        }

        int firstBiome = ids[0];
        for (int i = 1; i < ids.length; i++) {
            int biome = ids[i];
            if (biome != firstBiome) {
                return PalettedBiomeArrayData.create(ids, i, firstBiome, biome);
            }
        }
        return new SingleBiomeArrayData(Registry.BIOME.get(firstBiome), ids.length);
    }
}
