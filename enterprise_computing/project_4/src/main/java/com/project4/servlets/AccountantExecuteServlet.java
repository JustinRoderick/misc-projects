package com.project4.servlets;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/accountant/execute")
public class AccountantExecuteServlet extends BaseExecuteServlet {
    public AccountantExecuteServlet() {
        super("theaccountant");
    }
}
