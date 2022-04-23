package org.inlambda.kiwi.lazy;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * A thread-safe function that caches value.
 * @param <K>
 * @param <V>
 */
@ApiStatus.AvailableSince("0.1.0")
public final class LazyFunction<K,V> implements Function<K,V> {
    private final Function<K,V> function;
    private volatile V value;

    private LazyFunction(Function<K, V> function, V value) {
        this.function = function;
        this.value = value;
    }

    public static <K,V> LazyFunction<K,V> by(Function<K,V> function){
        return new LazyFunction<>(function,null);
    }

    @Override
    public V apply(K k) {
        if(value == null){
            synchronized (this){
                if(value==null){
                    value = function.apply(k);
                }
            }
        }
        return value;
    }
}
