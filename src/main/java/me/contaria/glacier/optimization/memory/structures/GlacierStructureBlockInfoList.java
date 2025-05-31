package me.contaria.glacier.optimization.memory.structures;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * Minecraft stores the list of blocks in a structure as a list of {@link Structure.StructureBlockInfo}'s.
 * Since a lot of blocks only differ in their position, we can compress this data into a paletted view.
 * <p>
 * {@link GlacierStructureBlockInfoList#data} represents the {@link Structure.StructureBlockInfo}'s in a compressed view as a long array.
 * The data is stored as the compressed position (since structures usually don't span the integer limit we can get away with way less bits)
 * followed by the index of the {@link BlockState} and {@link CompoundTag} in their respective palettes.
 * <p>
 * Each entry is compressed to a bit sequence of length {@link GlacierStructureBlockInfoList#bitsPerEntry}:
 * <p>
 * The first {@link GlacierStructureBlockInfoList#xBits} bits represent the unsigned x coordinate.
 * The next {@link GlacierStructureBlockInfoList#yBits} bits represent the unsigned y coordinate.
 * The next {@link GlacierStructureBlockInfoList#zBits} bits represent the unsigned z coordinate.
 * The next {@link GlacierStructureBlockInfoList#stateBits} bits represent the index of the {@link BlockState} in the {@link GlacierStructureBlockInfoList#states} palette.
 * The next {@link GlacierStructureBlockInfoList#tagBits} bits represent the index of the {@link CompoundTag} in the {@link GlacierStructureBlockInfoList#tags} palette.
 * <p>
 * If the amount of entries in the palette is not a power of 2, the trailing bits of each long will be unused.
 * <p>
 * The total amount of StructureBlockInfos represented is stored in {@link GlacierStructureBlockInfoList#size}.
 * The amount of bits each entry takes up is stored in {@link GlacierStructureBlockInfoList#bitsPerEntry}.
 * <p>
 * Most {@link List} functions are unsupported and a mutable {@link Structure.StructureBlockInfo} is created and populated during iteration
 *
 * @see GlacierStructureBlockInfoIterator
 */
public class GlacierStructureBlockInfoList implements List<Structure.StructureBlockInfo> {
    private static final long[] EMPTY_DATA = new long[0];
    private static final CompoundTag[] NULL_TAGS = new CompoundTag[]{null};

    protected final long[] data;
    protected final BlockState[] states;
    protected final CompoundTag[] tags;
    protected final int xBits, yBits, zBits;
    protected final int stateBits, tagBits;
    protected final int bitsPerEntry;
    protected final int size;

    public GlacierStructureBlockInfoList(List<Structure.StructureBlockInfo> infos) throws GlacierCompressionException {
        this(infos, null);
    }

    public GlacierStructureBlockInfoList(List<Structure.StructureBlockInfo> infos, Predicate<Structure.StructureBlockInfo> predicate) throws GlacierCompressionException {
        List<Entry> entries = new ArrayList<>();
        List<BlockState> states = new ArrayList<>();
        List<CompoundTag> tags = new ArrayList<>();

        int maxX = 0;
        int maxY = 0;
        int maxZ = 0;

        for (Structure.StructureBlockInfo info : infos) {
            if (predicate != null && !predicate.test(info)) {
                continue;
            }

            int state = states.indexOf(info.state);
            if (state == -1) {
                state = states.size();
                states.add(info.state);
            }

            int tag = indexOf(tags, info.tag);
            if (tag == -1) {
                tag = tags.size();
                tags.add(info.tag);
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

            entries.add(new Entry(x, y, z, state, tag));
        }

        this.xBits = bits(maxX);
        this.yBits = bits(maxY);
        this.zBits = bits(maxZ);
        this.stateBits = bits(states.size() - 1);
        this.tagBits = bits(tags.size() - 1);
        this.bitsPerEntry = this.xBits + this.yBits + this.zBits + this.stateBits + this.tagBits;

        if (this.bitsPerEntry > 64) {
            throw new GlacierCompressionException("Too many bits per entry: " + this.bitsPerEntry);
        }

        this.size = entries.size();
        if (this.bitsPerEntry != 0) {
            int entriesPerLong = 64 / this.bitsPerEntry;
            this.data = new long[(this.size + entriesPerLong - 1) / entriesPerLong];
            for (int i = 0; i < this.size; i++) {
                this.data[i / entriesPerLong] |= entries.get(i).compress(this.xBits, this.yBits, this.zBits, this.stateBits) << ((i % entriesPerLong) * this.bitsPerEntry);
            }
        } else {
            this.data = EMPTY_DATA;
        }
        this.states = states.toArray(new BlockState[0]);
        this.tags = tags.size() == 1 && tags.get(0) == null ? NULL_TAGS : tags.toArray(new CompoundTag[0]);
    }

    private static int bits(int i) {
        int bits = 0;
        while (i >= 1 << bits) {
            bits++;
        }
        return bits;
    }

    private static <T> int indexOf(List<T> list, T o) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == o) {
                return i;
            }
        }
        return -1;
    }

    public List<Structure.StructureBlockInfo> filter(Predicate<BlockState> predicate) {
        int bitsPerEntry = this.bitsPerEntry;
        if (bitsPerEntry == 0) {
            if (predicate.test(this.states[0])) {
                return this;
            }
            return Collections.emptyList();
        }

        int entriesPerLong = 64 / bitsPerEntry;
        int offset = this.xBits + this.yBits + this.zBits;
        int mask = (1 << this.stateBits) - 1;

        IntList entries = new IntArrayList();
        for (int i = 0; i < this.size; i++) {
            long entry = this.data[i / entriesPerLong] >>> ((i % entriesPerLong) * bitsPerEntry);
            int state = (int) (entry >> offset & mask);

            if (predicate.test(this.states[state])) {
                entries.add(i);
            }
        }

        if (entries.isEmpty()) {
            return Collections.emptyList();
        }
        if (this.size == entries.size()) {
            return this;
        }
        return new GlacierFilteredStructureBlockInfoList(this, entries.toIntArray());
    }

    public List<Structure.StructureBlockInfo> filterForSLO(BlockBox blockBox, BlockMirror mirror, BlockRotation rotation, BlockPos pivot, BlockPos offset) {
        int bitsPerEntry = this.bitsPerEntry;
        if (bitsPerEntry == 0) {
            return this;
        }

        int xOffset = this.xBits;
        int yOffset = xOffset + this.yBits;
        int xMask = (1 << this.xBits) - 1;
        int yMask = (1 << this.yBits) - 1;
        int zMask = (1 << this.zBits) - 1;

        BlockPos.Mutable mutable = new BlockPos.Mutable();

        IntList entries = new IntArrayList();
        for (int i = 0; i < this.size; i++) {
            int entriesPerLong = 64 / bitsPerEntry;
            long entry = this.data[i / entriesPerLong] >>> ((i % entriesPerLong) * bitsPerEntry);

            int x = (int) (entry & xMask);
            int y = (int) (entry >> xOffset & yMask);
            int z = (int) (entry >> yOffset & zMask);

            mutable.set(x, y, z);
            transform(mutable, mirror, rotation, pivot);
            mutable.move(offset.getX(), offset.getY(), offset.getZ());

            if (blockBox.contains(mutable)) {
                entries.add(i);
            }
        }

        if (entries.isEmpty()) {
            if (this.isEmpty()) {
                return Collections.emptyList();
            }
            entries.add(0);
        }
        if (this.size == entries.size()) {
            return this;
        }
        return new GlacierFilteredStructureBlockInfoList(this, entries.toIntArray());
    }

    // copy of StructureTemplateOptimizer#transform
    private static void transform(BlockPos.Mutable mutable, BlockMirror mirror, BlockRotation rotation, BlockPos pivot) {
        int x = mutable.getX();
        int y = mutable.getY();
        int z = mutable.getZ();
        boolean flag = true;
        switch (mirror) {
            case LEFT_RIGHT:
                z = -z;
                break;
            case FRONT_BACK:
                x = -x;
                break;
            default:
                flag = false;
        }

        int pivotX = pivot.getX();
        int pivotZ = pivot.getZ();
        switch (rotation) {
            case COUNTERCLOCKWISE_90:
                mutable.set(pivotX - pivotZ + z, y, pivotX + pivotZ - x);
                return;
            case CLOCKWISE_90:
                mutable.set(pivotX + pivotZ - z, y, pivotZ - pivotX + x);
                return;
            case CLOCKWISE_180:
                mutable.set(pivotX + pivotX - x, y, pivotZ + pivotZ - z);
                return;
            default:
                if (flag) mutable.set(x, y, z);
        }
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
        return new GlacierStructureBlockInfoIterator(this);
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

    private static class Entry {
        private final int x, y, z;
        private final int state, tag;

        private Entry(int x, int y, int z, int state, int tag) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.state = state;
            this.tag = tag;
        }

        private long compress(int xBits, int yBits, int zBits, int stateBits) {
            return this.x + ((this.y + ((this.z + ((this.state + ((long) this.tag << stateBits)) << zBits)) << yBits)) << xBits);
        }
    }
}
