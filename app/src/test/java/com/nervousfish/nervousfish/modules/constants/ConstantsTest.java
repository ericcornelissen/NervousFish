package com.nervousfish.nervousfish.modules.constants;

import com.nervousfish.nervousfish.BaseTest;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

 // We cannot test much more than just that the results are not null.
 // If we use serialization and change the names of the variables, then the tests don't compile anymore otherwise.
public class ConstantsTest {
    Constants constants = (Constants) BaseTest.accessConstructor(Constants.class, mock(IServiceLocator.class));

    @Test
    public void getDatabaseContactsPath() throws Exception {
        assertNotNull(constants.getDatabaseContactsPath());
    }

    @Test
    public void getDatabaseUserdataPath() throws Exception {
        assertNotNull(constants.getDatabaseUserdataPath());
    }

    @Test
    public void getUuid() throws Exception {
        assertNotNull(constants.getUuid());
    }

    @Test
    public void getSDPRecord() throws Exception {
        assertNotNull(constants.getSDPRecord());
    }

}
