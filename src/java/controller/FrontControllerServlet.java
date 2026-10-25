package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession; 
import java.io.IOException;
import java.util.*;
import utilitaire.*;
import java.lang.reflect.*;
import view.ModelAndView;
import annotation.ToJson;
import com.google.gson.Gson;
import java.io.PrintWriter;
import annotation.Param;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import jakarta.servlet.http.Part;
import jakarta.servlet.http.HttpServletRequest;
import util.*;
import jakarta.servlet.annotation.MultipartConfig;

@MultipartConfig
public class FrontControllerServlet extends HttpServlet {

    private static final String SESSION_ATTRIBUTE = "Mysession";

    // private List<String> classeController = new ArrayList<>();
    // private Map<UrlMethode, MethodeClass> methodeMap = new HashMap<>;
    String dossier ;

    public static class MethodeClass{
        Method methode;
        Object instance;
        ToJson toJson;

        public MethodeClass(Method methode, Object instance) {
            this.methode = methode;
            this.instance = instance;
            this.toJson = methode.getAnnotation(ToJson.class);
        }
        public ToJson getToJson() {
            return toJson;
        }
        public Method getMethode() {
            return methode;
        }
        public Object getInstance() {
            return instance;
        }
        
    }
    private MapFile construireMapFile(
            String nomChamp,
            HttpServletRequest req
    ) throws Exception {

        Part part = req.getPart(nomChamp);

        if (part == null || part.getSize() == 0) {
            return null;
        }

        String nomFichier = part.getSubmittedFileName();

        if (nomFichier == null || nomFichier.isBlank()) {
            return null;
        }

        nomFichier = nomFichier.replace('\\', '/');
        nomFichier = nomFichier.substring(
            nomFichier.lastIndexOf('/') + 1
        );

        String extension = "";
        int position = nomFichier.lastIndexOf('.');

        if (position > 0 && position < nomFichier.length() - 1) {
            extension = nomFichier.substring(position + 1);
        }

        byte[] contenu;

        try (var input = part.getInputStream()) {
            contenu = input.readAllBytes();
        }

        return new MapFile(nomFichier, extension, contenu);
    }


    private Object construireObjet(
            Class<?> type,
            String prefixe,
            HttpServletRequest req
    ) throws ReflectiveOperationException {
    
        Object objet = type.getDeclaredConstructor().newInstance();
    
        for (Field champ : type.getDeclaredFields()) {
            if (Modifier.isStatic(champ.getModifiers())
                    || Modifier.isFinal(champ.getModifiers())) {
                continue;
            }

            champ.setAccessible(true);

            String nom = prefixe.isEmpty()
                    ? champ.getName()
                    : prefixe + "." + champ.getName();

            Class<?> t = champ.getType();
            String valeur = req.getParameter(nom);

            if (t == String.class) {
                if (valeur != null) {
                    champ.set(objet, valeur);
                }

            } else if (t == int.class || t == Integer.class) {
                if (valeur != null && !valeur.isBlank()) {
                    champ.set(objet, Integer.parseInt(valeur));
                }

            } else if (t == double.class || t == Double.class) {
                if (valeur != null && !valeur.isBlank()) {
                    champ.set(objet, Double.parseDouble(valeur));
                }

            } else if (t == long.class || t == Long.class) {
                if (valeur != null && !valeur.isBlank()) {
                    champ.set(objet, Long.parseLong(valeur));
                }

            } else if (t == boolean.class || t == Boolean.class) {
                if (valeur != null) {
                    champ.set(objet, Boolean.parseBoolean(valeur));
                }

            } else if (t == MapFile.class) {
                    
                try {
                    MapFile fichier = construireMapFile(nom, req);

                    if (fichier != null) {
                        String dossier = getServletContext()
                                .getInitParameter("framework.upload.dir");

                        if (dossier == null || dossier.isBlank()) {
                            throw new IllegalStateException(
                                "framework.upload.dir n'est pas configuré"
                            );
                        }

                        FileUtil.copierFichier(fichier, dossier);
                        champ.set(objet, fichier);
                    }

                } catch (Exception e) {
                    throw new RuntimeException(
                        "Erreur lors du traitement du fichier : " + nom, e
                    );
                }
                finally {
                    continue;
                }

            } else if (!t.isPrimitive()
                    && !t.getName().startsWith("java.")
                    && !t.isArray()) {
                    
                
                Object sousObjet = construireObjet(t, nom, req);
                champ.set(objet, sousObjet);
            }
        }
    
        return objet;
    }


    protected void processRequest (HttpServletRequest req , HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String uri = req.getRequestURI();

        if (uri.endsWith("/FrameWork.js")) {
            resp.setContentType("application/javascript");
            resp.setCharacterEncoding("UTF-8");

            try (InputStream in = getClass()
                    .getClassLoader()
                    .getResourceAsStream("FrameWork.js")) {
                    
                if (in == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                in.transferTo(resp.getOutputStream());
            }

            return;
        }

        if (pathInfo != null && pathInfo.endsWith(".jsp")) {

            req.getServletContext().getNamedDispatcher("jsp").forward(req, resp);
            return; 
        }

        @SuppressWarnings("unchecked")
        Map<UrlMethode, MethodeClass> methodeMap = (Map<UrlMethode, MethodeClass>) getServletContext().getAttribute("methodeMap");

        UrlMethode path = new UrlMethode(req.getPathInfo(), req.getMethod());

        // resp.setContentType("text/html");
        // resp.getWriter().write("<html><body>");
        // resp.getWriter().write("<h1>Request Path11111: " + path.getPath() + path.getMethode() + "</h1>");
       

        Map.Entry<UrlMethode, MethodeClass> entree = methodeMap.get(path) != null ? Map.entry(path, methodeMap.get(path)) : null;
        // resp.getWriter().write("<h1>Matching Entry: " + (entree != null ? entree.getKey().getPath() + entree.getKey().getMethode() : "No matching entry") + "</h1>");

        boolean S = false;
        if(entree !=null){
            // resp.getWriter().write("<h1>Controller Class: " + entree.getValue().getInstance().getClass().getSimpleName() + "</h1> ");
            // resp.getWriter().write("<h2>Method: " + entree.getValue().getMethode().getName() + "</h2>");
            entree.getValue().getMethode().setAccessible(true);
            try {
                Class<?>[] parameterTypes = entree.getValue().getMethode().getParameterTypes();
                Object[] args = new Object[parameterTypes.length];

                Parameter[] parameters = entree.getValue().getMethode().getParameters();

                String nomObjet = req.getParameter("_fw_object");

                for (int i = 0; i < parameters.length; i++) {
                
                    Parameter parameter = parameters[i];
                    Class<?> type = parameter.getType();

                    Param param = parameter.getAnnotation(Param.class);

                    if (param != null) {
                    
                        String valeur = req.getParameter(param.value());

                        if (type == String.class) {
                            args[i] = valeur;

                        } else if (type == int.class || type == Integer.class) {
                            args[i] = (valeur != null && !valeur.isBlank())
                                    ? Integer.parseInt(valeur) : 0;

                        } else if (type == double.class || type == Double.class) {
                            args[i] = (valeur != null && !valeur.isBlank())
                                    ? Double.parseDouble(valeur) : 0.0;

                        } else if (type == boolean.class || type == Boolean.class) {
                            args[i] = (valeur != null)
                                    ? Boolean.parseBoolean(valeur) : false;
                        }


                    } else if (nomObjet != null
                            && nomObjet.equals(type.getSimpleName())) {
                            
                        args[i] = construireObjet(type, "", req);
                    }else if(type == Mysession.class){
                        args[i] = getMysession(req);
                        S = true;
                    }
                }
                
                // if (parameterTypes.length > 0 && parameterTypes[0] == ModelAndView.class) {
                //     args[0] = new ModelAndView();
                // }

                Object resultat = entree.getValue().getMethode()
                        .invoke(entree.getValue().getInstance(), args);
                
                if(S){
                    updateSession(req);
                }

                if (entree.getValue().getToJson() != null) {
                    resp.setContentType("application/json;charset=UTF-8");
                
                    PrintWriter out = resp.getWriter();
                    if (entree.getValue().getToJson().prettyPrint()) {
                        out.print(resultat);
                    } else {
                        Gson objectMapper = new Gson();
                        out.print(objectMapper.toJson(resultat));
                    }
                
                    out.flush();
                    return;
                }

                if (resultat instanceof ModelAndView) {
                
                    ModelAndView modelAndView = (ModelAndView) resultat;

                    for (Map.Entry<String, Object> entry :
                            modelAndView.getModel().entrySet()) {
                            
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }

                    String prefix = "/WEB-INF/jsp/";
                    String suffix = ".jsp";

                    req.getRequestDispatcher(
                            prefix + modelAndView.getViewName() + suffix
                    ).forward(req, resp);
                }
            } catch (ReflectiveOperationException e) {
                e.printStackTrace();

                if (!resp.isCommitted()) {
                    resp.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Erreur lors de la construction de l'objet."
                    );
                }
                return;
            }
        }else{
            resp.getWriter().write("<h1>No matching controller method found for path: " + path + "</h1>");
            for(Map.Entry<UrlMethode, MethodeClass> entry : methodeMap.entrySet()) {
    
                // resp.getWriter().write("<h1>Controller Class: " + entry.getValue().getInstance().getClass().getSimpleName() + "</h1> ");
                // resp.getWriter().write("<h2>Method: " + entry.getValue().getMethode().getName() + "</h2>");
                resp.getWriter().write("<h2>Method: " + entry.getKey().getMethode() + "</h2>");
                resp.getWriter().write("<h3>Path: " + entry.getKey().getPath() + "</h3>");
            }

        }

        resp.getWriter().write("</body></html>");

    }



    @Override
    public void init() throws ServletException {
        super.init();

        dossier = getServletContext()
                .getInitParameter("framework.upload.dir");

        if (dossier == null || dossier.isBlank()) {
            throw new ServletException(
                "Le paramètre framework.upload.dir n'est pas configuré"
            );
        }
    }

    protected Mysession getMysession(HttpServletRequest hsr){
        HttpSession Hsession = hsr.getSession(true);

        synchronized (Hsession){
            Object value = Hsession.getAttribute(SESSION_ATTRIBUTE);

            if(value instanceof Mysession){
                return (Mysession)value;
            }

            Mysession session = new Mysession();

            Hsession.setAttribute(SESSION_ATTRIBUTE, session);

            return session;
        }
    }

    protected void updateSession(HttpServletRequest req){
        HttpSession Hsession = req.getSession(false);

        if(Hsession == null || Hsession.getAttribute(SESSION_ATTRIBUTE) == null ){
            return;
        }
        
        Object val = Hsession.getAttribute(SESSION_ATTRIBUTE);

        if(val instanceof Mysession){
            Hsession.setAttribute(SESSION_ATTRIBUTE, val);
        }
    }

    protected void doGet(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void doPost(HttpServletRequest req,HttpServletResponse resp) throws ServletException, IOException {
       processRequest(req, resp);
    }
    
}