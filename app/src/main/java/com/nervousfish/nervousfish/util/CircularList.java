package com.nervousfish.nervousfish.util;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a circular list, that is, a List of seemingly infinite maxSize while the maxSize is fixed in fact.
 *
 * @param <E> The type of the elements of the circular list
 */
public final class CircularList<E> extends AbstractList<E> implements List<E>, Serializable {
    private final int maxSize;
    private final Object[] data;
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
            throw new IllegalArgumentException("Illegal Capacity: " + maxSize);
        }
        this.data = new Object[maxSize];
        this.maxSize = maxSize;
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
        if (currentSize == maxSize) {
            head++;
        }
        currentSize = Math.min(maxSize, currentSize + 1);
        return true;
    }

    /**
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
}
