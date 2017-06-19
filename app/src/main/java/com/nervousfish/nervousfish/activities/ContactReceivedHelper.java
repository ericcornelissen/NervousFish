package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;

import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.modules.database.DatabaseException;
import com.nervousfish.nervousfish.modules.database.IDatabase;

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
        NOT, BY_NAME, BY_NAME_AND_KEYS
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
        if (contact == null) {
            return;
        }

        switch (checkExists(database, contact)) {
            case NOT:
                LOGGER.info("Adding contact to database...");
                try {
                    database.addContact(contact);
                    handleNewContact(activity);
                } catch (final IOException e) {
                    LOGGER.error("Could not add contact to database", e);
                }
                break;
            case BY_NAME:
                LOGGER.warn("Contact with equal name but different keys already existed...");
                handleExistingContact(database, activity, contact);
                break;
            case BY_NAME_AND_KEYS:
                LOGGER.warn("Contact with equal name and keys already existed...");
                handleDuplicateContact(activity, contact);
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
     * Handle the case where a newly received contact with a name already exists.
     *
     * @param database The {@link IDatabase} to use.
     * @param activity The {@link Activity} to use.
     * @param contact The new {@link Contact}.
     */
    private static void handleExistingContact(final IDatabase database, final Activity activity, final Contact contact) {
        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(activity.getString(R.string.contact_already_exists))
                .setContentText(String.format(activity.getString(R.string.contact_already_exists_with_name), contact.getName()))
                .setConfirmText(activity.getString(R.string.add_public_key_to_contact))
                .setCancelText(activity.getString(R.string.create_new_contact))
                .showCancelButton(true)
                .setCancelClickListener(new ContactReceivedHelper.CreateNewContactClickListener(activity, database, contact))
                .setConfirmClickListener(new ContactReceivedHelper.AddPublicKeyToContactsClickListener(database, contact))
                .show();
    }

    /**
     * Handle the case where a newly received contact is a duplicate.
     *
     * @param activity The {@link Activity} to use.
     * @param contact The new {@link Contact}.
     */
    private static void handleDuplicateContact(final Activity activity, final Contact contact) {
        new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
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
        final String name = contact.getName();
        try {
            final List<Contact> list = database.getAllContacts();
            for (final Contact e : list) {
                if (e.getName().equals(name)) {
                    if (checkKeysEqual(contact.getKeys(), e.getKeys())) {
                        return ContactReceivedHelper.ContactExists.BY_NAME_AND_KEYS;
                    }
                    return ContactReceivedHelper.ContactExists.BY_NAME;
                }
            }
        } catch (final IOException e) {
            LOGGER.error("Could not get contacts from database", e);
        }
        return ContactReceivedHelper.ContactExists.NOT;
    }

    /**
     * Check if the keys of two users are equal.
     *
     * @param keys1 The first {@link List} of {@link IKey}s.
     * @param keys2 The second {@link List} of {@link IKey}s.
     * @return A boolean indicating if all the {@link IKey}s in the first list are also in the second list.
     */
    private static boolean checkKeysEqual(final List<IKey> keys1, final List<IKey> keys2) {
        final Set<IKey> keysSet1 = new HashSet<>(keys1);
        for (final IKey key : keys2) {
            if (!keysSet1.contains(key)) {
                return false;
            }
        }
        return true;
    }

    private static final class CreateNewContactClickListener implements SweetAlertDialog.OnSweetClickListener {

        private final DialogInterface.OnClickListener confirmNewNameListener = new ConfirmNewNameClickListener();
        private final Activity activity;
        private final IDatabase database;
        private final Contact contact;
        private EditText edit_newContactName;

        /**
         * Create a click listener for the creation of a new {@link Contact}.
         *
         * @param activity The activity related to creating the {@link Contact}.
         * @param database The {@link IDatabase} instance to use.
         * @param contact The {@link Contact} to add.
         */
        CreateNewContactClickListener(final Activity activity, final IDatabase database, final Contact contact) {
            this.activity = activity;
            this.database = database;
            this.contact = contact;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final SweetAlertDialog sweetAlertDialog) {
            LOGGER.info("New contact button clicked");
            this.askForNewName();
        }

        /**
         * Prompt the user to give a name for a new user.
         */
        private void askForNewName() {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this.activity.getApplicationContext());
            this.edit_newContactName = new EditText(this.activity.getApplicationContext());

            alert.setTitle(this.activity.getString(R.string.create_new_contact));
            alert.setMessage(this.activity.getString(R.string.enter_name_contact));
            alert.setView(this.edit_newContactName);

            alert.setNeutralButton(this.activity.getString(R.string.confirm), this.confirmNewNameListener);
        }

        // Suppressed because we only want the OnSweetClickListener to have access to this class
        @SuppressWarnings({"InnerClassTooDeeplyNested", "NonStaticInnerClassInSecureContext"})
        final class ConfirmNewNameClickListener implements DialogInterface.OnClickListener {

            /**
             * {@inheritDoc}
             */
            @SuppressWarnings("UnqualifiedFieldAccess")
            // Suppressed, because we otherwise have to add ContactReceivedHelper.CreateNewContactClickListener.this. for each variable
            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                LOGGER.info("Confirmation button new contact name clicked");
                final String newName = edit_newContactName.getText().toString();
                LOGGER.info("New contact name entered is \"{}\"", newName);
                try {
                    if (database.contactExists(newName)) {
                        new SweetAlertDialog(activity, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText(activity.getString(R.string.contact_already_exists))
                                .setContentText(String.format(
                                        activity.getString(R.string.contact_already_exists_with_name),
                                        contact.getName()))
                                .setConfirmText(activity.getString(R.string.add_public_key_to_contact))
                                .setCancelText(activity.getString(R.string.create_new_contact))
                                .showCancelButton(true)
                                .setCancelClickListener(
                                        new ContactReceivedHelper.CreateNewContactClickListener(
                                                activity,
                                                database,
                                                contact))
                                .setConfirmClickListener(ContactReceivedHelper.CreateNewContactClickListener.this)
                                .show();
                    }
                } catch (final IOException e) {
                    LOGGER.error("Could not check if contact exists in database", e);
                    throw new DatabaseException(e);
                }

                LOGGER.info("Adding contact to database");

                try {
                    final IDatabase database = ContactReceivedHelper.CreateNewContactClickListener.this.database;
                    database.addContact(ContactReceivedHelper.CreateNewContactClickListener.this.contact);
                } catch (final IOException e) {
                    LOGGER.error("Couldn't add the new contact", e);
                }
            }
        }

    }

    private static final class AddPublicKeyToContactsClickListener implements SweetAlertDialog.OnSweetClickListener {

        private final IDatabase database;
        private final Contact contact;

        /**
         * Create a click listeners for the add public key to contact button.
         *
         * @param database The {@link IDatabase} instance to use.
         * @param contact The {@link Contact} to add the key to.
         */
        AddPublicKeyToContactsClickListener(final IDatabase database, final Contact contact) {
            this.database = database;
            this.contact = contact;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final SweetAlertDialog sweetAlertDialog) {
            try {
                final Contact existingContact = this.database.getContactWithName(this.contact.getName());
                existingContact.addKeys(this.contact.getKeys());
            } catch (final IOException e) {
                LOGGER.error("Couldn't get contacts from database", e);
            }

        }

    }

}