package me.contaria.glacier.optimization.memory.biome_array_data;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.BiomeArray;
import net.minecraft.world.biome.source.BiomeSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PalettedBiomeArrayData implements BiomeArrayData {
    private final Biome[] biomes;
    private final int length;
    private final long[] data;
    public final int bitsPerEntry;
    public final int entriesPerLong;
    public final int bitMask;

    public PalettedBiomeArrayData(Biome[] biomes, int length, long[] data, int bitsPerEntry) {
        this.biomes = biomes;
        this.length = length;
        this.data = data;
        this.bitsPerEntry = bitsPerEntry;
        this.entriesPerLong = 64 / bitsPerEntry;
        this.bitMask = (1 << bitsPerEntry) - 1;
    }

    @Override
    public int[] toIntArray() {
        int[] rawIDs = this.getRawBiomeIDs();
        int[] intArray = new int[this.length];
        for (int i = 0; i < this.length; i++) {
            intArray[i] = rawIDs[this.getBiomeIndex(i)];
        }
        return intArray;
    }

    @Override
    public void toPacket(PacketByteBuf buf) {
        int[] rawIDs = this.getRawBiomeIDs();
        for (int i = 0; i < this.length; i++) {
            buf.writeInt(rawIDs[this.getBiomeIndex(i)]);
        }
    }

    @Override
    public BiomeArrayData copy() {
        return new PalettedBiomeArrayData(Arrays.copyOf(this.biomes, this.biomes.length), this.length, Arrays.copyOf(this.data, this.data.length), this.bitsPerEntry);
    }

    @Override
    public Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ) {
        int x = biomeX & BiomeArray.HORIZONTAL_BIT_MASK;
        int y = MathHelper.clamp(biomeY, 0, BiomeArray.VERTICAL_BIT_MASK);
        int z = biomeZ & BiomeArray.HORIZONTAL_BIT_MASK;
        return this.getBiome(y << BiomeArray.HORIZONTAL_SECTION_COUNT + BiomeArray.HORIZONTAL_SECTION_COUNT | z << BiomeArray.HORIZONTAL_SECTION_COUNT | x);
    }

    private Biome getBiome(int index) {
        return this.biomes[this.getBiomeIndex(index)];
    }

    private int getBiomeIndex(int index) {
        return readData(this.data, index, this.bitsPerEntry, this.entriesPerLong, this.bitMask);
    }

    private int[] getRawBiomeIDs() {
        int[] rawIDs = new int[this.biomes.length];
        for (int i = 0; i < rawIDs.length; i++) {
            rawIDs[i] = Registry.BIOME.getRawId(this.biomes[i]);
        }
        return rawIDs;
    }

    static BiomeArrayData create(int startX, int startZ, BiomeSource source, int length, int secondBiomeIndex, Biome firstBiome, Biome secondBiome) {
        List<Biome> biomes = new ArrayList<>();
        biomes.add(firstBiome);
        biomes.add(secondBiome);
        int bitsPerEntry = 1;
        int entriesPerLong = 64;
        long[] data = new long[(length + entriesPerLong - 1) / entriesPerLong];
        for (int i = secondBiomeIndex; i < length; i++) {
            int x = i & BiomeArray.HORIZONTAL_BIT_MASK;
            int y = i >> BiomeArray.HORIZONTAL_SECTION_COUNT + BiomeArray.HORIZONTAL_SECTION_COUNT & BiomeArray.VERTICAL_BIT_MASK;
            int z = i >> BiomeArray.HORIZONTAL_SECTION_COUNT & BiomeArray.HORIZONTAL_BIT_MASK;
            Biome biome = source.getBiomeForNoiseGen(startX + x, y, startZ + z);
            int biomeIndex = biomes.indexOf(biome);
            if (biomeIndex == -1) {
                biomes.add(biome);
                if (biomes.size() > 1 << bitsPerEntry) {
                    data = resize(data, length, bitsPerEntry, entriesPerLong, ++bitsPerEntry, entriesPerLong = 64 / bitsPerEntry, secondBiomeIndex, i);
                }
                biomeIndex = biomes.indexOf(biome);
            }
            writeData(data, i, biomeIndex, bitsPerEntry, entriesPerLong);
        }
        return new PalettedBiomeArrayData(biomes.toArray(new Biome[0]), length, data, bitsPerEntry);
    }

    static BiomeArrayData create(PacketByteBuf buf, int length, int secondBiomeIndex, int firstBiome, int secondBiome) {
        IntList biomes = new IntArrayList();
        biomes.add(firstBiome);
        biomes.add(secondBiome);
        int bitsPerEntry = 1;
        int entriesPerLong = 64;
        long[] data = new long[(length + entriesPerLong - 1) / entriesPerLong];
        for (int i = secondBiomeIndex; i < length; i++) {
            int biome = buf.readInt();
            int biomeIndex = biomes.indexOf(biome);
            if (biomeIndex == -1) {
                biomes.add(biome);
                if (biomes.size() > 1 << bitsPerEntry) {
                    data = resize(data, length, bitsPerEntry, entriesPerLong, ++bitsPerEntry, entriesPerLong = 64 / bitsPerEntry, secondBiomeIndex, i);
                }
                biomeIndex = biomes.indexOf(biome);
            }
            writeData(data, i, biomeIndex, bitsPerEntry, entriesPerLong);
        }
        return new PalettedBiomeArrayData(toBiomeArray(biomes), length, data, bitsPerEntry);
    }

    static BiomeArrayData create(int[] ids, int secondBiomeIndex, int firstBiome, int secondBiome) {
        IntList biomes = new IntArrayList();
        biomes.add(firstBiome);
        biomes.add(secondBiome);
        int bitsPerEntry = 1;
        int entriesPerLong = 64;
        long[] data = new long[(ids.length + entriesPerLong - 1) / entriesPerLong];
        for (int i = secondBiomeIndex; i < ids.length; i++) {
            int biome = ids[i];
            int biomeIndex = biomes.indexOf(biome);
            if (biomeIndex == -1) {
                biomeIndex = biomes.size();
                biomes.add(biome);
                if (biomeIndex > 1 << bitsPerEntry) {
                    data = resize(data, ids.length, bitsPerEntry, entriesPerLong, ++bitsPerEntry, entriesPerLong = 64 / bitsPerEntry, secondBiomeIndex, i);
                }
            }
            writeData(data, i, biomeIndex, bitsPerEntry, entriesPerLong);
        }
        return new PalettedBiomeArrayData(toBiomeArray(biomes), ids.length, data, bitsPerEntry);
    }

    private static Biome[] toBiomeArray(IntList rawIDs) {
        Biome[] biomes = new Biome[rawIDs.size()];
        for (int i = 0; i < biomes.length; i++) {
            biomes[i] = Registry.BIOME.get(rawIDs.getInt(i));
        }
        return biomes;
    }

    private static long[] resize(long[] data, int length, int oldBitsPerEntry, int oldEntriesPerLong, int newBitsPerEntry, int newEntriesPerLong, int fromIndex, int toIndex) {
        long[] resized = new long[(length + newEntriesPerLong - 1) / newEntriesPerLong];
        int oldBitMask = (1 << oldBitsPerEntry) - 1;
        for (int i = fromIndex; i < toIndex; i++) {
            writeData(resized, i, readData(data, i, oldBitsPerEntry, oldEntriesPerLong, oldBitMask), newBitsPerEntry, newEntriesPerLong);
        }
        return resized;
    }

    private static void writeData(long[] data, int index, int value, int bitsPerEntry, int entriesPerLong) {
        data[index / entriesPerLong] |= (long) value << ((index % entriesPerLong) * bitsPerEntry);
    }

    private static int readData(long[] data, int index, int bitsPerEntry, int entriesPerLong, int bitMask) {
        return (int) (data[index / entriesPerLong] >> ((index % entriesPerLong) * bitsPerEntry)) & bitMask;
    }
}
