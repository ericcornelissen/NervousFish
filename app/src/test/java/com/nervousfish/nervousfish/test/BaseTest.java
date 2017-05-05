package com.nervousfish.nervousfish.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Provides Powermock-functionality to tests.
 */
public final class BaseTest {

    /**
     * Simple constructor for the BaseTest class
     */
    private BaseTest() {
        // Prevents instantiation
    }

    /**
     * Creates a new instance of a class that has an inaccessible constructor (e.g. private).
     * Note that there should be a single constructor in the class.
     *
     * @param clazz The class that should be instantiated
     * @param args  The arguments that should be passed to the constructor
     * @return A new instance of clazz
     */
    protected static Object accessConstructor(final Class<?> clazz, final Object... args) {
        try {
            final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            // Assuming a single constructor
            for (Constructor<?> constructor : constructors) {
                constructor.setAccessible(true);
                return constructor.newInstance(args);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * Executes an inaccessible method on an object (e.g. private method).
     *
     * @param object      The object owning the method
     * @param method_name The name of the method
     * @param args        The arguments of the method
     * @return The result of the method
     */
    protected static Object accessMethod(final Object object, final String method_name, final Object... args) {
        try {
            final Method[] methods = object.getClass().getDeclaredMethods();
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
     * Sets the value of an inaccessible field on an object (e.g. private field).
     *
     * @param object     The object owning the method
     * @param field_name The name of the field
     * @param value      The new value of the field
     */
    protected static void setField(final Object object, final String field_name, final Object value) {
        try {
            final Field field = object.getClass().getDeclaredField(field_name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }

    /**
     * Gets the value of an inaccessible field on an object (e.g. private field).
     *
     * @param object     The object owning the method
     * @param field_name The name of the field
     */
    protected static Object getField(final Object object, final String field_name) {
        try {
            final Field field = object.getClass().getDeclaredField(field_name);
            field.setAccessible(true);
            return field.get(object);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }
}
