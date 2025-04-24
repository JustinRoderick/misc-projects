package com.project4.filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/root/*", "/client/*", "/dataentry/*", "/accountant/*"})
public class AuthenticationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.err.println("AuthenticationFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        System.err.println("Debug - RequestURI: " + httpRequest.getRequestURI());
        System.err.println("Debug - Session: " + (session != null ? "exists" : "null"));
        if (session != null) {
            System.err.println("Debug - Session ID: " + session.getId());
            System.err.println("Debug - Authenticated: " + session.getAttribute("authenticated"));
            System.err.println("Debug - Role: " + session.getAttribute("role"));
            System.err.println("Debug - Username: " + session.getAttribute("username"));
        }

        // If accessing the login page or authentication endpoint, allow through
        if (httpRequest.getRequestURI().endsWith("/index.jsp") || 
            httpRequest.getRequestURI().endsWith("/authenticate")) {
            System.err.println("Debug - Allowing access to public resource");
            chain.doFilter(request, response);
            return;
        }

        boolean isAuthenticated = session != null && session.getAttribute("authenticated") != null &&
                                (Boolean) session.getAttribute("authenticated");

        System.err.println("Debug - isAuthenticated: " + isAuthenticated);

        if (isAuthenticated) {
            String role = (String) session.getAttribute("role");
            String requestURI = httpRequest.getRequestURI();
            
            // Check if user has access to the requested area
            boolean hasAccess = (requestURI.startsWith(httpRequest.getContextPath() + "/root/") && "root".equals(role)) ||
                              (requestURI.startsWith(httpRequest.getContextPath() + "/client/") && "client".equals(role)) ||
                              (requestURI.startsWith(httpRequest.getContextPath() + "/dataentry/") && "dataentry".equals(role)) ||
                              (requestURI.startsWith(httpRequest.getContextPath() + "/accountant/") && "theaccountant".equals(role));

            System.err.println("Debug - Role: " + role);
            System.err.println("Debug - RequestURI: " + requestURI);
            System.err.println("Debug - Has Access: " + hasAccess);

            if (hasAccess) {
                System.err.println("Debug - Access granted to " + role + " for " + requestURI);
                chain.doFilter(request, response);
            } else {
                System.err.println("Debug - Access denied to " + role + " for " + requestURI);
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } else {
            System.err.println("Debug - Redirecting to login page - not authenticated");
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
        }
    }

    @Override
    public void destroy() {
        System.err.println("AuthenticationFilter destroyed");
    }
}
