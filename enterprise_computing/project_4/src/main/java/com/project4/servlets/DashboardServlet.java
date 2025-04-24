package com.project4.servlets;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/root/dashboard")
public class DashboardServlet extends BaseDashboardServlet {
    public DashboardServlet() {
        super("root", "/WEB-INF/jsp/root/dashboard.jsp");
    }
}
