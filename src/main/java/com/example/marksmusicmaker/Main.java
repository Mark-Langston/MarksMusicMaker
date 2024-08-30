// Marks Music Maker
// 8/30/2024 Remasterd Version 1.0 Ported from C++


package com.example.marksmusicmaker;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class Main {
    public static void main(String[] args) {
        // Set the Look and Feel to the system's default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
            System.out.println("Failed to set Look and Feel");
        }

        // Launch the GUI
        SwingUtilities.invokeLater(() -> {
            try {
                new MarksMusicMakerUI();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error initializing the application: " + e.getMessage());
            }
        });
    }
}
