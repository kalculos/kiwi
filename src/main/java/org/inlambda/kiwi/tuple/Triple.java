package org.inlambda.kiwi.tuple;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * A triple of three different elements, immutable.
 * @param <A> the type of the first element
 * @param <B> the type of the second element
 * @param <C> the type of the third element
 */
@ApiStatus.AvailableSince("0.1.0")
@RequiredArgsConstructor
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public final class Triple<A,B,C> {
    public final A a;
    public final B b;
    public final C c;

    @Contract(" -> new")
    public Triple<C,B,A> asReversed(){
        return new Triple<>(c, b, a);
    }

    @SuppressWarnings("unchecked")
    public <T> boolean anyMatch(Predicate<T> predicate){
        return predicate.test((T) a) || predicate.test((T) b) || predicate.test((T) c);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> asImmutableList(Class<T> tClass){
        return (List<T>) List.of(a,b,c);
    }

    public boolean norNull(){
        return a != null && b != null && c != null;
    }
}
