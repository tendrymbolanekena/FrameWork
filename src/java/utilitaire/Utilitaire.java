package utilitaire;

import java.util.*;
import java.lang.reflect.*;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import annotation.Controller;
import java.util.Enumeration;
import java.util.List;

public class Utilitaire {
    

    public List<Class<?>> getClassByPackage(String package) throws IOException {
        List<Class<?>> classes = new ArrayList<>();

        String path = package.replace('.', '/');

        if(path == null || path.isEmpty()){
            return Collections.emptyList();
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> ressources = classLoader.getResources(path);

        while(ressources.hasMoreElements()){
            URL resource = ressources.nextElement();
            File directory = new File(resource.getFile());

            if(directory.exists() && directory.isDirectory()){
                File[] files = directory.listFiles();

                for(File file : files){
                    if(file.isFile() && file.getName().endsWith(".class")){
                        String className = package + "." + file.getName().replace(".class", "");
                        try{
                            Class<?> clazz = Class.forName(className);
                            classes.add(clazz);
                        } catch (ClassNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return classes;
    }

    public List<Class<?>> getClassesWithAnnotation(List<Class<?>> classes , Controller annotation) {
        List<Class<?>> annotatedClasses = new ArrayList<>();

        for(Class<?> clazz : classes){
            if(clazz.isAnnotationPresent(annotation)){
                annotatedClasses.add(clazz);
            }
        }
        return annotatedClasses;
    }
}
