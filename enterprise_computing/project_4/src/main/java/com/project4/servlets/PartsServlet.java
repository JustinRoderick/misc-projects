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

@WebServlet(urlPatterns = {"/dataentry/parts/*"})
public class PartsServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        
        try (Connection conn = DatabaseConnection.getDataEntryConnection()) {
            String pnum = request.getParameter("pnum");
            String pname = request.getParameter("pname");
            String color = request.getParameter("color");
            String weight = request.getParameter("weight");
            String city = request.getParameter("city");
            
            if (pnum == null || pname == null || color == null || weight == null || city == null) {
                throw new ServletException("All fields are required");
            }
            
            String sql = "INSERT INTO parts (pnum, pname, color, weight, city) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, pnum);
                stmt.setString(2, pname);
                stmt.setString(3, color);
                stmt.setDouble(4, Double.parseDouble(weight));
                stmt.setString(5, city);
                
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
            String pnum = request.getParameter("pnum");
            String pname = request.getParameter("pname");
            String color = request.getParameter("color");
            String weight = request.getParameter("weight");
            String city = request.getParameter("city");
            
            if (pnum == null) {
                throw new ServletException("Part number is required");
            }
            
            StringBuilder sql = new StringBuilder("UPDATE parts SET ");
            boolean needsComma = false;
            
            if (pname != null) {
                sql.append("pname = ?");
                needsComma = true;
            }
            if (color != null) {
                if (needsComma) sql.append(", ");
                sql.append("color = ?");
                needsComma = true;
            }
            if (weight != null) {
                if (needsComma) sql.append(", ");
                sql.append("weight = ?");
                needsComma = true;
            }
            if (city != null) {
                if (needsComma) sql.append(", ");
                sql.append("city = ?");
            }
            
            sql.append(" WHERE pnum = ?");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                if (pname != null) stmt.setString(paramIndex++, pname);
                if (color != null) stmt.setString(paramIndex++, color);
                if (weight != null) stmt.setDouble(paramIndex++, Double.parseDouble(weight));
                if (city != null) stmt.setString(paramIndex++, city);
                stmt.setString(paramIndex, pnum);
                
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
