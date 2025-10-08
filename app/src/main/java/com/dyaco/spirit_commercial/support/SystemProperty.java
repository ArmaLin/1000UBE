package com.dyaco.spirit_commercial.support;

import android.annotation.SuppressLint;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SystemProperty {
    private static Method getSystemProperty;
    private static Method setSystemProperty;

    private SystemProperty() {

    }

    static {
        try {
            @SuppressLint("PrivateApi")
            Class<?> S = Class.forName("android.os.SystemProperties");
            Method[] M = S.getMethods();
            for (Method m : M) {
                String n = m.getName();
                if (n.equals("get")) {
                    getSystemProperty = m;
                } else if (n.equals("set")) {
                    setSystemProperty = m;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static String get(String name, String default_value) {
        try {
            return (String) getSystemProperty.invoke(null, name, default_value);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return default_value;
    }

    public static void set(String name, String value) {
        try {
            setSystemProperty.invoke(null, name, value);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
