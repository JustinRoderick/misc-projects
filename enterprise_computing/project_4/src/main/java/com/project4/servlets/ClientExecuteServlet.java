package com.project4.servlets;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/client/execute")
public class ClientExecuteServlet extends BaseExecuteServlet {
    public ClientExecuteServlet() {
        super("client");
    }
}
