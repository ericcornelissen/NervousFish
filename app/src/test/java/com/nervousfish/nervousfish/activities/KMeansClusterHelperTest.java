package com.nervousfish.nervousfish.activities;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class KMeansClusterHelperTest {
    @Test
    public void testGetEncryptionKey() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));
        taps.add(new Timestamp(5));
        taps.add(new Timestamp(20));
        taps.add(new Timestamp(24));
        taps.add(new Timestamp(40));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        long key = (long) method.invoke(kMeansClusterHelper, taps);

        assertEquals(2 + 8 + 4 * (long) StrictMath.pow(10, 10 + 0), key);
    }

    @Test(expected = InvocationTargetException.class)
    public void testGetEncryptionKeyNoTaps() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        method.invoke(kMeansClusterHelper, taps);
    }

    @Test(expected = InvocationTargetException.class)
    public void testGetEncryptionKeySingleTap() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        method.invoke(kMeansClusterHelper, taps);
    }

    @Test(expected = InvocationTargetException.class)
    public void testGetEncryptionKeyDoubleTap() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));
        taps.add(new Timestamp(5));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        method.invoke(kMeansClusterHelper, taps);
    }

    @Test
    public void testGetEncryptionKeyTripleTap() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));
        taps.add(new Timestamp(5));
        taps.add(new Timestamp(11));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        long key = (long) method.invoke(kMeansClusterHelper, taps);

        assertEquals(2 + 2 * (long) StrictMath.pow(10, 10 + 0), key);
    }

    @Test
    public void testGetEncryptionKeyTripleTap2() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));
        taps.add(new Timestamp(6));
        taps.add(new Timestamp(10));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        long key = (long) method.invoke(kMeansClusterHelper, taps);

        assertEquals(1 + 2 * (long) StrictMath.pow(10, 10 + 0), key);
    }

    @Test
    public void testGetEncryptionKeyTripleTap3() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));
        taps.add(new Timestamp(6));
        taps.add(new Timestamp(7));
        taps.add(new Timestamp(8));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        long key = (long) method.invoke(kMeansClusterHelper, taps);

        assertEquals(1 + 3 * (long) StrictMath.pow(10, 10 + 0), key);
    }

    @Test
    public void testGetEncryptionKey15Tap() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));
        taps.add(new Timestamp(6));
        taps.add(new Timestamp(7));
        taps.add(new Timestamp(8));
        taps.add(new Timestamp(10));
        taps.add(new Timestamp(14));
        taps.add(new Timestamp(18));
        taps.add(new Timestamp(30));
        taps.add(new Timestamp(31));
        taps.add(new Timestamp(35));
        taps.add(new Timestamp(40));
        taps.add(new Timestamp(50));
        taps.add(new Timestamp(60));
        taps.add(new Timestamp(65));
        taps.add(new Timestamp(70));
        taps.add(new Timestamp(71));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        long key = (long) method.invoke(kMeansClusterHelper, taps);

        assertEquals(16241 + 1 * (long) StrictMath.pow(10, 10 + 1) + 5 * (long) StrictMath.pow(10, 10 + 0), key);
    }

    @Test(expected = InvocationTargetException.class)
    public void testGetEncryptionKeyNullList() throws Exception {

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        method.invoke(kMeansClusterHelper, (Object) null);
    }

    @Test(expected = InvocationTargetException.class)
    public void testGetEncryptionKeyNullTaps() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(null);

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmVerificationActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        method.invoke(kMeansClusterHelper, taps);
    }
}