package com.nervousfish.nervousfish.test;

import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    ISample sample = mock(ISample.class);

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void bar() {
        when(sample.foo()).thenReturn(5);
        assertEquals(new SampleClass(sample).get(), 5);
        Constants constants = (Constants) BaseTest.accessConstructor(Constants.class, mock(IServiceLocatorCreator.class));
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
}