package com.nervousfish.nervousfish.util;

import com.nervousfish.nervousfish.ConstantKeywords;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a circular list, that is, a List of seemingly infinite maxSize while the maxSize is fixed in fact.
 *
 * @param <E> The type of the elements of the circular list
 */
public final class CircularList<E extends Serializable> extends AbstractList<E> implements Serializable {

    private static final long serialVersionUID = -4861606797222560124L;
    private static final int HASH_SEED = 31;

    private final int maxSize;
    private final Serializable[] data;

    private int head;
    private int lastElementLocation;
    private int currentSize;

    /**
     * Creates a new CircularList.
     *
     * @param maxSize The maxSize of the list
     */
    public CircularList(final int maxSize) {
        super();
        if (maxSize <= 0) {
            throw new IllegalArgumentException("Illegal capacity: " + maxSize);
        }
        this.data = new Serializable[maxSize];
        this.maxSize = maxSize;
    }

    /**
     * Creates a new CircularList for the proxy serialization pattern.
     * This method should not be used outside this class!
     *
     * @param maxSize The maxSize of the list
     */
    private CircularList(final int maxSize, final Serializable[] data, final int head, final int lastElementLocation, final int currentSize) {
        super();
        this.maxSize = maxSize;
        this.data = data;
        this.head = head;
        this.lastElementLocation = lastElementLocation;
        this.currentSize = currentSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        final CircularList<?> that = (CircularList<?>) o;
        return this.maxSize == that.maxSize && Arrays.equals(this.data, that.data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = CircularList.HASH_SEED * result + this.maxSize;
        result = CircularList.HASH_SEED * result + Arrays.hashCode(this.data);
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public E get(final int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Illegal Index: " + index);
        }
        return this.elementData((index + head) % this.maxSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return this.currentSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean add(final E e) {
        this.data[this.lastElementLocation] = e;
        this.lastElementLocation++;
        if (this.lastElementLocation == maxSize) {
            this.lastElementLocation = 0;
        }
        if (this.currentSize == this.maxSize) {
            this.head++;
        }
        this.currentSize = Math.min(this.maxSize, this.currentSize + 1);
        return true;
    }

    /**
     * Get all elements of the {@link CircularList} in a {@link List}.
     *
     * @return All elements in order
     */
    public List<E> getElements() {
        final List<E> array = new ArrayList<>(this.maxSize);
        for (int i = 0; i < this.maxSize; i++) {
            array.add(get(i));
        }
        return array;
    }

    /**
     * Returns the element at position index
     *
     * @param index The position of the element
     * @return The element at position index
     */
    @SuppressWarnings("unchecked")
    private E elementData(final int index) {
        return (E) this.data[index];
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new CircularList.SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A
     * correct stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException(ConstantKeywords.PROXY_REQUIRED);
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     *
     * Used for the Serialization Proxy Pattern.
     *
     * We suppress the AccessorClassGeneration warning because the only alternative to this pattern
     * - ordinary serialization - is far more dangerous.
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = -4861606797222560124L;
        private final int maxSize;
        private final Serializable[] data;
        private final int head;
        private final int lastElementLocation;
        private final int currentSize;

        /**
         * Constructs a new SerializationProxy
         * @param list The current instance of the proxy
         */
        SerializationProxy(final CircularList list) {
            this.maxSize = list.maxSize;
            this.data = list.data;
            this.head = list.head;
            this.lastElementLocation = list.lastElementLocation;
            this.currentSize = list.currentSize;
        }

        /**
         * Not to be called by the user - resolves a new object of this proxy
         * @return The object resolved by this proxy
         */
        private Object readResolve() {
            return new CircularList(this.maxSize, this.data, this.head, this.lastElementLocation, this.currentSize);
        }
    }

}
