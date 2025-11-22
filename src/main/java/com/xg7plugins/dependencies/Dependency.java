package com.xg7plugins.dependencies;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.http.HTTP;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Represents a plugin dependency that can be downloaded and installed.
 * This class handles the management of external plugin dependencies,
 * including downloading and installation in the plugins' folder.
 */
@Data
@Getter
@RequiredArgsConstructor
public class Dependency {

    private final String name;
    private final String downloadLink;
    private final boolean required;


    /**
     * Creates a new Dependency instance.
     *
     * @param name         The name of the dependency
     * @param downloadLink The URL from where the dependency can be downloaded
     * @return A new Dependency instance
     */
    public static Dependency of(String name, String downloadLink, boolean required) {
        return new Dependency(name,downloadLink, required);
    }

    /**
     * Downloads and installs the dependency into the plugins' folder.
     * If the dependency is already installed, it will skip the download.
     * Downloads the jar file using HTTP GET request and saves it to the plugins directory.
     *
     * @throws RuntimeException if there's an error during download or installation
     */
    public void downloadDependency() {
        Debug debug = Debug.of(XG7Plugins.getInstance());
        try {

            debug.info("dependencies", "Downloading dependency: " + name);

            File pluginsFolder = new File(XG7Plugins.getInstance().getJavaPlugin().getDataFolder().getParent());

            File file = new File(pluginsFolder, name + ".jar");
            if (file.exists()) {
                debug.info("dependencies", "§aDependency '" + name + "' is already installed.");
                return;
            }

            try {
                InputStream in = HTTP.get(downloadLink).getInputStream();
                FileOutputStream out = new FileOutputStream(file);

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                debug.severe("Error on downloading dependency " + name);
                throw new RuntimeException();
            }

            debug.info("dependencies", "&aDependency '" + name + "' downloaded with success.");

        } catch (Exception e) {
            debug.severe("Error on install dependency " + name);
            throw new RuntimeException();
        }
    }

}
