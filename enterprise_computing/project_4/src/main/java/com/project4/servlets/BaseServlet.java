package com.project4.servlets;

import com.project4.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import org.json.JSONObject;
import org.json.JSONArray;

public abstract class BaseServlet extends HttpServlet {
    
    protected Connection getConnection() throws SQLException {
        return DatabaseUtil.getConnection(getUserType());
    }
    
    protected abstract String getUserType();
    
    protected void checkAuthentication(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null 
                || !session.getAttribute("username").equals(getUserType())) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
        }
    }
    
    protected JSONObject executeQuery(String sql) throws SQLException {
        JSONObject result = new JSONObject();
        JSONArray columns = new JSONArray();
        JSONArray data = new JSONArray();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int columnCount = rs.getMetaData().getColumnCount();
            
            // Get column names
            for (int i = 1; i <= columnCount; i++) {
                columns.put(rs.getMetaData().getColumnName(i));
            }
            
            // Get data
            while (rs.next()) {
                JSONArray row = new JSONArray();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(rs.getString(i));
                }
                data.put(row);
            }
            
            result.put("columns", columns);
            result.put("data", data);
            result.put("success", true);
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    protected JSONObject executeUpdate(String sql) throws SQLException {
        JSONObject result = new JSONObject();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            int rowsAffected = stmt.executeUpdate(sql);
            result.put("success", true);
            result.put("rowsAffected", rowsAffected);
            
        } catch (SQLException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    protected void sendJsonResponse(HttpServletResponse response, JSONObject json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json.toString());
    }
}
