package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import utilitaire.Utilitaire;
import annotation.Controller;
import annotation.RequestMapping;
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;



public class FrontControllerServlet extends HttpServlet {

    private List<String> classeController = new ArrayList<>();
    private Map<String, MethodeClass> methodeMap = new HashMap<>();

    private static class MethodeClass{
        Method methode;
        Object instance;

        MethodeClass(Method methode, Object instance) {
            this.methode = methode;
            this.instance = instance;
        }
        public Method getMethode() {
            return methode;
        }
        public Object getInstance() {
            return instance;
        }
        
    }

    protected void processRequest (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getRequestURI();

        resp.setContentType("text/html");
        resp.getWriter().write("<html><body>");
        // resp.getWriter().write("<h1>Request Path: " + path + "</h1>");

        Map.Entry<String, MethodeClass> entree = methodeMap.get(path) != null ? Map.entry(path, methodeMap.get(path)) : null;

        if(entree !=null){
            resp.getWriter().write("<h1>Controller Class: " + entree.getValue().getInstance().getClass().getSimpleName() + "</h1> ");
            resp.getWriter().write("<h2>Method: " + entree.getValue().getMethode().getName() + "</h2>");
            resp.getWriter().write("<h3>Path: " + entree.getKey() + "</h3>");
        }else{
            
            for(Map.Entry<String, MethodeClass> entry : methodeMap.entrySet()) {
    
                // resp.getWriter().write("<h1>Controller Class: " + entry.getValue().getInstance().getClass().getSimpleName() + "</h1> ");
                // resp.getWriter().write("<h2>Method: " + entry.getValue().getMethode().getName() + "</h2>");
                resp.getWriter().write("<h3>Path: " + entry.getKey() + "</h3>");
            }

        }

        resp.getWriter().write("</body></html>");

    }

    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
       processRequest(req, resp);
    }

    // @Override
    // public void init() throws ServletException {
    //     super.init();
    //     String packageName = "controller";
    //     Utilitaire utilitaire = new Utilitaire();
    //     try {
    //         List<Class<?>> classes = utilitaire.getClassByPackage(packageName);
    //         List<Class<?>> classEnumerer = utilitaire.getClassesWithAnnotation(classes, annotation.Controller.class);
            
    //         for (Class<?> clazz : classEnumerer) {
    //             classeController.add(clazz.getName());
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    // }

    @Override
    public void init() throws ServletException {
        super.init();
        String packageName = "controller";
        Utilitaire utilitaire = new Utilitaire();
        try {
            List<Class<?>> classes = utilitaire.getClassByPackage(packageName);
            List<Class<?>> classEnumerer = utilitaire.getClassesWithAnnotation(classes, annotation.Controller.class);
            
            for (Class<?> clazz : classEnumerer) {
                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    Method[] methods = clazz.getDeclaredMethods();
                    for (Method method : methods) {
                        if (method.isAnnotationPresent(RequestMapping.class)) {
                            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                            String path = requestMapping.value();
                            methodeMap.put(path, new MethodeClass(method, instance));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}