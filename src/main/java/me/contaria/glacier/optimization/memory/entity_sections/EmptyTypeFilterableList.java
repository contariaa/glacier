package me.contaria.glacier.optimization.memory.entity_sections;

import net.minecraft.util.collection.TypeFilterableList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EmptyTypeFilterableList<T> extends TypeFilterableList<T> {
    public EmptyTypeFilterableList(Class<T> elementType) {
        super(elementType);
    }

    @Override
    public boolean add(T e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public <S> Collection<S> getAllOfType(Class<S> type) {
        return Collections.emptyList();
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public List<T> getAllElements() {
        return Collections.emptyList();
    }

    @Override
    public int size() {
        return 0;
    }
}
