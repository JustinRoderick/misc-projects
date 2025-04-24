/* Name: Justin Roderick
Course: CNT 4714 – Spring 2025 – Project Four
Assignment title: A Three-Tier Distributed Web-Based Application
Date: April 23, 2025
*/

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

        // If accessing the login page or authentication endpoint, allow through
        String requestURI = httpRequest.getRequestURI();
        if (requestURI.endsWith("/index.jsp") || 
            requestURI.endsWith("/authenticate") ||
            requestURI.endsWith("/logout") ||
            requestURI.contains("/css/") ||
            requestURI.contains("/js/")) {
            chain.doFilter(request, response);
            return;
        }

        boolean isAuthenticated = session != null && session.getAttribute("authenticated") != null &&
                                (Boolean) session.getAttribute("authenticated");

        if (isAuthenticated) {
            String role = (String) session.getAttribute("role");
            String contextPath = httpRequest.getContextPath();
            
            // Check if user has access to the requested area
            boolean hasAccess = switch (role) {
                case "root" -> requestURI.startsWith(contextPath + "/root/");
                case "client" -> requestURI.startsWith(contextPath + "/client/");
                case "dataentry" -> requestURI.startsWith(contextPath + "/dataentry/");
                case "theaccountant" -> requestURI.startsWith(contextPath + "/accountant/");
                default -> false;
            };

            if (hasAccess) {
                chain.doFilter(request, response);
            } else {
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied for role: " + role);
            }
        } else {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/index.jsp");
        }
    }

    @Override
    public void destroy() {
        System.err.println("AuthenticationFilter destroyed");
    }
}
