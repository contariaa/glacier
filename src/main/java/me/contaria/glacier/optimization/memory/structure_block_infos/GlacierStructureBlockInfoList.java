package me.contaria.glacier.optimization.memory.structure_block_infos;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

/**
 * Minecraft stores the list of blocks in a structure as a list of {@link Structure.StructureBlockInfo}'s.
 * Since a lot of blocks only differ in their position, we can compress this data into a paletted view.
 * <p>
 * The data is stored as the compressed position (since structures usually don't span the integer limit we can get away with way less bits)
 * followed by the index of the {@link MutableStructureBlockInfo} in the palette.
 * <p>
 * {@link Structure.StructureBlockInfo#pos} is a {@link BlockPos.Mutable} and set during iteration.
 */
public class GlacierStructureBlockInfoList implements List<Structure.StructureBlockInfo> {
    private final long[] data;
    private final MutableStructureBlockInfo[] palette;
    private final int xBits, yBits, zBits;
    private final int bitsPerEntry;
    private final int size;

    public GlacierStructureBlockInfoList(List<Structure.StructureBlockInfo> infos) throws GlacierCompressionException {
        this(infos, null);
    }

    public GlacierStructureBlockInfoList(List<Structure.StructureBlockInfo> infos, Predicate<Structure.StructureBlockInfo> predicate) throws GlacierCompressionException {
        List<Entry> entries = new ArrayList<>();
        List<MutableStructureBlockInfo> palette = new ArrayList<>();

        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;

        for (Structure.StructureBlockInfo info : infos) {
            if (predicate != null && !predicate.test(info)) {
                continue;
            }

            MutableStructureBlockInfo mutableInfo;
            if (info instanceof MutableStructureBlockInfo) {
                // reuse the same instance for filtered lists in PalettedBlockInfoList#blockToInfos
                mutableInfo = (MutableStructureBlockInfo) info;
            } else {
                mutableInfo = new MutableStructureBlockInfo(info.state, info.tag);
            }

            int index = palette.indexOf(mutableInfo);
            if (index == -1) {
                index = palette.size();
                palette.add(mutableInfo);
            }

            int x = info.pos.getX();
            int y = info.pos.getY();
            int z = info.pos.getZ();

            if (x < 0 || y < 0 || z < 0) {
                throw new GlacierCompressionException("Invalid StructureBlockInfo position: " + info.pos);
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
            if (z > maxZ) {
                maxZ = z;
            }

            entries.add(new Entry(x, y, z, index));
        }

        this.xBits = bits(maxX);
        this.yBits = bits(maxY);
        this.zBits = bits(maxZ);
        this.bitsPerEntry = this.xBits + this.yBits + this.zBits + bits(palette.size() - 1);

        if (this.bitsPerEntry > 64) {
            throw new GlacierCompressionException("Too many bits per entry: " + this.bitsPerEntry);
        }

        this.size = entries.size();
        if (this.bitsPerEntry != 0) {
            int entriesPerLong = 64 / this.bitsPerEntry;
            this.data = new long[(this.size + entriesPerLong - 1) / entriesPerLong];
            for (int i = 0; i < this.size; i++) {
                this.data[i / entriesPerLong] |= entries.get(i).compress(this.xBits, this.yBits, this.zBits) << ((i % entriesPerLong) * this.bitsPerEntry);
            }
        } else {
            this.data = null;
        }
        this.palette = palette.toArray(new MutableStructureBlockInfo[0]);
    }

    private static int bits(int i) {
        int bits = 0;
        while (i >= 1 << bits) {
            bits++;
        }
        return bits;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @NotNull
    @Override
    public Iterator<Structure.StructureBlockInfo> iterator() {
        return new Itr();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(Structure.StructureBlockInfo info) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Structure.StructureBlockInfo> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends Structure.StructureBlockInfo> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Structure.StructureBlockInfo get(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Structure.StructureBlockInfo set(int index, Structure.StructureBlockInfo element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Structure.StructureBlockInfo element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Structure.StructureBlockInfo remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<Structure.StructureBlockInfo> listIterator() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public ListIterator<Structure.StructureBlockInfo> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public List<Structure.StructureBlockInfo> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    private class Itr implements Iterator<Structure.StructureBlockInfo> {
        private final int xOffset, yOffset, zOffset;
        private final int xMask, yMask, zMask;
        private final int indexMask;

        private int index = 0;

        public Itr() {
            this.xOffset = GlacierStructureBlockInfoList.this.xBits;
            this.yOffset = this.xOffset + GlacierStructureBlockInfoList.this.yBits;
            this.zOffset = this.yOffset + GlacierStructureBlockInfoList.this.zBits;
            this.xMask = (1 << GlacierStructureBlockInfoList.this.xBits) - 1;
            this.yMask = (1 << GlacierStructureBlockInfoList.this.yBits) - 1;
            this.zMask = (1 << GlacierStructureBlockInfoList.this.zBits) - 1;
            this.indexMask = (1 << (GlacierStructureBlockInfoList.this.bitsPerEntry - this.zOffset)) - 1;
        }

        @Override
        public boolean hasNext() {
            return GlacierStructureBlockInfoList.this.size > this.index;
        }

        @Override
        public Structure.StructureBlockInfo next() {
            int index = this.index++;
            if (index >= GlacierStructureBlockInfoList.this.size) {
                throw new IndexOutOfBoundsException();
            }

            int bitsPerEntry = GlacierStructureBlockInfoList.this.bitsPerEntry;
            if (bitsPerEntry == 0) {
                // the position is guaranteed to be {0, 0, 0}, and there is exactly one element in the palette
                return GlacierStructureBlockInfoList.this.palette[0];
            }

            int entriesPerLong = 64 / bitsPerEntry;
            long entry = GlacierStructureBlockInfoList.this.data[index / entriesPerLong] >>> ((index % entriesPerLong) * bitsPerEntry);

            int x = (int) (entry & this.xMask);
            int y = (int) (entry >> this.xOffset & this.yMask);
            int z = (int) (entry >> this.yOffset & this.zMask);
            int i = (int) (entry >> this.zOffset & this.indexMask);

            Structure.StructureBlockInfo next = GlacierStructureBlockInfoList.this.palette[i];
            ((BlockPos.Mutable) next.pos).set(x, y, z);
            return next;
        }
    }

    public static class MutableStructureBlockInfo extends Structure.StructureBlockInfo {
        public MutableStructureBlockInfo(BlockState state, @Nullable CompoundTag tag) {
            super(new BlockPos.Mutable(), state, tag);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            MutableStructureBlockInfo info = (MutableStructureBlockInfo) o;
            return Objects.equals(this.state, info.state) && Objects.equals(this.tag, info.tag);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.state, this.tag);
        }
    }

    private static class Entry {
        private final int x, y, z;
        private final int index;

        private Entry(int x, int y, int z, int index) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.index = index;
        }

        private long compress(int xBits, int yBits, int zBits) {
            return this.x + ((this.y + ((this.z + ((long) this.index << zBits)) << yBits)) << xBits);
        }
    }
}
