package com.nervousfish.nervousfish.activities;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class KMeansClusterHelperTest {
    @Test
    public void getEncryptionKey() throws Exception {
        final List<Timestamp> taps = new ArrayList<>();
        taps.add(new Timestamp(0));
        taps.add(new Timestamp(5));
        taps.add(new Timestamp(20));
        taps.add(new Timestamp(24));
        taps.add(new Timestamp(40));

        Class<?> kMeansClusterHelperClass = Class.forName("com.nervousfish.nervousfish.activities.RhythmCreateActivity$KMeansClusterHelper");
        Constructor<?> constructor = kMeansClusterHelperClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        Object kMeansClusterHelper = constructor.newInstance();
        Method method = kMeansClusterHelperClass.getDeclaredMethod("getEncryptionKey", List.class);
        method.setAccessible(true);
        int key = (int) method.invoke(kMeansClusterHelper, taps);

        assertEquals(2 + 8, key);
    }
}