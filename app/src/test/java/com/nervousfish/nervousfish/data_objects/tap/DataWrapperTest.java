package com.nervousfish.nervousfish.data_objects.tap;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DataWrapperTest {
    @Test
    public void getTapData() throws Exception {
        AbstractTapData mock = mock(AbstractTapData.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getTapData(), mock);
    }

    @Test
    public void getClazz() throws Exception {
        AbstractTapData mock = mock(AbstractTapData.class);
        DataWrapper wrapper = new DataWrapper(mock);
        assertEquals(wrapper.getClazz(), mock.getClass());
    }

}