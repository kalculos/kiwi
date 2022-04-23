package org.inlambda.kiwi;

import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
public final class RandomHelper {

    @SuppressWarnings("unchecked")
    public static <T> T pickOrNull(Collection<T> t) {
        if (t.size() == 0) return null;
        return (T) t.toArray()[ThreadLocalRandom.current().nextInt(t.size())];
    }

    public static <T> T pickOrNull(List<T> t){
        if(t.size() == 0)return null;
        return t.get(ThreadLocalRandom.current().nextInt(t.size()));
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> pick(Collection<T> t) {
        return (Optional<T>) Optional.ofNullable(t.toArray()[ThreadLocalRandom.current().nextInt(t.size())]);
    }

    public static int number(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static int number(int from, int to){
        return ThreadLocalRandom.current().nextInt(from, to);
    }

    public static int number() {
        return number(9999);
    }

    public static String string(){
        return string(16);
    }

    public static String string(int len){
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char)number(65,65+57));
        }
        return sb.toString();
    }
}
