package com.project4.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import org.json.JSONObject;
import org.json.JSONArray;
import com.project4.utils.DatabaseConnection;

@WebServlet(urlPatterns = {"/accountant/*"})
public class AccountantServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        // Handle dashboard view
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            request.getRequestDispatcher("/WEB-INF/jsp/accountant/dashboard.jsp").forward(request, response);
            return;
        }
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        // Handle report execution
        if ("/execute".equals(pathInfo)) {
            executeReport(request, response);
            return;
        }
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
    
    private void executeReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String reportType = request.getParameter("report");
        JSONObject result = new JSONObject();
        
        try (Connection conn = DatabaseConnection.getAccountantConnection()) {
            String procedureName = switch (reportType) {
                case "supplier_shipments" -> "GetSupplierShipments";
                case "job_parts" -> "GetJobParts";
                case "city_suppliers" -> "GetCitySuppliers";
                default -> throw new ServletException("Invalid report type");
            };
            
            try (CallableStatement stmt = conn.prepareCall("{call " + procedureName + "()}")) {
                boolean hasResults = stmt.execute();
                
                if (hasResults) {
                    ResultSet rs = stmt.getResultSet();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    // Get column names
                    JSONArray columns = new JSONArray();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.put(metaData.getColumnName(i));
                    }
                    result.put("columns", columns);
                    
                    // Get data
                    JSONArray data = new JSONArray();
                    while (rs.next()) {
                        JSONArray row = new JSONArray();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(rs.getString(i));
                        }
                        data.put(row);
                    }
                    result.put("data", data);
                }
                
                result.put("success", true);
            }
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
}
