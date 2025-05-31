package me.contaria.glacier.optimization.lazier_dfu;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * An even lazier implementation of the DataFixer than LazyDFU.
 * <p>
 * Instead of just lazily initialising rules, the entire building process is done lazily.
 * This saves some more startup time and RAM.
 * <p>
 * Compatible with LazyDFU as it simply wraps their builder.
 */
public class LazyDataFixer implements DataFixer {
    @NotNull
    private final Supplier<DataFixer> supplier;
    @Nullable
    private DataFixer fixer;

    public LazyDataFixer(@NotNull Supplier<DataFixer> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    @Override
    public <T> Dynamic<T> update(DSL.TypeReference type, Dynamic<T> input, int version, int newVersion) {
        if (version >= newVersion) {
            return input;
        }
        if (this.fixer == null) {
            this.fixer = Objects.requireNonNull(this.supplier.get());
        }
        return this.fixer.update(type, input, version, newVersion);
    }

    @Override
    public Schema getSchema(int key) {
        if (this.fixer == null) {
            this.fixer = Objects.requireNonNull(this.supplier.get());
        }
        return this.fixer.getSchema(key);
    }
}
