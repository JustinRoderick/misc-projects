package com.project4.servlets;

import jakarta.servlet.annotation.WebServlet;

@WebServlet("/dataentry/execute")
public class DataEntryExecuteServlet extends BaseExecuteServlet {
    public DataEntryExecuteServlet() {
        super("dataentry");
    }
}
