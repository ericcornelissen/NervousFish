package com.nervousfish.nervousfish;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@SuppressWarnings("PMD")
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    private interface ISample {
        int foo();
    }

    private class SampleClass {
        private final int tmp;
        private SampleClass(ISample sample) {
            tmp = sample.foo();
        }

        private int get() {
            return tmp;
        }
    }

    ISample sample = mock(ISample.class);

    @Test
    public void bar() {
        when(sample.foo()).thenReturn(5);
        assertEquals(new SampleClass(sample).get(), 5);
    }
}