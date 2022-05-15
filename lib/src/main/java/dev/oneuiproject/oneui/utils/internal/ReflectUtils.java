package dev.oneuiproject.oneui.utils.internal;

import static androidx.annotation.RestrictTo.Scope.LIBRARY;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.RestrictTo;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @hide
 */
@RestrictTo(LIBRARY)
public class ReflectUtils {

    /**
     * @hide
     */
    @RestrictTo(LIBRARY)
    public static Object genericInvokeMethod(Class<?> cl, Object obj, String methodName, Object... params) {
        final int paramCount = params.length;

        Method method;
        Object requiredObj = null;
        Class<?>[] classArray = new Class<?>[paramCount];

        for (int i = 0; i < paramCount; i++) {
            if (params[i].getClass() == Boolean.class)
                classArray[i] = Boolean.TYPE;
            else if (params[i].getClass() == Integer.class)
                classArray[i] = Integer.TYPE;
            else if (params[i].getClass() == Float.class)
                classArray[i] = Float.TYPE;
            else if (params[i].getClass() == Double.class)
                classArray[i] = Double.TYPE;
            else
                classArray[i] = params[i].getClass();
        }

        try {
            method = cl.getDeclaredMethod(methodName, classArray);
            method.setAccessible(true);
            requiredObj = method.invoke(obj, params);
        } catch (NoSuchMethodException | IllegalArgumentException
                | IllegalAccessException | InvocationTargetException e) {
            Log.e("ReflectUtils", "genericInvokeMethod: " + e);
        }

        return requiredObj;
    }

}
