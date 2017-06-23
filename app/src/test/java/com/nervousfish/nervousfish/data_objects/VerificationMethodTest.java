package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VerificationMethodTest {
    VerificationMethod verificationMethod;

    @Before
    public void testInstantiate() {
        verificationMethod = new VerificationMethod(VerificationMethodEnum.RHYTHM);
    }

    @Test(expected = NullPointerException.class)
    public void testNullConstructor() {
        new VerificationMethod(null);
    }

    @Test
    public void testGetVerificationMethod() {
        assertEquals(verificationMethod.getVerificationMethod(), VerificationMethodEnum.RHYTHM);
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        try (
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)
        ) {
            oos.writeObject(verificationMethod);
            byte[] bytes = bos.toByteArray();
            try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                Object object = ois.readObject();
                assertTrue(object.getClass().equals(VerificationMethod.class));
                assertEquals(((VerificationMethod) object).getVerificationMethod(), VerificationMethodEnum.RHYTHM);
            }
        }
    }
}
