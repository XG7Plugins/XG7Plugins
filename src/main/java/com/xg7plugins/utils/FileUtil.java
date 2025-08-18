package com.xg7plugins.utils;

import com.xg7plugins.boot.Plugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    public static File createFile(File parent, Plugin plugin, String path, boolean replace) {
        return createFile(plugin.getDataFolder(), path, replace);
    }

    public static File createFile(Plugin plugin, String path, boolean replace) {
        return createFile(plugin.getDataFolder(), path, replace);
    }

    public static File createFile(Plugin plugin, String path) {
        return createFile(plugin.getDataFolder(), path, false);
    }

    public static List<String> listFiles(File parent, String path) {
        File folder = new File(parent, path);
        if (!folder.isDirectory()) return Collections.emptyList();
        return Arrays.stream(folder.listFiles())
                .map(File::getName)
                .collect(Collectors.toList());
    }
    public static List<String> listFiles(Plugin plugin, String path) {
        return listFiles(plugin.getDataFolder(), path);
    }

    public static void createDirectory(File parent, String path) {
        new File(parent, path).mkdirs();
    }

    public static void createDirectory(Plugin plugin, String path) {
        createDirectory(plugin.getDataFolder(), path);
    }

    public static String readFile(File parent, String path) throws IOException {
        return new String(Files.readAllBytes(parent.toPath().resolve(path)));
    }

    public static String readFile(Plugin plugin, String path) throws IOException {
        return readFile(plugin.getDataFolder(), path);
    }

    public static void writeFile(File parent, String path, String content) throws IOException {
        Files.write(Paths.get(path), content.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    public static void writeFile(Plugin plugin, String path, String content) throws IOException {
        writeFile(plugin.getDataFolder(), path, content);
    }

    public static boolean deleteFile(File parent, String path) {
        return new File(parent, path).delete();
    }

    public static boolean deleteFile(Plugin plugin, String path) {
        return deleteFile(plugin.getDataFolder(), path);
    }

    public static void deleteDirectory(File dir) throws IOException {
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                deleteDirectory(file);
            }
        }
        Files.deleteIfExists(dir.toPath());
    }

    public static InputStream fromResource(Plugin plugin, String path) {
        return plugin.getResource(path);
    }

    public static Reader reader(InputStream inputStream) throws IOException {
        return new InputStreamReader(inputStream, StandardCharsets.UTF_8);
    }


}
