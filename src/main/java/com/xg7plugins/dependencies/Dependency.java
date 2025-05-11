package com.xg7plugins.dependencies;

import com.xg7plugins.XG7Plugins;
import com.xg7plugins.utils.Debug;
import com.xg7plugins.utils.Pair;
import com.xg7plugins.utils.http.HTTPMethod;
import com.xg7plugins.utils.http.HTTP;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Collections;

@Data
@Getter
@RequiredArgsConstructor
public class Dependency {

    private final String name;
    private final String downloadLink;


    public static Dependency of(String name, String downloadLink) {
        return new Dependency(name,downloadLink);
    }

    public void downloadDependency() {
        Debug debug = Debug.of(XG7Plugins.getInstance());
        try {

            debug.loading("Downloading dependency: " + name);

            File pluginsFolder = new File(XG7Plugins.getInstance().getDataFolder().getParent());
            if (!pluginsFolder.exists()) pluginsFolder.mkdirs();


            File file = new File(pluginsFolder, name + ".jar");
            if (file.exists()) {
                debug.loading("§aDependency '" + name + "' is already installed.");
                return;
            }

            debug.loading("§eDownloading dependency: " + name);


            try (
                    InputStream in = HTTP.makeRequest(
                        downloadLink,
                        HTTPMethod.GET,
                        Collections.singletonList(Pair.of("User-Agent", "Mozilla/5.0")),
                        null
                    ).getInputStream();
                    FileOutputStream out = new FileOutputStream(file)
            ) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            debug.loading("§aDependency '" + name + "' downloaded with success.");

        } catch (Exception e) {
            debug.severe("Error on install dependency " + name);
            throw new RuntimeException();
        }
    }

}
