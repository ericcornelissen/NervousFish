package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.IContact;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An adapter to the GSON database library
 */
public final class GsonDatabaseAdapter implements IDatabase {

    final IConstants constants;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocatorCreator The object responsible for creating the service locator
     */
    GsonDatabaseAdapter(final IServiceLocatorCreator serviceLocatorCreator) {
        final IServiceLocator serviceLocator = serviceLocatorCreator.getServiceLocator();
        this.constants = serviceLocator.getConstants();
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocatorCreator} can access the new module to create the new
     * {@link IServiceLocator}.
     *
     * @param serviceLocatorCreator The service locator bridge that creates the new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<GsonDatabaseAdapter> newInstance(final IServiceLocatorCreator serviceLocatorCreator) {
        return new ModuleWrapper<>(new GsonDatabaseAdapter(serviceLocatorCreator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addContact(IContact contact) {
        // TODO: To be implemented
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteContact(IContact contact) {
        // TODO: To be implemented
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<IContact> getAllContacts() {
        return new ArrayList<IContact>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateContact(IContact oldContact, IContact newContact) {
        // TODO: To be implemented
    }

    /**
     * Initialize the database.
     */
    private void initializeDatabase() {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(constants.getAccountInformationFileName()));
            bufferedWriter.write("[]");
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
