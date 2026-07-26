package java8.features.stream;

import java8.features.function.Function;
import java8.features.function.Predicate;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class Stream {

    public static <T> T findFirstOrException(Collection<T> collection, Predicate<T> predicate) {
        for (T t : collection) {
            if (!predicate.test(t)) {
                continue;
            }
            return t;
        }
        throw new ElementNotFoundException();
    }

    public static <T> int count(Collection<T> collection, Predicate<T> predicate) {
        int returnValue = 0;
        for (T t : collection) {
            if (predicate.test(t)) {
                returnValue++;
            }
        }
        return returnValue;
    }

    public static <T> boolean anyMatch(Collection<T> collection, Predicate<T> predicate) {
        for (T t : collection) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    public static <T, Out> Collection<Out> map(Collection<T> collection, Function<T, Out> function) {
        LinkedList<Out> outs = new LinkedList<Out>();
        for (T t : collection) {
            outs.add(function.apply(t));
        }
        return outs;
    }

    public static <T, R extends T> List<R> collectToList(Collection<T> collection) {
        return collectToList(collection, null);
    }

    public static <T, R extends T> List<R> collectToList(Collection<T> collection, Predicate<T> predicate) {
        List<R> returnList = new LinkedList<R>();

        for (T t : collection) {
            if (predicate != null && !predicate.test(t)) {
                continue;
            }
            returnList.add((R) t);
        }
        return returnList;
    }
}
