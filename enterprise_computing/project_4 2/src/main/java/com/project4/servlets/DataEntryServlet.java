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

@WebServlet(urlPatterns = {"/dataentry/*"})
public class DataEntryServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/dashboard")) {
            request.getRequestDispatcher("/WEB-INF/jsp/dataentry/dashboard.jsp").forward(request, response);
            return;
        }
        
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        if ("/execute".equals(request.getPathInfo())) {
            executeDataEntry(request, response);
            return;
        }
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void executeDataEntry(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        JSONObject result = new JSONObject();
        String table = request.getParameter("table");
        
        try (Connection conn = DatabaseConnection.getDataEntryConnection()) {
            String sql = switch (table) {
                case "suppliers" -> "INSERT INTO suppliers (snum, sname, status, city) VALUES (?, ?, ?, ?)";
                case "parts" -> "INSERT INTO parts (pnum, pname, color, weight, city) VALUES (?, ?, ?, ?, ?)";
                case "jobs" -> "INSERT INTO jobs (jnum, jname, numworkers, city) VALUES (?, ?, ?, ?)";
                case "shipments" -> "INSERT INTO shipments (snum, pnum, jnum, quantity) VALUES (?, ?, ?, ?)";
                default -> throw new ServletException("Invalid table specified");
            };

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                switch (table) {
                    case "suppliers":
                        stmt.setString(1, request.getParameter("snum"));
                        stmt.setString(2, request.getParameter("sname"));
                        stmt.setInt(3, Integer.parseInt(request.getParameter("status")));
                        stmt.setString(4, request.getParameter("city"));
                        break;
                    case "parts":
                        stmt.setString(1, request.getParameter("pnum"));
                        stmt.setString(2, request.getParameter("pname"));
                        stmt.setString(3, request.getParameter("color"));
                        stmt.setDouble(4, Double.parseDouble(request.getParameter("weight")));
                        stmt.setString(5, request.getParameter("city"));
                        break;
                    case "jobs":
                        stmt.setString(1, request.getParameter("jnum"));
                        stmt.setString(2, request.getParameter("jname"));
                        stmt.setInt(3, Integer.parseInt(request.getParameter("numworkers")));
                        stmt.setString(4, request.getParameter("city"));
                        break;
                    case "shipments":
                        stmt.setString(1, request.getParameter("snum"));
                        stmt.setString(2, request.getParameter("pnum"));
                        stmt.setString(3, request.getParameter("jnum"));
                        stmt.setInt(4, Integer.parseInt(request.getParameter("quantity")));
                        break;
                }

                int rowsAffected = stmt.executeUpdate();
                result.put("success", true);
                result.put("rowsAffected", rowsAffected);
            }
        } catch (SQLException | NumberFormatException e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }

        response.setContentType("application/json");
        response.getWriter().write(result.toString());
    }
}
