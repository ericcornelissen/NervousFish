package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MultiTapTest {

    @Test
    public void testInstantiate() {
        SingleTap tap = new SingleTap();
        List<SingleTap> list = new ArrayList<>();
        list.add(tap);
        MultiTap mTap = new MultiTap(list);
        assertNotNull(mTap);
        assertNotNull(mTap.getTaps());
    }

    @Test
    public void testInstantiateList() {
        SingleTap tap = new SingleTap();
        List<SingleTap> list = new ArrayList<>();
        list.add(tap);
        MultiTap mTap = new MultiTap(list);
        assertNotNull(mTap.getTaps());
    }

    @Test
    public void testCheckList() {
        SingleTap tap = new SingleTap();
        List<SingleTap> list = new ArrayList<>();
        list.add(tap);
        MultiTap mTap = new MultiTap(list);
        assertEquals(mTap.getTaps(), list);
    }

}
