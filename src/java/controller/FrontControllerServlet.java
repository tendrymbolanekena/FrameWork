package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import utilitaire.Utilitaire;
import annotation.Controller;


public class FrontControllerServlet extends HttpServlet {

    private List<String> classeController;

    protected void processRequest (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getRequestURI();

        resp.setContentType("text/html");
    
        resp.getWriter().write("<html><body><p>lien: " + path + "</p></body></html>");
    }

    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
       processRequest(req, resp);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        String packageName = "controller";
        Utilitaire utilitaire = new Utilitaire();
        try {
            List<Class<?>> classes = utilitaire.getClassByPackage(packageName);
            List<Class<?>> classEnumerer = utilitaire.getClassesWithAnnotation(classes, annotation.Controller.class);
            
            for (Class<?> clazz : classEnumerer) {
                classeController.add(clazz.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}