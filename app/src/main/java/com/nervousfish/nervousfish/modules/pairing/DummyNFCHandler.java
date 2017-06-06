package com.nervousfish.nervousfish.modules.pairing;

import android.nfc.NdefMessage;
import android.nfc.NdefRecord;

import com.nervousfish.nervousfish.data_objects.Contact;
import com.nervousfish.nervousfish.data_objects.Profile;
import com.nervousfish.nervousfish.data_objects.SimpleKey;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static android.nfc.NdefRecord.createExternal;

/**
 * An handler for NFC communication without implementation, needed because NFC is unavailable on the emulator
 */
public final class DummyNFCHandler extends APairingHandler implements INfcHandler {

    private static final long serialVersionUID = -6465987636766819498L;
    private IServiceLocator serviceLocator;
    private static final Logger LOGGER = LoggerFactory.getLogger("DummyNFCHandler");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private DummyNFCHandler(final IServiceLocator serviceLocator) {
        super(serviceLocator);
        this.serviceLocator = serviceLocator;
        this.serviceLocator.registerToEventBus(this);
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<DummyNFCHandler> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new DummyNFCHandler(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(Serializable object) throws IOException {
        LOGGER.info("Begin writing object");
        final byte[] bytes;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(new DataWrapper(object));
            oos.flush();
            bytes = bos.toByteArray();
        }
        final Profile myProfile;
        final Contact myProfileAsContact;
        try {
            myProfile = this.serviceLocator.getDatabase().getProfiles().get(0);
            myProfileAsContact = new Contact(myProfile.getName(), new SimpleKey("simplekey", "73890ien"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createExternal (
                        "com.nervousfish", "contact" , bytes)
                });

    }

    @Override
    public void send(byte[] buffer) {
        
    }


}
