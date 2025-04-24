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
import com.project4.utils.DatabaseConnection;

@WebServlet(urlPatterns = {"/dataentry/jobs/*"})
public class JobsServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        
        try (Connection conn = DatabaseConnection.getDataEntryConnection()) {
            String jnum = request.getParameter("jnum");
            String jname = request.getParameter("jname");
            String numworkers = request.getParameter("numworkers");
            String city = request.getParameter("city");
            
            if (jnum == null || jname == null || numworkers == null || city == null) {
                throw new ServletException("All fields are required");
            }
            
            String sql = "INSERT INTO jobs (jnum, jname, numworkers, city) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, jnum);
                stmt.setString(2, jname);
                stmt.setInt(3, Integer.parseInt(numworkers));
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
            String jnum = request.getParameter("jnum");
            String jname = request.getParameter("jname");
            String numworkers = request.getParameter("numworkers");
            String city = request.getParameter("city");
            
            if (jnum == null) {
                throw new ServletException("Job number is required");
            }
            
            StringBuilder sql = new StringBuilder("UPDATE jobs SET ");
            boolean needsComma = false;
            
            if (jname != null) {
                sql.append("jname = ?");
                needsComma = true;
            }
            if (numworkers != null) {
                if (needsComma) sql.append(", ");
                sql.append("numworkers = ?");
                needsComma = true;
            }
            if (city != null) {
                if (needsComma) sql.append(", ");
                sql.append("city = ?");
            }
            
            sql.append(" WHERE jnum = ?");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                if (jname != null) stmt.setString(paramIndex++, jname);
                if (numworkers != null) stmt.setInt(paramIndex++, Integer.parseInt(numworkers));
                if (city != null) stmt.setString(paramIndex++, city);
                stmt.setString(paramIndex, jnum);
                
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
