package com.project4.servlets;

import com.project4.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/authenticate")
public class AuthenticationServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            if (authenticateUser(username, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("role", username); // The role is the same as username in our case
                session.setAttribute("authenticated", true);
                
                // Redirect based on user type
                switch (username) {
                    case "root":
                        response.sendRedirect(request.getContextPath() + "/root/dashboard");
                        break;
                    case "client":
                        response.sendRedirect(request.getContextPath() + "/client/dashboard");
                        break;
                    case "dataentry":
                        response.sendRedirect(request.getContextPath() + "/dataentry/dashboard");
                        break;
                    case "theaccountant":
                        response.sendRedirect(request.getContextPath() + "/accountant/dashboard");
                        break;
                    default:
                        throw new ServletException("Invalid user type");
                }
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
    
    private boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM usercredentials WHERE login_username = ? AND login_password = ?";
        
        try (Connection conn = DatabaseUtil.getConnection("system");
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
