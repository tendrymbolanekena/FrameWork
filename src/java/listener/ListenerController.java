package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import utilitaire.*;
import annotation.Controller;
import annotation.UrlMapping;
import controller.FrontControllerServlet.MethodeClass;

@WebListener
public class ListenerController implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String packageName = "controller";
        Utilitaire utilitaire = new Utilitaire();
        Map<UrlMethode, MethodeClass> methodeMap = new HashMap<>();

        try {
            List<Class<?>> classEnumerer = utilitaire.getClassByPackage(packageName);
            utilitaire.getClassesWithAnnotation(classEnumerer, Controller.class);
            
            for (Class<?> clazz : classEnumerer) {
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(UrlMapping.class)) {
                            UrlMapping urlMapping = method.getAnnotation(UrlMapping.class);
                            UrlMethode path = new UrlMethode(urlMapping.path(), urlMapping.methode());
                            
                            if (methodeMap.containsKey(path)) {
                                throw new RuntimeException("Duplicate mapping for path: " + urlMapping.path() + " and method: " + urlMapping.methode() + " vv " + method.getName());
                            } else {
                                methodeMap.put(path, new MethodeClass(method, instance));
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
            sce.getServletContext().setAttribute("methodeMap", methodeMap);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}