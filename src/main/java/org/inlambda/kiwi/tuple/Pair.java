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
 * A pair is a tuple of two elements, immutable.
 * @param <L> the type of the first element
 * @param <R> the type of the second element
 */
@ApiStatus.AvailableSince("0.1.0")
@RequiredArgsConstructor
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public final class Pair<L, R> {
    private int hashCode;
    public final L left;
    public final R right;

    @Contract(" -> new")
    public Pair<R, L> asReversed(){
        return new Pair<>(right, left);
    }

    @SuppressWarnings("unchecked")
    public <T> boolean anyMatch(Predicate<T> predicate){
        return predicate.test((T) left) || predicate.test((T) right);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> asImmutableList(Class<T> tClass){
        return (List<T>) List.of(left,right);
    }

    public boolean norNull(){
        return left != null && right != null;
    }
}
