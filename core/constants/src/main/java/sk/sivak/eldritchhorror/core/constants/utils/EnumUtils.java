package sk.sivak.eldritchhorror.core.constants.utils;

import java.util.Random;

public final class EnumUtils {
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
        int x = new Random().nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
}
