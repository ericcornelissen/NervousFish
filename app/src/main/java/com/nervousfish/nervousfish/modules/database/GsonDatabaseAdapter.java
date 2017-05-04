package com.nervousfish.nervousfish.modules.database;

import com.nervousfish.nervousfish.data_objects.IContact;
import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.IServiceLocatorCreator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import java.io.BufferedWriter;
import java.io.File;
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

        this.initializeDatabase();
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
    public final void addContact(IContact contact) {
        // TODO: To be implemented
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void deleteContact(IContact contact) {
        // TODO: To be implemented
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<IContact> getAllContacts() {
        return new ArrayList<IContact>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void updateContact(IContact oldContact, IContact newContact) {
        // TODO: To be implemented
    }

    /**
     * Initialize the database.
     */
    private void initializeDatabase() {
        this.initializeContacts();
        this.initializeUserdata();
    }

    /**
     * Initialize the contacts in the database. This does nothing
     * if the contacts section of the database already exists.
     */
    private void initializeContacts() {
        final String constantsPath = constants.getDatabaseContactsPath();
        final File file = new File(constantsPath);
        if (!file.exists()) {
            try {
                final BufferedWriter bufferedWriter =
                        new BufferedWriter(new FileWriter(constantsPath));
                bufferedWriter.write("[]");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Initialize the userdata in the database. This does nothing
     * if the userdata section of the database already exists.
     */
    private void initializeUserdata() {
        final String userdataPath = constants.getDatabaseUserdataPath();
        final File file = new File(userdataPath);
        if (!file.exists()) {
            try {
                final BufferedWriter bufferedWriter =
                        new BufferedWriter(new FileWriter(userdataPath));
                bufferedWriter.write("[]");
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
