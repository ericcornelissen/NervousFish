package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatabaseTest {

    IServiceLocatorCreator serviceLocatorCreator = mock(IServiceLocatorCreator.class);
    IServiceLocator serviceLocator = mock(IServiceLocator.class);
    IConstants constants = mock(IConstants.class);

    private IDatabase database;

    @Before
    public void setup() {
        when(serviceLocatorCreator.getServiceLocator()).thenReturn(serviceLocator);
        when(serviceLocator.getConstants()).thenReturn(constants);

        database = new GsonDatabaseAdapter(serviceLocatorCreator);
    }

    @Test
    public void exampleTest() throws Exception {
        Contact contact = database.getContact(1);
        assertEquals(contact, new Contact("name", new SimpleKey("key")));
    }

}
