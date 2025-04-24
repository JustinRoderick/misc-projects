package com.project4.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;
import org.json.JSONObject;
import com.project4.utils.DatabaseConnection;

@WebServlet(urlPatterns = {"/dataentry/suppliers/*"})
public class SuppliersServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        
        try (Connection conn = DatabaseConnection.getDataEntryConnection()) {
            String snum = request.getParameter("snum");
            String sname = request.getParameter("sname");
            String status = request.getParameter("status");
            String city = request.getParameter("city");
            
            if (snum == null || sname == null || status == null || city == null) {
                throw new ServletException("All fields are required");
            }
            
            String sql = "INSERT INTO suppliers (snum, sname, status, city) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, snum);
                stmt.setString(2, sname);
                stmt.setInt(3, Integer.parseInt(status));
                stmt.setString(4, city);
                
                int rowsAffected = stmt.executeUpdate();
                result.put("success", true);
                result.put("rowsAffected", rowsAffected);
            }
        } catch (SQLException | ServletException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        
        try (Connection conn = DatabaseConnection.getDataEntryConnection()) {
            String snum = request.getParameter("snum");
            String sname = request.getParameter("sname");
            String status = request.getParameter("status");
            String city = request.getParameter("city");
            
            if (snum == null) {
                throw new ServletException("Supplier number is required");
            }
            
            StringBuilder sql = new StringBuilder("UPDATE suppliers SET ");
            boolean needsComma = false;
            
            if (sname != null) {
                sql.append("sname = ?");
                needsComma = true;
            }
            if (status != null) {
                if (needsComma) sql.append(", ");
                sql.append("status = ?");
                needsComma = true;
            }
            if (city != null) {
                if (needsComma) sql.append(", ");
                sql.append("city = ?");
            }
            
            sql.append(" WHERE snum = ?");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                if (sname != null) stmt.setString(paramIndex++, sname);
                if (status != null) stmt.setInt(paramIndex++, Integer.parseInt(status));
                if (city != null) stmt.setString(paramIndex++, city);
                stmt.setString(paramIndex, snum);
                
                int rowsAffected = stmt.executeUpdate();
                result.put("success", true);
                result.put("rowsAffected", rowsAffected);
            }
        } catch (SQLException | ServletException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
}
