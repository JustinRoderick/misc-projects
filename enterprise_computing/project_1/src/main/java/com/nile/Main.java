/*
Name: Justin Roderick
Course: CNT 4714 – Spring 2025
Assignment title: Project 1 – An Event-driven Enterprise Simulation
Date: Sunday January 26, 2025
 */

package com.nile;

import com.nile.ui.MainFrame;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(()-> {
            JFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}