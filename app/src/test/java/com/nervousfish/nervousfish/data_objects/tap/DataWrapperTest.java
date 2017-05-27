package com.nervousfish.nervousfish.data_objects.tap;

import com.nervousfish.nervousfish.data_objects.DataWrapper;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DataWrapperTest {
    @Test
    public void testGetData() throws Exception {
        ATapData mock = mock(ATapData.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getData(), mock);
    }

    @Test
    public void testGetClazz() throws Exception {
        ATapData mock = mock(ATapData.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getClazz(), mock.getClass());
    }

}