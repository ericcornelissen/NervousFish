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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * An adapter to the default Android file system
 */
public final class AndroidFileSystemAdapter implements IFileSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidFileSystemAdapter");

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    @SuppressWarnings("PMD.UnusedFormalParameter")
    // This servicelocator will be used later on probably
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
    public Writer getWriter(final String path) throws IOException {
        final OutputStream outputStream = new FileOutputStream(path);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, UTF_8);
        return new BufferedWriter(outputStreamWriter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reader getReader(final String path) throws IOException {
        final InputStream inputStream = new FileInputStream(path);
        final InputStreamReader outputStreamReader = new InputStreamReader(inputStream, UTF_8);
        return new BufferedReader(outputStreamReader);
    }
}
