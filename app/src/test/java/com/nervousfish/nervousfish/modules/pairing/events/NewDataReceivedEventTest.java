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
        this.data = mock(Object.class);
        this.clazz = this.data.getClass();
        this.event = new NewDataReceivedEvent(this.data, this.clazz);
    }

    @Test
    public void getData() throws Exception {
        assertThat(this.event.getData(), is(equalTo(this.data)));
    }

    @Test
    public void getClazz() throws Exception {
        assertThat(this.event.getClazz(), is(equalTo(this.clazz)));
    }

}