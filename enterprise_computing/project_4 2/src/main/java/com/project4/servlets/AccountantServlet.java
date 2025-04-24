/* Name: Justin Roderick
Course: CNT 4714 – Spring 2025 – Project Four
Assignment title: A Three-Tier Distributed Web-Based Application
Date: April 23, 2025
*/

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
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            request.getRequestDispatcher("/WEB-INF/jsp/accountant/dashboard.jsp").forward(request, response);
            return;
        }
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        if ("/execute".equals(request.getPathInfo())) {
            executeReport(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void executeReport(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        String reportType = request.getParameter("report_type");
        
        try (Connection conn = DatabaseConnection.getAccountantConnection()) {
            String procedure = switch (reportType) {
                case "total_parts_weight" -> "Get_The_Sum_Of_All_Parts_Weights";
                case "max_supplier_status" -> "Get_The_Maximum_Status_Of_All_Suppliers";
                case "total_shipments" -> "Get_The_Total_Number_Of_Shipments";
                case "max_workers_job" -> "Get_The_Name_Of_The_Job_With_The_Most_Workers";
                case "supplier_status_list" -> "List_The_Name_And_Status_Of_All_Suppliers";
                default -> throw new ServletException("Invalid report type");
            };
            
            try (CallableStatement stmt = conn.prepareCall("{call " + procedure + "()}");
                 ResultSet rs = stmt.executeQuery()) {
                
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                
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
