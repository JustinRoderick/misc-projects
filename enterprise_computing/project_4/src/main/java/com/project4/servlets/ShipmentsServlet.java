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

@WebServlet(urlPatterns = {"/dataentry/shipments/*"})
public class ShipmentsServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getDataEntryConnection();
            conn.setAutoCommit(false);
            
            String snum = request.getParameter("snum");
            String pnum = request.getParameter("pnum");
            String jnum = request.getParameter("jnum");
            String quantity = request.getParameter("quantity");
            
            if (snum == null || pnum == null || jnum == null || quantity == null) {
                throw new ServletException("All fields are required");
            }
            
            // First insert the shipment
            String sql = "INSERT INTO shipments (snum, pnum, jnum, quantity) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, snum);
                stmt.setString(2, pnum);
                stmt.setString(3, jnum);
                stmt.setInt(4, Integer.parseInt(quantity));
                
                stmt.executeUpdate();
            }
            
            // Then check if we need to update supplier status
            int quantityNum = Integer.parseInt(quantity);
            if (quantityNum >= 100) {
                updateSupplierStatus(conn, snum);
            }
            
            conn.commit();
            result.put("success", true);
            
        } catch (SQLException | ServletException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            result.put("success", false);
            result.put("error", e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log close error
                }
            }
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
    
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getDataEntryConnection();
            conn.setAutoCommit(false);
            
            String snum = request.getParameter("snum");
            String pnum = request.getParameter("pnum");
            String jnum = request.getParameter("jnum");
            String quantity = request.getParameter("quantity");
            
            if (snum == null || pnum == null || jnum == null) {
                throw new ServletException("Primary key fields (snum, pnum, jnum) are required");
            }
            
            StringBuilder sql = new StringBuilder("UPDATE shipments SET ");
            if (quantity != null) {
                sql.append("quantity = ? ");
            }
            sql.append("WHERE snum = ? AND pnum = ? AND jnum = ?");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                int paramIndex = 1;
                if (quantity != null) {
                    stmt.setInt(paramIndex++, Integer.parseInt(quantity));
                }
                stmt.setString(paramIndex++, snum);
                stmt.setString(paramIndex++, pnum);
                stmt.setString(paramIndex, jnum);
                
                stmt.executeUpdate();
                
                // Check if we need to update supplier status
                if (quantity != null && Integer.parseInt(quantity) >= 100) {
                    updateSupplierStatus(conn, snum);
                }
            }
            
            conn.commit();
            result.put("success", true);
            
        } catch (SQLException | ServletException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    // Log rollback error
                }
            }
            result.put("success", false);
            result.put("error", e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Log close error
                }
            }
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
    
    private void updateSupplierStatus(Connection conn, String snum) throws SQLException {
        String sql = "UPDATE suppliers SET status = status + 5 WHERE snum = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, snum);
            stmt.executeUpdate();
        }
    }
}
