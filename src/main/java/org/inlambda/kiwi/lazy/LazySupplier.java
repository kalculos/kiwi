package org.inlambda.kiwi.lazy;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * A thread safe supplier that caches result.
 * @param <V>
 */
@ApiStatus.AvailableSince("0.1.0")
public final class LazySupplier<V> implements Supplier<V>{
    private final Supplier<V> supplier;
    private volatile V result;

    private LazySupplier(Supplier<V> supplier) {
        this.supplier = supplier;
        this.result = null;
    }

    public static <V> LazySupplier<V> by(Supplier<V> supplier){
        return new LazySupplier<>(supplier);
    }

    @Override
    public V get(){
        if(result == null){
            synchronized (this) {
                if(result==null){
                    result = supplier.get();
                }
            }
        }
        return result;
    }
}
