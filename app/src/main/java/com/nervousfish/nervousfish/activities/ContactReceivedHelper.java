package com.nervousfish.nervousfish.activities;

import android.app.Activity;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.modules.database.IDatabase;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Contains shared functionality between classes to reduce code duplication.
 * This is an enum to prevent instiation of this helper class
 */
// Suppressed because we have an empty enum
@SuppressWarnings("checkstyle:nowhitespacebefore")
enum ContactReceivedHelper {
    ;

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactReceivedHelper.class);

    private enum ContactExists {
        NOT, BY_NAME
    }

    /**
     * Adds a new contact to the database. If it existed already, it asks the user if he wants to add
     * the received public key to the existing contact, or if he wants to create a contact with a new name.
     *
     * @param database The database in which the contact should be stored
     * @param activity The activity that should draw the prompts
     * @param contact  The newly received {@link Contact}
     */
    static void newContactReceived(final IDatabase database, final Activity activity, final Contact contact) {
        Validate.notNull(database);
        Validate.notNull(activity);
        Validate.notNull(contact);
      
        switch (checkExists(database, contact)) {
            case NOT:
                LOGGER.info("Adding contact to database...");
                try {
                    database.addContact(contact);
                    ContactReceivedHelper.handleNewContact(activity);
                } catch (final IOException e) {
                    LOGGER.error("Could not add contact to database", e);
                }
                break;
            case BY_NAME:
                LOGGER.warn("Contact with equal name and keys already existed...");
                ContactReceivedHelper.handleDuplicateContact(activity, contact);
                break;
            default:
                final String error = "No exhaustive check on ContactExists";
                LOGGER.error(error);
                throw new AssertionError(error);
        }
    }

    /**
     * Handle the case where a newly received contact is actually new.
     *
     * @param activity The {@link Activity} to use.
     */
    private static void handleNewContact(final Activity activity) {
        new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(activity.getString(R.string.contact_added_popup_title))
                .setContentText(activity.getString(R.string.contact_added_popup_explanation))
                .setConfirmText(activity.getString(R.string.dialog_ok))
                .show();
    }

    /**
     * Handle the case where a newly received contact is a duplicate.
     *
     * @param activity The {@link Activity} to use.
     * @param contact The new {@link Contact}.
     */
    private static void handleDuplicateContact(final Activity activity, final Contact contact) {
        assert activity != null;
        assert contact != null;
      
        new SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(activity.getString(R.string.contact_already_exists))
                .setContentText(String.format(activity.getString(R.string.contact_exists_nothing_done), contact.getName()))
                .setConfirmText(activity.getString(R.string.dialog_ok))
                .show();
    }

    /**
     * Checks if a name of a given contact exists in the database.
     *
     * @param contact A {@link Contact} object
     * @return true when a contact with the same exists in the database
     */
    private static ContactReceivedHelper.ContactExists checkExists(final IDatabase database, final Contact contact) {
        assert database != null;
        assert contact != null;
        final String name = contact.getName();
        try {
            final List<Contact> list = database.getAllContacts();
            for (final Contact e : list) {
                if (e.getName().equals(name)) {
                    return ContactReceivedHelper.ContactExists.BY_NAME;
                }
            }
        } catch (final IOException e) {
            LOGGER.error("Could not get contacts from database", e);
        }
        return ContactReceivedHelper.ContactExists.NOT;
    }
}