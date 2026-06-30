package utilitaire;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Utilitaire {

    public List<Class<?>> getClassByPackage(String packageName) throws IOException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageName.replace('.', '/');
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();

            if ("file".equals(protocol)) {
                try {
                    String filePath = URLDecoder.decode(resource.getFile(), "UTF-8");
                    File directory = new File(filePath);
                    if (directory.exists() && directory.isDirectory()) {
                        File[] files = directory.listFiles();
                        if (files != null) {
                            for (File file : files) {
                                if (file.isFile() && file.getName().endsWith(".class")) {
                                    String className = packageName + "." + file.getName().replace(".class", "");
                                    classes.add(Class.forName(className));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if ("jar".equals(protocol)) {
                try {
                    String jarPath = resource.getPath().substring(5, resource.getPath().indexOf("!"));
                    jarPath = URLDecoder.decode(jarPath, "UTF-8");
                    try (JarFile jar = new JarFile(jarPath)) {
                        Enumeration<JarEntry> entries = jar.entries();
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            if (name.startsWith(path) && name.endsWith(".class") && !entry.isDirectory()) {
                                String className = name.replace('/', '.').substring(0, name.length() - 6);
                                classes.add(Class.forName(className));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return classes;
    }

    public void getClassesWithAnnotation(List<Class<?>> classes, Class<? extends java.lang.annotation.Annotation> annotation) { 
        // List<Class<?>> annotatedClasses = new ArrayList<>();
        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(annotation)) {
                classes.remove(clazz);
            }
        }
        // return annotatedClasses;
    }
}