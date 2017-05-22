package com.nervousfish.nervousfish.events;

import com.nervousfish.nervousfish.data_objects.Contact;

/**
 * Greenrobot's EventBus message event
 *
 * Sent when the a new public key is received
 */
public class ContactReceivedEvent {
    final Contact contact;

    /**
     * Constructs a new PublicKeyReceivedEvent
     * @param contact The key of the new device
     */
    public ContactReceivedEvent(final Contact contact) {
        this.contact = contact;
    }

    public Contact getContact() {
        return contact;
    }
}
