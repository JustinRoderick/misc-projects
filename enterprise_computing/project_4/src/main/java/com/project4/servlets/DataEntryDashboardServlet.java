package com.project4.servlets;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/dataentry/dashboard")
public class DataEntryDashboardServlet extends BaseDashboardServlet {
    public DataEntryDashboardServlet() {
        super("dataentry", "/WEB-INF/jsp/dataentry/dashboard.jsp");
    }
}
