package com.xg7plugins.utils;

import com.xg7plugins.boot.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

/**
 * Utility class for file operations.
 */
public class FileUtil {

    /**
     * Creates a file at the specified path within the given parent directory.
     * If the file already exists and 'replace' is true, it will be replaced.
     *
     * @param parent  the parent directory
     * @param path    the relative path of the file to create
     * @param replace whether to replace the file if it already exists
     * @return the created or existing file
     */
    public static File createFile(File parent, String path, boolean replace) {
        File file = new File(parent, path);

        if (!file.exists() || replace) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * Creates a file at the specified path within the plugin's data folder.
     * If the file already exists and 'replace' is true, it will be replaced.
     *
     * @param plugin  the plugin instance
     * @param path    the relative path of the file to create
     * @param replace whether to replace the file if it already exists
     * @return the created or existing file
     */
    public static File createFile(Plugin plugin, String path, boolean replace) {
        return createFile(plugin.getJavaPlugin().getDataFolder(), path, replace);
    }

    /**
     * Creates a file at the specified path within the plugin's data folder.
     * If the file already exists, it will not be replaced.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the file to create
     * @return the created or existing file
     */
    public static File createFile(Plugin plugin, String path) {
        return createFile(plugin.getJavaPlugin().getDataFolder(), path, false);
    }

    /**
     * Creates or saves a resource file from the plugin's jar to the plugin's data folder.
     * If the file already exists and 'replace' is true, it will be replaced.
     *
     * @param plugin  the plugin instance
     * @param path    the relative path of the resource to create or save
     * @param replace whether to replace the file if it already exists
     * @return the created or existing file
     */
    public static File createOrSaveResource(Plugin plugin, String path, boolean replace) {
        File file = new File(plugin.getJavaPlugin().getDataFolder(), path);

        if (!file.exists() || replace) {
            try {
                plugin.getJavaPlugin().saveResource(path, replace);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * Creates or saves a resource file from the plugin's jar to the plugin's data folder.
     * If the file already exists, it will not be replaced.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the resource to create or save
     * @return the created or existing file
     */
    public static File createOrSaveResource(Plugin plugin, String path) {
        return createOrSaveResource(plugin, path, false);
    }

    /**
     * Lists the names of files in the specified directory within the given parent directory.
     *
     * @param parent the parent directory
     * @param path   the relative path of the directory to list files from
     * @return a list of file names in the specified directory
     */
    public static List<String> listFiles(File parent, String path) {
        File folder = new File(parent, path);
        if (!folder.isDirectory()) return Collections.emptyList();
        return Arrays.stream(folder.listFiles())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    /**
     * Lists the names of files in the specified directory within the plugin's data folder.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the directory to list files from
     * @return a list of file names in the specified directory
     */
    public static List<String> listFiles(Plugin plugin, String path) {
        return listFiles(plugin.getJavaPlugin().getDataFolder(), path);
    }

    /**
     * Creates a directory at the specified path within the given parent directory.
     *
     * @param parent the parent directory
     * @param path   the relative path of the directory to create
     */
    public static void createDirectory(File parent, String path) {
        new File(parent, path).mkdirs();
    }

    /**
     * Creates a directory at the specified path within the plugin's data folder.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the directory to create
     */
    public static void createDirectory(Plugin plugin, String path) {
        createDirectory(plugin.getJavaPlugin().getDataFolder(), path);
    }

    /**
     * Reads the content of a file at the specified path within the given parent directory.
     *
     * @param parent the parent directory
     * @param path   the relative path of the file to read
     * @return the content of the file as a string
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static String readFile(File parent, String path) throws IOException {
        return new String(Files.readAllBytes(parent.toPath().resolve(path)));
    }

    /**
     * Reads the content of a file at the specified path within the plugin's data folder.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the file to read
     * @return the content of the file as a string
     * @throws IOException if an I/O error occurs reading from the file
     */
    public static String readFile(Plugin plugin, String path) throws IOException {
        return readFile(plugin.getJavaPlugin().getDataFolder(), path);
    }

    /**
     * Writes content to a file at the specified path within the given parent directory.
     * If the file does not exist, it will be created.
     *
     * @param parent  the parent directory
     * @param path    the relative path of the file to write to
     * @param content the content to write to the file
     * @throws IOException if an I/O error occurs writing to the file
     */
    public static void writeFile(File parent, String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Writes content to a file at the specified path within the plugin's data folder.
     * If the file does not exist, it will be created.
     *
     * @param plugin  the plugin instance
     * @param path    the relative path of the file to write to
     * @param content the content to write to the file
     * @throws IOException if an I/O error occurs writing to the file
     */
    public static void writeFile(Plugin plugin, String path, String content) throws IOException {
        writeFile(plugin.getJavaPlugin().getDataFolder(), path, content);
    }

    /**
     * Deletes a file at the specified path within the given parent directory.
     *
     * @param parent the parent directory
     * @param path   the relative path of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    public static boolean deleteFile(File parent, String path) {
        return new File(parent, path).delete();
    }

    /**
     * Deletes a file at the specified path within the plugin's data folder.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the file to delete
     * @return true if the file was deleted, false otherwise
     */
    public static boolean deleteFile(Plugin plugin, String path) {
        return deleteFile(plugin.getJavaPlugin().getDataFolder(), path);
    }

    /**
     * Saves a resource file from the plugin's jar to the plugin's data folder.
     *
     * @param plugin       the plugin instance
     * @param resourcePath the relative path of the resource to save
     * @param replace      whether to replace the file if it already exists
     * @throws IOException if an I/O error occurs during saving
     */
    public static void saveResource(Plugin plugin, String resourcePath, boolean replace) throws IOException {
        plugin.getJavaPlugin().saveResource(resourcePath, replace);
    }

    /**
     * Saves a resource file from the plugin's jar to the plugin's data folder.
     * If the file already exists, it will be replaced.
     *
     * @param plugin       the plugin instance
     * @param resourcePath the relative path of the resource to save
     * @throws IOException if an I/O error occurs during saving
     */
    public static void saveResource(Plugin plugin, String resourcePath) throws IOException {
        plugin.getJavaPlugin().saveResource(resourcePath, true);
    }

    /**
     * Moves a file from the source to the destination.
     *
     * @param source      the source file
     * @param destination the destination file
     * @throws IOException if an I/O error occurs during moving
     */
    public static void moveFile(File source, File destination) throws IOException {
        Files.move(source.toPath(), destination.toPath());
    }

    /**
     * Renames a file within the specified parent directory.
     *
     * @param parent  the parent directory
     * @param oldPath the current relative path of the file
     * @param newPath the new relative path of the file
     */
    public static void renameFile(File parent, String oldPath, String newPath) {
        File oldFile = new File(parent, oldPath);
        File newFile = new File(parent, newPath);
        oldFile.renameTo(newFile);
    }

    /**
     * Renames a file within the plugin's data folder.
     *
     * @param plugin  the plugin instance
     * @param oldPath the current relative path of the file
     * @param newPath the new relative path of the file
     */
    public static void renameFile(Plugin plugin, String oldPath, String newPath) {
        renameFile(plugin.getJavaPlugin().getDataFolder(), oldPath, newPath);
    }

    /**
     * Renames a file to a new name within the same directory.
     *
     * @param file    the file to rename
     * @param newName the new name for the file
     */
    public static void renameFile(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        file.renameTo(newFile);
    }

    /**
     * Retrieves an InputStream for a resource file within the plugin's jar.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the resource
     * @return an InputStream for the resource, or null if not found
     */
    public static InputStream fromResource(Plugin plugin, String path) {
        return plugin.getJavaPlugin().getResource(path);
    }

    /**
     * Creates a Reader from an InputStream using UTF-8 encoding.
     *
     * @param inputStream the InputStream to read from
     * @return a Reader for the InputStream
     * @throws IOException if an I/O error occurs
     */
    public static Reader reader(InputStream inputStream) throws IOException {
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

    /**
     * Creates a Reader from a File using UTF-8 encoding.
     *
     * @param file the File to read from
     * @return a Reader for the File
     * @throws IOException if an I/O error occurs
     */
    public static Reader reader(File file) throws IOException {
        return new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8);
    }

    /**
     * Finds and loads a class from a JAR file that is a subclass of the specified class.
     *
     * @param file  the JAR file to search
     * @param clazz the superclass or interface the target class should extend or implement
     * @param <T>   the type of the superclass or interface
     * @return the found class, or null if not found
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if a class cannot be found
     */
    public static <T> Class<? extends T> findClass(@NotNull final File file, @NotNull final Class<T> clazz) throws IOException, ClassNotFoundException {
        if (!file.exists()) {
            return null;
        }

        final URL jar = file.toURI().toURL();
        final URLClassLoader loader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
        final List<String> matches = new ArrayList<>();
        final List<Class<? extends T>> classes = new ArrayList<>();

        try (final JarInputStream stream = new JarInputStream(jar.openStream())) {
            JarEntry entry;
            while ((entry = stream.getNextJarEntry()) != null) {
                final String name = entry.getName();
                if (!name.endsWith(".class")) {
                    continue;
                }

                matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
            }

            for (final String match : matches) {
                try {
                    final Class<?> loaded = loader.loadClass(match);
                    if (clazz.isAssignableFrom(loaded)) {
                        return loaded.asSubclass(clazz);
                    }
                } catch (final NoClassDefFoundError ignored) {
                }
            }
        }
        loader.close();
        return null;
    }

    /**
     * Checks if a file exists at the specified path within the plugin's data folder.
     *
     * @param plugin the plugin instance
     * @param path   the relative path of the file to check
     * @return true if the file exists, false otherwise
     */
    public static boolean exists(Plugin plugin, String path) {
        File file = new File(plugin.getJavaPlugin().getDataFolder(), path);
        return file.exists();
    }




}
