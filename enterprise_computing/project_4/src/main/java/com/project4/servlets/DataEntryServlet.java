package com.project4.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet("/dataentry/*")
public class DataEntryServlet extends BaseServlet {
    
    @Override
    protected String getUserType() {
        return "dataentry";
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        checkAuthentication(request, response);
        
        String table = request.getParameter("table");
        if (table == null || table.trim().isEmpty()) {
            sendJsonResponse(response, new JSONObject().put("error", "Table name is required"));
            return;
        }
        
        try {
            JSONObject result;
            switch (table.toLowerCase()) {
                case "suppliers":
                    result = insertSupplier(request);
                    break;
                case "parts":
                    result = insertPart(request);
                    break;
                case "jobs":
                    result = insertJob(request);
                    break;
                case "shipments":
                    result = insertShipment(request);
                    break;
                default:
                    result = new JSONObject().put("error", "Invalid table name");
            }
            sendJsonResponse(response, result);
        } catch (SQLException e) {
            sendJsonResponse(response, new JSONObject()
                .put("success", false)
                .put("error", e.getMessage()));
        }
    }
    
    private JSONObject insertSupplier(HttpServletRequest request) throws SQLException {
        String sql = "INSERT INTO suppliers (snum, sname, status, city) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, request.getParameter("snum"));
            stmt.setString(2, request.getParameter("sname"));
            stmt.setInt(3, Integer.parseInt(request.getParameter("status")));
            stmt.setString(4, request.getParameter("city"));
            
            int rowsAffected = stmt.executeUpdate();
            return new JSONObject()
                .put("success", true)
                .put("rowsAffected", rowsAffected);
        }
    }
    
    private JSONObject insertPart(HttpServletRequest request) throws SQLException {
        String sql = "INSERT INTO parts (pnum, pname, color, weight, city) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, request.getParameter("pnum"));
            stmt.setString(2, request.getParameter("pname"));
            stmt.setString(3, request.getParameter("color"));
            stmt.setDouble(4, Double.parseDouble(request.getParameter("weight")));
            stmt.setString(5, request.getParameter("city"));
            
            int rowsAffected = stmt.executeUpdate();
            return new JSONObject()
                .put("success", true)
                .put("rowsAffected", rowsAffected);
        }
    }
    
    private JSONObject insertJob(HttpServletRequest request) throws SQLException {
        String sql = "INSERT INTO jobs (jnum, jname, numworkers, city) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, request.getParameter("jnum"));
            stmt.setString(2, request.getParameter("jname"));
            stmt.setInt(3, Integer.parseInt(request.getParameter("numworkers")));
            stmt.setString(4, request.getParameter("city"));
            
            int rowsAffected = stmt.executeUpdate();
            return new JSONObject()
                .put("success", true)
                .put("rowsAffected", rowsAffected);
        }
    }
    
    private JSONObject insertShipment(HttpServletRequest request) throws SQLException {
        String sql = "INSERT INTO shipments (snum, pnum, jnum, quantity) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, request.getParameter("snum"));
            stmt.setString(2, request.getParameter("pnum"));
            stmt.setString(3, request.getParameter("jnum"));
            stmt.setInt(4, Integer.parseInt(request.getParameter("quantity")));
            
            int rowsAffected = stmt.executeUpdate();
            return new JSONObject()
                .put("success", true)
                .put("rowsAffected", rowsAffected);
        }
    }
}
