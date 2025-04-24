package com.project4.servlets;

import com.project4.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.*;

public abstract class BaseExecuteServlet extends HttpServlet {
    private final String role;

    protected BaseExecuteServlet(String role) {
        this.role = role;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.err.println(role + "ExecuteServlet - doPost called");
        
        // Check authentication
        if (request.getSession() == null || 
            request.getSession().getAttribute("authenticated") == null ||
            !(Boolean)request.getSession().getAttribute("authenticated") ||
            !role.equals(request.getSession().getAttribute("role"))) {
            System.err.println(role + "ExecuteServlet - Not authorized");
            sendError(response, "Not authorized");
            return;
        }

        String sql = request.getParameter("sql");
        System.err.println(role + "ExecuteServlet - SQL query: " + sql);
        
        if (sql == null || sql.trim().isEmpty()) {
            System.err.println(role + "ExecuteServlet - Empty SQL query");
            sendError(response, "SQL query is required");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection(role)) {
            System.err.println(role + "ExecuteServlet - Got database connection");
            if (sql.trim().toLowerCase().startsWith("select")) {
                System.err.println(role + "ExecuteServlet - Executing SELECT query");
                executeQuery(conn, sql, response);
            } else {
                System.err.println(role + "ExecuteServlet - Executing UPDATE query");
                executeUpdate(conn, sql, response);
            }
        } catch (SQLException e) {
            System.err.println(role + "ExecuteServlet - SQL Error: " + e.getMessage());
            sendError(response, "Database error: " + e.getMessage());
        }
    }

    private void executeQuery(Connection conn, String sql, HttpServletResponse response) 
            throws SQLException, IOException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.err.println(role + "ExecuteServlet - Query returned " + columnCount + " columns");
            
            // Get column names
            JSONArray columns = new JSONArray();
            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnLabel(i);
                columns.put(columnName);
                System.err.println(role + "ExecuteServlet - Column " + i + ": " + columnName);
            }
            
            // Get data
            JSONArray data = new JSONArray();
            int rowCount = 0;
            while (rs.next()) {
                JSONArray row = new JSONArray();
                for (int i = 1; i <= columnCount; i++) {
                    String value = rs.getString(i);
                    row.put(value != null ? value : "null");
                    System.err.println(role + "ExecuteServlet - Row " + rowCount + ", Column " + i + ": " + value);
                }
                data.put(row);
                rowCount++;
            }
            System.err.println(role + "ExecuteServlet - Query returned " + rowCount + " rows");
            
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("columns", columns);
            result.put("data", data);
            
            String jsonResponse = result.toString();
            System.err.println(role + "ExecuteServlet - JSON response: " + jsonResponse);
            
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        }
    }

    private void executeUpdate(Connection conn, String sql, HttpServletResponse response) 
            throws SQLException, IOException {
        try (Statement stmt = conn.createStatement()) {
            int rowsAffected = stmt.executeUpdate(sql);
            System.err.println(role + "ExecuteServlet - Update affected " + rowsAffected + " rows");
            
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("rowsAffected", rowsAffected);
            
            String jsonResponse = result.toString();
            System.err.println(role + "ExecuteServlet - JSON response: " + jsonResponse);
            
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse);
        }
    }

    protected void sendError(HttpServletResponse response, String message) throws IOException {
        JSONObject error = new JSONObject();
        error.put("success", false);
        error.put("error", message);
        
        String jsonResponse = error.toString();
        System.err.println(role + "ExecuteServlet - Error response: " + jsonResponse);
        
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }
}
