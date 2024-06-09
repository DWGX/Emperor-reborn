package dev.robin.utils.client;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;

public class ReflectionUtil {
    public static Object getFieldValue(Object obj, String ... fields) {
        Class<?> clazz = obj.getClass();
        for (String string : fields) {
            try {
                Field f = clazz.getDeclaredField(string);
                f.setAccessible(true);
                try {
                    return f.get(obj);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            catch (NoSuchFieldException noSuchFieldException) {
            }
        }
        return null;
    }

    public static Field findField(Class<?> clazz, String ... fieldNames) {
        Exception failed = null;
        String[] var3 = fieldNames;
        int var4 = fieldNames.length;
        for (int var5 = 0; var5 < var4; ++var5) {
            String fieldName = var3[var5];
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            }
            catch (Exception var8) {
                failed = var8;
                continue;
            }
        }
        return null;
    }

    public static Object getField(Field f, Object o2) {
        try {
            return f.get(o2);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(Field f, Object v, Object o2) {
        try {
            f.set(o2, v);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Object invoke(Method m, Object o2, Object ... args) {
        try {
            return m.invoke(o2, args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Deprecated
    public static Object construction(Class<?> classIn, Object ... args) {
        Class[] arg = new Class[args.length];
        int i = 0;
        for (Object o2 : args) {
            arg[i] = o2.getClass();
            ++i;
        }
        try {
            Constructor<?> constructor = classIn.getConstructor(arg);
            return constructor.newInstance(args);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object callMethod(String className, String methodName, Object object, Object ... args) {
        try {
            Class<?> clazz = Class.forName(className);
            AccessibleObject method = null;
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                if (!declaredMethod.getName().equals(methodName)) continue;
                method = declaredMethod;
            }
            if (method == null) {
                throw new NoSuchMethodException();
            }
            method.setAccessible(true);
            return ((Method)method).invoke(object, args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getField(String className, String fieldName, Object object) {
        try {
            Class<?> clazz = Class.forName(className);
            AccessibleObject field = null;
            for (Field declaredField : clazz.getDeclaredFields()) {
                if (!declaredField.getName().equals(fieldName)) continue;
                field = declaredField;
            }
            if (field == null) {
                throw new NoSuchFieldException();
            }
            field.setAccessible(true);
            return ((Field)field).get(object);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object construction(Constructor<?> constructor, Object ... args) {
        try {
            return constructor.newInstance(args);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getFieldValue(Class<?> clazz, String ... fields) {
        for (String string : fields) {
            try {
                Field f = clazz.getDeclaredField(string);
                if (f == null) continue;
                f.setAccessible(true);
                try {
                    return f.get(null);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            catch (NoSuchFieldException e) {
                // empty catch block
            }
        }
        return null;
    }

    public static void setFieldValue(Object obj, Object value, String ... fields) {
        Class<?> clazz = obj.getClass();
        for (String string : fields) {
            try {
                Field f = clazz.getDeclaredField(string);
                if (f == null) continue;
                f.setAccessible(true);
                try {
                    f.set(obj, value);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                    return;
                }
            }
            catch (NoSuchFieldException e) {
                // empty catch block
            }
        }
    }

    public static void setFieldValue(Class<?> clazz, Object value, String ... fields) {
        for (String string : fields) {
            try {
                Field f = clazz.getDeclaredField(string);
                if (f == null) continue;
                f.setAccessible(true);
                try {
                    f.set(null, value);
                }
                catch (IllegalAccessException | IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            catch (NoSuchFieldException e) {
                // empty catch block
            }
        }
    }

    public static <T> T copy(T src, T dst) {
        for (Field f : ReflectionUtil.getAllFields(src.getClass())) {
            if (Modifier.isFinal(f.getModifiers()) || Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            try {
                f.set(dst, f.get(src));
            }
            catch (IllegalAccessException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
        return dst;
    }

    public static Field[] getAllFields(Class<?> clazz) {
        ArrayList fields = new ArrayList();
        do {
            Collections.addAll(fields, clazz.getDeclaredFields());
        } while ((clazz = clazz.getSuperclass()) != Object.class && clazz != null);
        return (Field[]) fields.toArray(new Field[0]);
    }
}

