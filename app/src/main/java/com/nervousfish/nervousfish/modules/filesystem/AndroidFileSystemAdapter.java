package com.nervousfish.nervousfish.modules.filesystem;

import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * An adapter to the default Android file system. Suppresses ClassDataAbstractionCoupling because
 * it's just not possible to work without all the imports, they are small but necessairy for safe file
 * writing and reading.
 */
@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public final class AndroidFileSystemAdapter implements IFileSystem {

    private static final long serialVersionUID = 1937542180968231197L;
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidFileSystemAdapter");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    // We suppress UnusedFormalParameter because the chance is big that a service locator will be used in the future
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private AndroidFileSystemAdapter(final IServiceLocator serviceLocator) {
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
     * {@inheritDoc}
     */
    @Override
    public Writer getWriter(final String path) throws FileNotFoundException {
        final OutputStream outputStream = new FileOutputStream(path);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        return new BufferedWriter(outputStreamWriter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reader getReader(final String path) throws FileNotFoundException {
        final InputStream inputStream = new FileInputStream(path);
        final InputStreamReader outputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(outputStreamReader);
    }

    /**
     * Deserialize the instance using readObject to ensure invariants and security.
     *
     * @param stream The serialized object to be deserialized
     */
    private void readObject(final ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.ensureClassInvariant();
    }

    /**
     * Used to improve performance / efficiency
     *
     * @param stream The stream to which this object should be serialized to
     */
    private void writeObject(final ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
    }

    /**
     * Ensure that the instance meets its class invariant
     *
     * @throws InvalidObjectException Thrown when the state of the class is unstbale
     */
    private void ensureClassInvariant() throws InvalidObjectException {
        // No checks to perform
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean checkFileExists(final String path) {
        final File file = new File(path);
        return file.exists();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deleteFile(final String path) {
        final File file = new File(path);
        return file.delete();
    }


}
