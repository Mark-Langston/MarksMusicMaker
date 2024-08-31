package com.example.marksmusicmaker;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class MarksMusicMakerUI extends JFrame {

    private JTextField filenameField;
    private JProgressBar progressBar;

    public MarksMusicMakerUI() {
        setTitle("Mark's Music Maker");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel titleLabel = new JLabel("Mark's Music Maker", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Filename input
        filenameField = new JTextField("output.midi");
        filenameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        JPanel filenamePanel = new JPanel(new BorderLayout());
        filenamePanel.add(new JLabel("Enter filename:"), BorderLayout.NORTH);
        filenamePanel.add(filenameField, BorderLayout.CENTER);
        filenamePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        mainPanel.add(filenamePanel);

        // Generate button
        JButton generateButton = new JButton("Generate");
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.addActionListener(e -> generateMusic());
        generateButton.setPreferredSize(new Dimension(200, 40));
        generateButton.setMaximumSize(new Dimension(200, 40));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        mainPanel.add(generateButton);

        // Progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        mainPanel.add(progressBar);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void generateMusic() {
        final String filename = filenameField.getText().trim();  // Mark filename as final

        // Ensure the filename ends with ".midi"
        String outputFilename = filename.toLowerCase().endsWith(".midi") ? filename : filename + ".midi";

        // Get the directory where the JAR or build is running
        File jarDir;
        try {
            jarDir = new File(MarksMusicMakerUI.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error determining JAR directory: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construct the full path for the output file in the same directory
        File outputFile = new File(jarDir, outputFilename);

        // Run the music generation in a background thread using SwingWorker
        SwingWorker<Void, Integer> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                // Show progress bar indeterminate while processing
                progressBar.setIndeterminate(true);

                try {
                    // Set the command to run the Python script with the filename argument
                    ProcessBuilder pb = new ProcessBuilder(
                            "C:\\Users\\mark_\\AppData\\Local\\Programs\\Python\\Python310\\python.exe", // Full path to Python 3.10 executable
                            "-u", // Run Python in unbuffered mode to get immediate output
                            "generate_music.py", // Python script
                            outputFile.getAbsolutePath() // Full path to the output file
                    );

                    // Set the working directory to where the script is located
                    pb.directory(new File("C:\\Users\\mark_\\OneDrive\\Desktop\\MarksMusicMaker\\MarksMusicMaker\\src\\main\\java\\com\\example\\marksmusicmaker"));
                    pb.redirectErrorStream(true);
                    Process process = pb.start();

                    // Capture and display the output of the Python script
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    String line;
                    int progress = 0;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);  // Optionally update the UI with script output
                        // Simulate progress updates
                        progress += 10; // Increment progress
                        publish(Math.min(progress, 100)); // Ensure progress does not exceed 100%
                    }

                    // Wait for the process to finish
                    int exitCode = process.waitFor();

                    // Check if the output file exists
                    if (exitCode == 0 && outputFile.exists()) {
                        JOptionPane.showMessageDialog(MarksMusicMakerUI.this, "Music generated and saved as " + outputFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(MarksMusicMakerUI.this, "Failed to generate music or find the output file. Check console for errors.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(MarksMusicMakerUI.this, "Error running Python script: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(100);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                // Update the progress bar with the latest value
                for (int progress : chunks) {
                    progressBar.setValue(progress);
                }
            }

            @Override
            protected void done() {
                progressBar.setIndeterminate(false);
                progressBar.setValue(100); // Complete
            }
        };

        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MarksMusicMakerUI::new);
    }
}
