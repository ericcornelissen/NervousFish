package com.nervousfish.nervousfish.modules.pairing;

import com.nervousfish.nervousfish.data_objects.tap.ATapData;

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