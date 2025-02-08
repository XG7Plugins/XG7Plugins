package com.xg7plugins.extensions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ExtensionClassLoader extends URLClassLoader {
    public ExtensionClassLoader(File jarfile, ClassLoader parent) throws MalformedURLException {
        super(new URL[]{jarfile.toURI().toURL()}, parent);
    }

    public Extension loadExtension(File jarFile) throws Exception {
        try (JarFile jar = new JarFile(jarFile)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName()
                            .replace("/", ".")
                            .replace(".class", "");

                    Class<?> clazz = loadClass(className);

                    if (Extension.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                        return (Extension) clazz.getDeclaredConstructor().newInstance();
                    }
                }
            }
        }
        throw new ExtensionNotFoundException("Nenhuma classe que implementa Extension foi encontrada em " + jarFile.getName());
    }
}
