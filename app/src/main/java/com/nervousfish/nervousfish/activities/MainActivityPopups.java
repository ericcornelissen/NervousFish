package com.nervousfish.nervousfish.activities;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.provider.Settings;

import com.nervousfish.nervousfish.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Is used to show popups in the MainActivity.
 */

final class MainActivityPopups {

    private final MainActivity mainActivity;

    /**
     * Create and initialize the class.
     *
     * @param mainActivity The MainActivity where sorting is needed
     */
    MainActivityPopups(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }


    /**
     * Shows a popup that adding a contact went fine if the boolean
     * added in the intent is true.
     *
     * @param successfulBluetooth The intents value for {@code SUCCESSFUL_BLUETOOTH}.
     */
    void showSuccessfulBluetoothPopup(final Object successfulBluetooth) {
        if (successfulBluetooth != null) {
            final boolean success = (boolean) successfulBluetooth;
            if (success) {
                new SweetAlertDialog(this.mainActivity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText(this.mainActivity.getString(R.string.contact_added_popup_title))
                        .setContentText(this.mainActivity.getString(R.string.contact_added_popup_explanation))
                        .setConfirmText(this.mainActivity.getString(R.string.dialog_ok))
                        .show();
            }
        }
    }

    /**
     * Shows a popup which asks if the user is sure that he wants to
     * log out.
     */
    void showAreYouSureToLogOut() {
        new SweetAlertDialog(this.mainActivity, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(this.mainActivity.getString(R.string.popup_log_out_title))
                .setContentText(this.mainActivity.getString(R.string.popup_log_out_description))
                .setCancelText(this.mainActivity.getString(R.string.no))
                .setConfirmText(this.mainActivity.getString(R.string.yes))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sDialog) {
                        final Intent intent = new Intent(mainActivity, LoginActivity.class);
                        mainActivity.startActivity(intent);
                    }
                })
                .show();
    }

    /**
     * Shows a popup which asks to enable Bluetooth.
     *
     * @param buttonClicked If the Bluetooth button was clicked
     */
    void showEnableBluetoothPopup(final boolean buttonClicked) {
        final String description = mainActivity.getString(
                buttonClicked
                        ? R.string.popup_enable_bluetooth_exchange : R.string.popup_enable_bluetooth_findable);

        new SweetAlertDialog(this.mainActivity, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(this.mainActivity.getString(R.string.popup_enable_bluetooth_title))
                .setContentText(description)
                .setCancelText(this.mainActivity.getString(R.string.no))
                .setConfirmText(this.mainActivity.getString(R.string.yes))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        MainActivity.LOGGER.info("Requesting to enable Bluetooth");
                        final Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        if (buttonClicked) {
                            mainActivity.startActivityForResult(intent, MainActivity.ENABLE_BLUETOOTH_ON_BUTTON_CLICK);
                        } else {
                            mainActivity.startActivityForResult(intent, MainActivity.ENABLE_BLUETOOTH_ON_START);
                        }
                        MainActivity.LOGGER.info("Request to enable Bluetooth sent");
                    }
                })
                .show();
    }

    /**
     * Shows a popup which asks to enable NFC.
     */
    void showEnableNFCPopup() {
        MainActivity.LOGGER.info("Requesting to enable NFC");
        new SweetAlertDialog(this.mainActivity, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText(this.mainActivity.getString(R.string.popup_enable_nfc_title))
                .setContentText(this.mainActivity.getString(R.string.popup_enable_nfc_settings))
                .setCancelText(this.mainActivity.getString(R.string.no))
                .setConfirmText(this.mainActivity.getString(R.string.yes))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(final SweetAlertDialog dialog) {
                        dialog.dismiss();
                        mainActivity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                        MainActivity.LOGGER.info("Request to enable NFC sent, forwarded to settings");
                    }
                })
                .show();
    }

    /**
     * Shows a popup which tells that something went wrong.
     */
    void showSomethingWentWrong() {
        new SweetAlertDialog(this.mainActivity, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(this.mainActivity.getString(R.string.something_went_wrong))
                .setContentText(this.mainActivity.getString(R.string.something_went_wrong_QR_popup_explanation))
                .setConfirmText(this.mainActivity.getString(R.string.dialog_ok))
                .show();
    }


}
