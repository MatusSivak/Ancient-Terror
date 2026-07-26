package java8.features.util;

import java8.features.function.BiConsumer;
import java8.features.function.Function;

import java.util.Map;

public class MapUtils {
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<? super K, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v;
        if ((v = map.get(key)) == null) {
            V newValue;
            if ((newValue = mappingFunction.apply(key)) != null) {
                map.put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    public static <K, V> void forEach(Map<K, V> map, BiConsumer<? super K, ? super V> action) {
        Objects.requireNonNull(action);
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K k;
            V v;
            k = entry.getKey();
            v = entry.getValue();
            action.accept(k, v);
        }
    }

    public static <V> V getOrDefault(Map<?, V> map, Object key, V defaultValue) {
        V v;
        return (((v = map.get(key)) != null) || map.containsKey(key))
                ? v
                : defaultValue;
    }
}
