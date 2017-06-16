package com.nervousfish.nervousfish.modules.filesystem;

import com.nervousfish.nervousfish.modules.IModule;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

/**
 * Defines a module used for interacting with the filesystem that can be used by a service locator.
 */

public interface IFileSystem extends IModule {
    /**
     * Returns a writer that can be used to write to the path specified.
     * @param path The full path, containing the file, to which should be written
     * @return A {@link Writer} that can be used to write to the file
     */
    Writer getWriter(String path) throws IOException;

    /**
     * Returns a reader that can be used to read from the path specified.
     * @param path The full path, containing the file, from which should be read
     * @return A {@link Writer} that can be used to read from the file
     */
    Reader getReader(String path) throws IOException;

    /**
     * Checks whether a given file exists.
     * @param path The file path to check.
     * @return Whether the file exists.
     */
    boolean checkFileExists(String path);

    /**
     * Deletes a file at the given path.
     * @param path The file path to delete.
     * @return If the deletion was succesful.
     */
    boolean deleteFile(String path);
}
