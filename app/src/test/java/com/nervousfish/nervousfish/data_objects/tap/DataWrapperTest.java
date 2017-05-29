package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DataWrapperTest {
    @Test
    public void getDataTest() throws Exception {
        final Serializable mock = mock(Serializable.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getData(), mock);
    }

    @Test
    public void getClazzTest() throws Exception {
        ATapData mock = mock(ATapData.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getClazz(), mock.getClass());
    }

}