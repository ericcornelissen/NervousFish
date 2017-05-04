package com.nervousfish.nervousfish;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * The superclass of all tests
 */
public abstract class BaseTest {

    /**
     * Creates a new instance of a class that has an inaccessible constructor (eg private)
     *
     * @param clazz The class that should be instantiated
     * @return A new instance of clazz
     */
    public Object accessConstructor(final Class<?> clazz) {
        try {
            final Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * Executes an inaccessible method on an object (eg private method)
     *
     * @param object      The object owning the method
     * @param method_name The name of the method
     * @param args        The arguments of the method
     * @return The result of the method
     */
    public Object accessMethod(final Object object, final String method_name, final Object... args) {
        try {
            final Method[] methods = object.getClass().getMethods();
            for (Method method : methods) {
                if (method.getName().equals(method_name)) {
                    method.setAccessible(true);
                    return method.invoke(object, args);
                }
            }
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * Sets the value of an inaccessible field on an object (eg private field)
     *
     * @param object     The object owning the method
     * @param field_name The name of the field
     * @param value      The new value of the field
     */
    public void setField(final Object object, final String field_name, final Object value) {
        try {
            final Field field = object.getClass().getField(field_name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * Gets the value of an inaccessible field on an object (eg private field)
     *
     * @param object     The object owning the method
     * @param field_name The name of the field
     */
    public Object getField(final Object object, final String field_name) {
        try {
            final Field field = object.getClass().getField(field_name);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
}
