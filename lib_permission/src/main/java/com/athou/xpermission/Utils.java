package com.athou.xpermission;

import java.lang.reflect.Field;

/**
 * @author athoucai
 * @date 2019/4/10
 */
class Utils {
    private static final int SYNTHETIC = 0x00001000;
    private static final int FINAL = 0x00000010;
    private static final int SYNTHETIC_AND_FINAL = SYNTHETIC | FINAL;

    private static boolean checkModifier(int mod) {
        return (mod & SYNTHETIC_AND_FINAL) == SYNTHETIC_AND_FINAL;
    }

    public static Object getExternalClass(Object target) {
        return getField(target);
    }

    private static Object getField(Object target) {
        if (target == null) {
            return null;
        }
        Class classCache = target.getClass();
        if (!classCache.getName().contains("$")) {
            return target;
        }
        try {
            Field[] fields = classCache.getDeclaredFields();
            for (Field field : fields) {
                if (checkModifier(field.getModifiers())) {
                    field.setAccessible(true);
                    Object object = field.get(target);
                    return getField(object);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return getField(target);
    }
}
