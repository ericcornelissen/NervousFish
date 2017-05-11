package com.nervousfish.nervousfish.data_objects;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SimpleKeyTest {

        @Test
        public void testCanBeInstantiatedWithArbitraryValues() {
            IKey key = new SimpleKey("foobar");
            assertNotNull(key);
        }

        @Test
        public void testGetKeyReturnsNotNull() {
            IKey key = new SimpleKey("foobar");
            assertNotNull(key.getKey());
        }

        @Test
        public void testGetKeyReturnsNonEmptyString() {
            IKey key = new SimpleKey("foobar");
            assertNotEquals("", key.getKey());
        }

        @Test
        public void testGetTypeReturnsSimple() {
            IKey key = new SimpleKey("foobar");
            assertEquals("simple", key.getType());
        }

        @Test
        public void testEqualsWorksWithNull() {
            IKey key = new SimpleKey("foobar");
            assertFalse(key.equals(null));
        }

        @Test
        public void testEqualsWorksWithArbitraryObject() {
            IKey key = new SimpleKey("foobar");
            assertFalse(key.equals("foobar"));
        }

        @Test
        public void testEqualsReturnsFalseForDifferentKeyTypes() {
            IKey keyA = new SimpleKey("Hello world!");
            IKey keyB = new RSAKey("foo", "bar");
            assertFalse(keyA.equals(keyB));
        }

        @Test
        public void testEqualsReturnsFalseForUnequalKeys() {
            IKey keyA = new SimpleKey("foobar");
            IKey keyB = new SimpleKey("Hello world!");
            assertFalse(keyA.equals(keyB));
        }

        @Test
        public void testEqualsReturnsTrueForEqualKeys() {
            IKey keyA = new SimpleKey("foobar");
            IKey keyB = new SimpleKey("foobar");
            assertTrue(keyA.equals(keyB));
        }

        @Test
        public void testHashCodeNotNull() {
            IKey key = new SimpleKey("foobar");
            assertNotNull(key.hashCode());
        }

}
