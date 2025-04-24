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

@WebServlet(urlPatterns = {"/client/*"})
public class ClientServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            request.getRequestDispatcher("/WEB-INF/jsp/client/dashboard.jsp").forward(request, response);
            return;
        }
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        JSONObject result = new JSONObject();
        
        try (Connection conn = DatabaseConnection.getClientConnection()) {
            String sql = request.getParameter("sql");
            if (sql == null || sql.trim().isEmpty()) {
                throw new ServletException("SQL query is required");
            }
            
            // Client can only execute SELECT queries
            if (!isSelectQuery(sql)) {
                throw new ServletException("Only SELECT queries are allowed for client users");
            }
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
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
                result.put("success", true);
            }
        } catch (SQLException | ServletException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
    
    private boolean isSelectQuery(String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }
}
