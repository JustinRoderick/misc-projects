package com.project4.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class BaseDashboardServlet extends HttpServlet {
    private final String role;
    private final String jspPath;

    protected BaseDashboardServlet(String role, String jspPath) {
        this.role = role;
        this.jspPath = jspPath;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.err.println(role + "DashboardServlet - doGet called");
        
        // Check if user is authenticated and has correct role
        if (request.getSession() != null && 
            request.getSession().getAttribute("authenticated") != null &&
            (Boolean) request.getSession().getAttribute("authenticated") &&
            role.equals(request.getSession().getAttribute("role"))) {
            System.err.println(role + "DashboardServlet - User authenticated, forwarding to dashboard.jsp");
            request.getRequestDispatcher(jspPath).forward(request, response);
        } else {
            System.err.println(role + "DashboardServlet - User not authenticated, redirecting to index.jsp");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
}
