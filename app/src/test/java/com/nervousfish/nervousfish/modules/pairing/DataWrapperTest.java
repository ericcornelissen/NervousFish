package com.nervousfish.nervousfish.modules.pairing;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DataWrapperTest {
    @Test
    public void testGetData() throws Exception {
        Serializable mock = mock(Serializable.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getData(), mock);
    }

    @Test
    public void testGetClazz() throws Exception {
        Serializable mock = mock(Serializable.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getClazz(), mock.getClass());
    }

}