package com.nervousfish.nervousfish.data_objects;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.IllegalBlockSizeException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


public class DatabaseTest {
    private Profile profile;

    @Before
    public void setup() {
        List<KeyPair> profilePairs = new ArrayList<>();
        profilePairs.add(new KeyPair("Testpair", mock(IKey.class), mock(IKey.class)));
        this.profile = new Profile("Tester", profilePairs);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNullContactListNormalProfile() {
        new Database(null, profile);
    }

    @Test(expected = NullPointerException.class)
    public void testInstantiateWithNormalContactListNullProfile() {
        new Database(new ArrayList<>(), null);
    }

}