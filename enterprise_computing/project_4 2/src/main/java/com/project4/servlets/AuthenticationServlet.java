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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import com.project4.utils.DatabaseConnection;

@WebServlet("/authenticate")
public class AuthenticationServlet extends HttpServlet {
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        
        try {
            if (authenticateUser(username, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                session.setAttribute("role", username); // The role is the same as username in our case
                session.setAttribute("authenticated", true);
                
                String redirectPath = switch (username) {
                    case "root" -> "/root/dashboard";
                    case "client" -> "/client/dashboard";
                    case "dataentry" -> "/dataentry/dashboard";
                    case "theaccountant" -> "/accountant/dashboard";
                    default -> throw new ServletException("Invalid user type");
                };
                
                response.sendRedirect(request.getContextPath() + redirectPath);
            } else {
                request.setAttribute("error", "Invalid username or password");
                request.getRequestDispatcher("/index.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Database error: " + e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        } catch (ServletException e) {
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/index.jsp").forward(request, response);
        }
    }
    
    private boolean authenticateUser(String username, String password) throws SQLException {
        String sql = "SELECT * FROM usercredentials WHERE login_username = ? AND login_password = ?";
        
        try (Connection conn = DatabaseConnection.getAuthConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
}
