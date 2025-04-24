package com.project4.servlets;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/client/dashboard")
public class ClientDashboardServlet extends BaseDashboardServlet {
    public ClientDashboardServlet() {
        super("client", "/WEB-INF/jsp/client/dashboard.jsp");
    }
}
