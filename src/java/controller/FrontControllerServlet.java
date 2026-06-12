package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class FrontControllerServlet extends HttpServlet {

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

}