package java8.features.util;

import java8.features.function.Consumer;
import java8.features.function.Predicate;

import java.util.Iterator;

public class IterableUtils {
    public static <T> void forEach(Iterable<T> iterable, Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : iterable) {
            action.accept(t);
        }
    }

    public static <E> boolean removeIf(Iterable<E> iterable, Predicate<? super E> filter) {
        Objects.requireNonNull(filter);
        boolean removed = false;
        final Iterator<E> each = iterable.iterator();
        while (each.hasNext()) {
            if (filter.test(each.next())) {
                each.remove();
                removed = true;
            }
        }
        return removed;
    }
}
