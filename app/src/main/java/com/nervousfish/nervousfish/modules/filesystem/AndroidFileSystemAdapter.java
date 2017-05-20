package com.nervousfish.nervousfish.modules.filesystem;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;


/**
 * An adapter to the default Android file system
 */
// We suppress this warning because this class is by nature tightly coupled to the Java filesystem, which has a lot of separate classes unfortunately
@SuppressWarnings("checkstyle:classdataabstractioncoupling")
public final class AndroidFileSystemAdapter implements IFileSystem {
    private static final long serialVersionUID = 1937542180968231197L;
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidFileSystemAdapter");
    private final IServiceLocator serviceLocator;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    private AndroidFileSystemAdapter(final IServiceLocator serviceLocator) {
        this.serviceLocator = serviceLocator;
        LOGGER.info("Initialized");
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidFileSystemAdapter> newInstance(final IServiceLocator serviceLocator) {
        return new ModuleWrapper<>(new AndroidFileSystemAdapter(serviceLocator));
    }

    /**
     * Serialize the created proxy instead of this instance.
     */
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    /**
     * Ensure that no instance of this class is created because it was present in the stream. A correct
     * stream should only contain instances of the proxy.
     */
    private void readObject(final ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Writer getWriter(final String path) throws IOException {
        final OutputStream outputStream = new FileOutputStream(path);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, java.nio.charset.StandardCharsets.UTF_8);
        return new BufferedWriter(outputStreamWriter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reader getReader(final String path) throws IOException {
        final InputStream inputStream = new FileInputStream(path);
        final InputStreamReader outputStreamReader = new InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8);
        return new BufferedReader(outputStreamReader);
    }

    /**
     * Represents the logical state of this class and copies the data from that class without
     * any consistency checking or defensive copying.
     * Used for the Serialization Proxy Pattern.
     * We suppress here the AccessorClassGeneration warning because the only alternative to this pattern -
     * ordinary serialization - is far more dangerous
     */
    @SuppressWarnings("PMD.AccessorClassGeneration")
    private static final class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 1937542180968231197L;
        private final IServiceLocator serviceLocator;

        SerializationProxy(final AndroidFileSystemAdapter androidFileSystemAdapter) {
            this.serviceLocator = androidFileSystemAdapter.serviceLocator;
        }

        private Object readResolve() {
            return new AndroidFileSystemAdapter(this.serviceLocator);
        }
    }
}
