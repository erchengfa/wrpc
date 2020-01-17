
package com.github.wang.wrpc.serialization.kryo;

public abstract class ReflectionUtils {

    public static boolean checkZeroArgConstructor(Class clazz) {
        try {
            clazz.getDeclaredConstructor();
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean isJdk(Class clazz) {
        return clazz.getName().startsWith("java.") || clazz.getName().startsWith("javax.");
    }
}
