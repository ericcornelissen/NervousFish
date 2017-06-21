package com.nervousfish.nervousfish.modules.filesystem;

import com.nervousfish.nervousfish.modules.constants.IConstants;
import com.nervousfish.nervousfish.service_locator.IServiceLocator;
import com.nervousfish.nervousfish.service_locator.ModuleWrapper;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "ClassFanOutComplexity"})
// 1 / 2) Suppressed because the java.io library is very verbose
public final class AndroidFileSystemAdapter implements IFileSystem {
    private static final Logger LOGGER = LoggerFactory.getLogger("AndroidFileSystemAdapter");
    private final IConstants constants;

    /**
     * Prevents construction from outside the class.
     *
     * @param serviceLocator Can be used to get access to other modules
     */
    // We suppress UnusedFormalParameter because the chance is big that a service locator will be used in the future
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private AndroidFileSystemAdapter(final IServiceLocator serviceLocator) {
        assert serviceLocator != null;
        LOGGER.info("Initialized");
        this.constants = serviceLocator.getConstants();
    }

    /**
     * Creates a new instance of itself and wraps it in a {@link ModuleWrapper} so that only an
     * {@link IServiceLocator}
     *
     * @param serviceLocator The new service locator
     * @return A wrapper around a newly created instance of this class
     */
    public static ModuleWrapper<AndroidFileSystemAdapter> newInstance(final IServiceLocator serviceLocator) {
        Validate.notNull(serviceLocator);
        return new ModuleWrapper<>(new AndroidFileSystemAdapter(serviceLocator));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Writer getWriter(final String path) throws FileNotFoundException {
        Validate.notBlank(path);
        final OutputStream outputStream = new FileOutputStream(path);
        final OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream, this.constants.getCharset());
        return new BufferedWriter(outputStreamWriter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reader getReader(final String path) throws FileNotFoundException {
        Validate.notBlank(path);
        final InputStream inputStream = new FileInputStream(path);
        final InputStreamReader outputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        return new BufferedReader(outputStreamReader);
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
