package com.nervousfish.nervousfish;

import org.junit.Test;

import static com.nervousfish.nervousfish.BaseTest.accessConstructor;
import static org.junit.Assert.assertNotNull;

public final class ConstantKeywordsTest {

    @Test
    public void testConstantKeywords() {
        ConstantKeywords keywords = (ConstantKeywords) accessConstructor(ConstantKeywords.class);
        assertNotNull(keywords);
    }

}
