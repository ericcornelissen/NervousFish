package com.nervousfish.nervousfish.test;

import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;

import com.nervousfish.nervousfish.modules.constants.Constants;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@SuppressWarnings("PMD")
@RunWith(AndroidJUnit4.class)
public class ExampleUnitTest {
    ISample sample = Mockito.mock(ISample.class);

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void bar() {
        Mockito.when(sample.foo()).thenReturn(5);
        assertEquals(new SampleClass(sample).get(), 4);
        Constants constants = (Constants) BaseTest.accessConstructor(Constants.class, Mockito.mock(IServiceLocatorCreator.class));
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