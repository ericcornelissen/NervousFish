package com.nervousfish.nervousfish.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.nervousfish.nervousfish.ConstantKeywords;
import com.nervousfish.nervousfish.R;
import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.IKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.NervousFish;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

/**
 * An {@link Activity} that shows the contacts information and his public keys.
 */
public final class ContactActivity extends AppCompatActivity {

    private static final Logger LOGGER = LoggerFactory.getLogger("ContactActivity");

    private IServiceLocator serviceLocator;
    private Contact contact;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_contact);
        this.serviceLocator = NervousFish.getServiceLocator();

        final Intent intent = this.getIntent();
        this.contact = (Contact) intent.getSerializableExtra(ConstantKeywords.CONTACT);

        ListviewActivityHelper.setName(this, this.contact.getName(), R.id.contact_name);
        ListviewActivityHelper.setKeys(this, this.contact.getKeys(), R.id.list_view_contact);

        final ListView lv = (ListView) this.findViewById(R.id.list_view_contact);

        lv.setOnItemClickListener(new ContactActivity.KeyListClickListener(this, this.contact.getKeys()));

        final ImageButton backButton = (ImageButton) this.findViewById(R.id.back_button_change);
        backButton.setOnClickListener(v -> this.finish());
        LOGGER.info("Activity created");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_FIRST_USER) {
            this.contact = (Contact) data.getSerializableExtra(ConstantKeywords.CONTACT);
            ListviewActivityHelper.setName(this, this.contact.getName(), R.id.contact_name);
        }
    }

    /**
     * Opens when the 3 dots are clicked.
     *
     * @param v - the View element clicked
     */
    public void showPopupMenu(final View v) {
        Validate.notNull(v);
        final PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(new PopupMenuListener());
        final MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.edit_contact_menu, popup.getMenu());
        popup.show();
    }

    /**
     * An {@link AdapterView.OnItemClickListener}
     * which listens to the clicks on keys in  the list view of {@link KeyManagementActivity}
     */
    private static final class KeyListClickListener implements AdapterView.OnItemClickListener {
        private final Activity activity;
        private final List keys;

        /**
         * * A Constructor for {@link AdapterView.OnItemClickListener} where we pass an
         * Alertdialog builder
         *
         * @param activity {@link Activity} where this listener is located
         * @param keys     A {@Link List} with all the keys of a {@link Contact}
         */
        KeyListClickListener(final Activity activity, final List<IKey> keys) {
            Validate.notNull(activity);
            this.activity = activity;
            this.keys = keys;
        }

        @Override
        public void onItemClick(final AdapterView<?> parent, final View view, final int position,
                                final long id) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
            builder.setTitle(((IKey) this.keys.get(position)).getName());
            builder.setMessage(((IKey) this.keys.get(position)).getFormattedKey());
            builder.setPositiveButton("Copy", (dialog, which) -> {
                final Activity activity = this.activity;
                final ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                final ClipData clip = ClipData.newPlainText(MIMETYPE_TEXT_PLAIN, ((IKey) this.keys.get(position)).getKey());
                clipboard.setPrimaryClip(clip);
            });
            builder.show();
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
                        .setTitleText(ContactActivity.this.getString(R.string.are_you_sure))
                        .setContentText(ContactActivity.this.getString(R.string.delete_popup_no_recovery))
                        .setCancelText(ContactActivity.this.getString(R.string.cancel))
                        .setConfirmText(ContactActivity.this.getString(R.string.yes_delete))
                        .setConfirmClickListener(new DeleteContactClickListener())
                        .show();
                return true;
            } else if (menuItem.getItemId() == R.id.edit_contact_menu_iten) {
                final Intent intent = new Intent(ContactActivity.this, ChangeContactActivity.class);
                intent.putExtra(ConstantKeywords.CONTACT, ContactActivity.this.contact);
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
                ContactActivity.this.serviceLocator.getDatabase().deleteContact(ContactActivity.this.contact.getName());
                sweetAlertDialog.setTitleText(ContactActivity.this.getString(R.string.contact_deleted_title))
                        .setContentText(ContactActivity.this.getString(R.string.contact_deleted_description))
                        .setConfirmText(ContactActivity.this.getString(R.string.dialog_ok))
                        .setConfirmClickListener(new DismissClickListener())
                        .showCancelButton(false)
                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            } catch (final IllegalArgumentException e) {
                LOGGER.error("IllegalArgumentException while deleting contact in ContactActivity", e);
                sweetAlertDialog.setTitleText(ContactActivity.this.getString(R.string.contact_doesnt_exist))
                        .setContentText(ContactActivity.this.getString(R.string.contact_already_deleted))
                        .setConfirmText(ContactActivity.this.getString(R.string.dialog_ok))
                        .setConfirmClickListener(new DismissClickListener())
                        .showCancelButton(false)
                        .changeAlertType(SweetAlertDialog.WARNING_TYPE);
            } catch (final IOException e) {
                LOGGER.error("IOException while deleting contact in ContactActivity", e);
                sweetAlertDialog.setTitleText(ContactActivity.this.getString(R.string.something_went_wrong))
                        .setContentText(ContactActivity.this.getString(R.string.try_again))
                        .setConfirmText(ContactActivity.this.getString(R.string.dialog_ok))
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
            ContactActivity.this.finish();
        }
    }
}
