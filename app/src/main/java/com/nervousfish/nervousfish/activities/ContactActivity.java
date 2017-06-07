package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * An {@link Activity} that shows the contacts information and his public keys.
 */
public final class ContactActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("ContactActivity");

    private IServiceLocator serviceLocator;
    private Contact contact;

    /**
     * Creates the new activity, should only be called by Android
     *
     * @param savedInstanceState The saved state of the instance.
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_contact);

        final Intent intent = this.getIntent();
        this.serviceLocator = NervousFish.getServiceLocator();
        this.contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);

        ContactActivityHelper.setName(this, this.contact.getName(), R.id.contact_name);
        ContactActivityHelper.setKeys(this, this.contact.getKeys(), R.id.list_view_contact);

        final ImageButton backButton = (ImageButton) findViewById(R.id.back_button_change);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                finish();
            }
        });
    }

    /**
     * Opens when the 3 dots are clicked.
     *
     * @param v - the View element clicked
     */
    public void showPopupMenu(final View v) {
        final PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenuListener());
        final MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_contact_menu, popup.getMenu());
        popup.show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_FIRST_USER) {
            this.contact = (Contact) data.getSerializableExtra(ConstantKeywords.CONTACT);
            ContactActivityHelper.setName(this, this.contact.getName(), R.id.contact_name);
        }
    }

    private final class PopupMenuListener implements PopupMenu.OnMenuItemClickListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onMenuItemClick(final MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.delete_contact_menu_item) {
                new SweetAlertDialog(ContactActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText(getString(R.string.are_you_sure))
                        .setContentText(getString(R.string.delete_popup_no_recovery))
                        .setCancelText(getString(R.string.cancel))
                        .setConfirmText(getString(R.string.yes_delete))
                        .setConfirmClickListener(new DeleteContactClickListener())
                        .show();
                return true;
            } else if (menuItem.getItemId() == R.id.edit_contact_menu_iten) {
                final Intent intent = new Intent(ContactActivity.this, ChangeContactActivity.class);
                intent.putExtra(ConstantKeywords.CONTACT, contact);
                ContactActivity.this.startActivityForResult(intent, RESULT_FIRST_USER);
                return true;
            } else {
                return false;
            }
        }

    }

    private final class DeleteContactClickListener implements SweetAlertDialog.OnSweetClickListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final SweetAlertDialog sweetAlertDialog) {
            try {
                serviceLocator.getDatabase().deleteContact(contact.getName());
                sweetAlertDialog.setTitleText(getString(R.string.contact_deleted_title))
                        .setContentText(getString(R.string.contact_deleted_description))
                        .setConfirmText(getString(R.string.dialog_ok))
                        .setConfirmClickListener(new DismissClickListener())
                        .showCancelButton(false)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            } catch (final IllegalArgumentException e) {
                LOGGER.error("IllegalArgumentException while deleting contact in ContactActivity", e);
                sweetAlertDialog.setTitleText(getString(R.string.contact_doesnt_exist))
                        .setContentText(getString(R.string.contact_already_deleted))
                        .setConfirmText(getString(R.string.dialog_ok))
                        .setConfirmClickListener(new DismissClickListener())
                        .showCancelButton(false)
                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);
            } catch (final IOException e) {
                LOGGER.error("IOException while deleting contact in ContactActivity", e);
                sweetAlertDialog.setTitleText(getString(R.string.something_went_wrong))
                        .setContentText(getString(R.string.try_again))
                        .setConfirmText(getString(R.string.dialog_ok))
                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
            }
        }

    }

    private final class DismissClickListener implements SweetAlertDialog.OnSweetClickListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(final SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismiss();
            finish();
        }

    }

}
