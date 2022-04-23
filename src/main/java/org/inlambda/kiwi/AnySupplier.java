package org.inlambda.kiwi;

import java.util.function.Supplier;

@FunctionalInterface
public interface AnySupplier<T>{
    T get() throws Throwable;

    default Supplier<T> asSupplier() {
        return () -> {
            try {
                return get();
            } catch (Throwable e) {
                throw new IllegalStateException(e);
            }
        };
    }
}
