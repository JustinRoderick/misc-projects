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
        String pathInfo = request.getPathInfo();
        JSONObject result = new JSONObject();
        
        try (Connection conn = DatabaseConnection.getSystemConnection()) {
            String operation = request.getParameter("operation");
            if (operation == null) {
                throw new ServletException("Operation parameter is required");
            }
            
            switch (operation) {
                case "create_user":
                    createUser(conn, request, result);
                    break;
                case "delete_user":
                    deleteUser(conn, request, result);
                    break;
                case "modify_permissions":
                    modifyPermissions(conn, request, result);
                    break;
                case "execute_sql":
                    executeSql(conn, request, result);
                    break;
                default:
                    throw new ServletException("Invalid operation");
            }
            
            result.put("success", true);
        } catch (SQLException | ServletException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
    
    private void createUser(Connection conn, HttpServletRequest request, JSONObject result) 
            throws SQLException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String role = request.getParameter("role");
        
        if (username == null || password == null || role == null) {
            throw new SQLException("Username, password, and role are required");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO users (username, password, role) VALUES (?, ?, ?)")) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
        }
    }
    
    private void deleteUser(Connection conn, HttpServletRequest request, JSONObject result) 
            throws SQLException {
        String username = request.getParameter("username");
        
        if (username == null) {
            throw new SQLException("Username is required");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM users WHERE username = ?")) {
            stmt.setString(1, username);
            stmt.executeUpdate();
        }
    }
    
    private void modifyPermissions(Connection conn, HttpServletRequest request, JSONObject result) 
            throws SQLException {
        String username = request.getParameter("username");
        String newRole = request.getParameter("newRole");
        
        if (username == null || newRole == null) {
            throw new SQLException("Username and new role are required");
        }
        
        try (PreparedStatement stmt = conn.prepareStatement(
                "UPDATE users SET role = ? WHERE username = ?")) {
            stmt.setString(1, newRole);
            stmt.setString(2, username);
            stmt.executeUpdate();
        }
    }
    
    private void executeSql(Connection conn, HttpServletRequest request, JSONObject result) 
            throws SQLException {
        String sql = request.getParameter("sql");
        if (sql == null || sql.trim().isEmpty()) {
            throw new SQLException("SQL query is required");
        }
        
        if (isSelectQuery(sql)) {
            result = executeQuery(sql);
        } else {
            if (isShipmentOperation(sql)) {
                result = executeShipmentOperation(sql);
            } else {
                result = executeUpdate(sql);
            }
        }
    }
    
    private boolean isSelectQuery(String sql) {
        return sql.trim().toLowerCase().startsWith("select");
    }
    
    private boolean isShipmentOperation(String sql) {
        String lowerSql = sql.trim().toLowerCase();
        return (lowerSql.startsWith("insert into shipments") || 
                lowerSql.startsWith("update shipments")) &&
               lowerSql.contains("quantity");
    }
    
    private JSONObject executeShipmentOperation(String sql) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSystemConnection();
            conn.setAutoCommit(false);
            
            JSONObject result = executeUpdate(sql);
            
            if (result.getBoolean("success")) {
                updateSupplierStatus(conn);
            }
            
            conn.commit();
            return result;
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private void updateSupplierStatus(Connection conn) throws SQLException {
        String findSuppliersSql = 
            "SELECT DISTINCT s.snum FROM suppliers s " +
            "INNER JOIN shipments sh ON s.snum = sh.snum " +
            "WHERE sh.quantity >= 100";
            
        try (PreparedStatement stmt = conn.prepareStatement(findSuppliersSql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String updateStatusSql = 
                    "UPDATE suppliers SET status = status + 5 WHERE snum = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {
                    updateStmt.setString(1, rs.getString("snum"));
                    updateStmt.executeUpdate();
                }
            }
        }
    }
    
    private JSONObject executeQuery(String sql) throws SQLException {
        try (PreparedStatement stmt = DatabaseConnection.getSystemConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("data", resultSetToJson(rs));
            return result;
        }
    }
    
    private JSONObject executeUpdate(String sql) throws SQLException {
        try (PreparedStatement stmt = DatabaseConnection.getSystemConnection().prepareStatement(sql)) {
            stmt.executeUpdate();
            JSONObject result = new JSONObject();
            result.put("success", true);
            return result;
        }
    }
    
    private JSONObject resultSetToJson(ResultSet rs) throws SQLException {
        JSONObject result = new JSONObject();
        while (rs.next()) {
            int columns = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columns; i++) {
                result.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
            }
        }
        return result;
    }
}
