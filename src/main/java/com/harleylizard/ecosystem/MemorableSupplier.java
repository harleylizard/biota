package com.harleylizard.ecosystem;

import java.util.function.Supplier;

public final class MemorableSupplier<T> implements Supplier<T> {
    private final Supplier<T> supplier;
    private T t;

    private MemorableSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        return t == null ? t = supplier.get() : t;
    }

    public static <T> Supplier<T> of(Supplier<T> supplier) {
        return new MemorableSupplier<>(supplier);
    }
}
