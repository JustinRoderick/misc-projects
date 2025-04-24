
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

@WebServlet(urlPatterns = {"/root/*"})
public class RootServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            request.getRequestDispatcher("/WEB-INF/jsp/root/dashboard.jsp").forward(request, response);
            return;
        }
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String sql = request.getParameter("sql");
        JSONObject result = new JSONObject();
        
        if (sql == null || sql.trim().isEmpty()) {
            result.put("success", false);
            result.put("error", "SQL command is required");
            response.setContentType("application/json");
            response.getWriter().write(result.toString());
            return;
        }

        try (Connection conn = DatabaseConnection.getSystemConnection()) {
            sql = sql.trim();
            boolean isQuery = sql.toLowerCase().startsWith("select");
            
            if (isQuery) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(sql)) {
                    
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    
                    JSONArray columns = new JSONArray();
                    for (int i = 1; i <= columnCount; i++) {
                        columns.put(metaData.getColumnName(i));
                    }
                    result.put("columns", columns);
                    
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
            } else {
                try (Statement stmt = conn.createStatement()) {
                    int rowsAffected = stmt.executeUpdate(sql);
                    result.put("success", true);
                    result.put("rowsAffected", rowsAffected);
                }
            }
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
}
