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

public class FileUtil {


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

    public static File createFile(Plugin plugin, String path, boolean replace) {
        return createFile(plugin.getJavaPlugin().getDataFolder(), path, replace);
    }

    public static File createFile(Plugin plugin, String path) {
        return createFile(plugin.getJavaPlugin().getDataFolder(), path, false);
    }

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

    public static File createOrSaveResource(Plugin plugin, String path) {
        return createOrSaveResource(plugin, path, false);
    }

    public static List<String> listFiles(File parent, String path) {
        File folder = new File(parent, path);
        if (!folder.isDirectory()) return Collections.emptyList();
        return Arrays.stream(folder.listFiles())
                .map(File::getName)
                .collect(Collectors.toList());
    }
    public static List<String> listFiles(Plugin plugin, String path) {
        return listFiles(plugin.getJavaPlugin().getDataFolder(), path);
    }

    public static void createDirectory(File parent, String path) {
        new File(parent, path).mkdirs();
    }

    public static void createDirectory(Plugin plugin, String path) {
        createDirectory(plugin.getJavaPlugin().getDataFolder(), path);
    }

    public static String readFile(File parent, String path) throws IOException {
        return new String(Files.readAllBytes(parent.toPath().resolve(path)));
    }

    public static String readFile(Plugin plugin, String path) throws IOException {
        return readFile(plugin.getJavaPlugin().getDataFolder(), path);
    }

    public static void writeFile(File parent, String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void writeFile(Plugin plugin, String path, String content) throws IOException {
        writeFile(plugin.getJavaPlugin().getDataFolder(), path, content);
    }

    public static boolean deleteFile(File parent, String path) {
        return new File(parent, path).delete();
    }

    public static boolean deleteFile(Plugin plugin, String path) {
        return deleteFile(plugin.getJavaPlugin().getDataFolder(), path);
    }
    public static void saveResource(Plugin plugin, String resourcePath, boolean replace) throws IOException {
        plugin.getJavaPlugin().saveResource(resourcePath, replace);
    }
    public static void saveResource(Plugin plugin, String resourcePath) throws IOException {
        plugin.getJavaPlugin().saveResource(resourcePath, true);
    }

    public static void moveFile(File source, File destination) throws IOException {
        Files.move(source.toPath(), destination.toPath());
    }

    public static void renameFile(File parent, String oldPath, String newPath) {
        File oldFile = new File(parent, oldPath);
        File newFile = new File(parent, newPath);
        oldFile.renameTo(newFile);
    }

    public static void renameFile(Plugin plugin, String oldPath, String newPath) {
        renameFile(plugin.getJavaPlugin().getDataFolder(), oldPath, newPath);
    }
    public static void renameFile(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        file.renameTo(newFile);
    }

    public static InputStream fromResource(Plugin plugin, String path) {
        return plugin.getJavaPlugin().getResource(path);
    }

    public static Reader reader(InputStream inputStream) throws IOException {
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }

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


}
