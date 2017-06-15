package com.nervousfish.nervousfish.modules.pairing.events;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class NewDataReceivedEventTest {
    NewDataReceivedEvent event;
    Object data;
    Class<?> clazz;

    @Before
    public void init() {
        data = mock(Object.class);
        clazz = data.getClass();
        event = new NewDataReceivedEvent(data, clazz);
    }

    @Test
    public void getData() throws Exception {
        assertThat(event.getData(), is(equalTo(data)));
    }

    @Test
    public void getClazz() throws Exception {
        assertThat(event.getClazz(), is(equalTo(clazz)));
    }

}