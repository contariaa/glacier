package me.contaria.glacier.optimization.memory.biome_array_data;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;

/**
 * Specialized replacement for the Biome array in {@link BiomeArray}.
 * <p>
 * Minecraft stores biomes in an array covering the entire biome view of the chunk.
 * This uses 4KB per chunk (1024 entries), but most chunks only contain 1-4 biomes at most.
 * <p>
 * To store this data more efficiently we instead use {@link SingleBiomeArrayData} for biomes with only a single biome
 * or {@link PalettedBiomeArrayData} to represent a Biome array in a compressed paletted view
 */
public interface BiomeArrayData {

    int[] toIntArray();

    void toPacket(PacketByteBuf buf);

    BiomeArrayData copy();

    Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ);

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
