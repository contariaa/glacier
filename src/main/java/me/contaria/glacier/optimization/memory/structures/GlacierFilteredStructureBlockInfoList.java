package me.contaria.glacier.optimization.memory.structures;

import net.minecraft.structure.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GlacierFilteredStructureBlockInfoList implements List<Structure.StructureBlockInfo> {
    private final GlacierStructureBlockInfoList list;
    private final int[] entries;

    GlacierFilteredStructureBlockInfoList(GlacierStructureBlockInfoList list, int[] entries) {
        this.list = list;
        this.entries = entries;
    }

    @Override
    public int size() {
        return this.entries.length;
    }

    @Override
    public boolean isEmpty() {
        return this.entries.length == 0;
    }

    @NotNull
    @Override
    public Iterator<Structure.StructureBlockInfo> iterator() {
        return new GlacierStructureBlockInfoIterator(this.list) {
            @Override
            protected int toIndex(int index) {
                return GlacierFilteredStructureBlockInfoList.this.entries[index];
            }

            @Override
            protected int size() {
                return GlacierFilteredStructureBlockInfoList.this.entries.length;
            }
        };
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
}
