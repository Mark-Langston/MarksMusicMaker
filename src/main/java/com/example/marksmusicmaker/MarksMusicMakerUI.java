package com.example.marksmusicmaker;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MarksMusicMakerUI extends JFrame {

    private JTextField filenameField;
    private JProgressBar progressBar;
    private Map<String, JCheckBox> instrumentCheckBoxes;

    public MarksMusicMakerUI() {
        setTitle("Mark's Music Maker");
        setSize(500, 600); // Adjusted size to accommodate more instruments in two columns
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

        // Instrument selection
        JPanel instrumentPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        instrumentPanel.setBorder(BorderFactory.createTitledBorder("Select Instruments"));
        instrumentCheckBoxes = new HashMap<>();
        addInstrumentCheckBoxes(instrumentPanel);
        mainPanel.add(instrumentPanel);

        // Generate button
        JButton generateButton = new JButton("Generate");
        generateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        generateButton.addActionListener(e -> generateMusic());
        generateButton.setPreferredSize(new Dimension(200, 40));
        generateButton.setMaximumSize(new Dimension(200, 40));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        mainPanel.add(generateButton);

        // Progress bar
        progressBar = new JProgressBar(0, 100); // Set progress bar with range 0 to 100
        progressBar.setStringPainted(true);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacer
        mainPanel.add(progressBar);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void addInstrumentCheckBoxes(JPanel panel) {
        String[] instruments = {
                "Piano", "Electric Piano", "Organ", "Guitar", "Bass",
                "Strings", "Ensemble", "Brass", "Reed", "Pipe",
                "Synth Lead", "Synth Pad", "Synth Effects", "Ethnic",
                "Percussive", "Sound Effects", "Drums"
        };

        for (String instrument : instruments) {
            JCheckBox checkBox = new JCheckBox(instrument);
            instrumentCheckBoxes.put(instrument, checkBox);
            panel.add(checkBox);
        }
    }

    private void generateMusic() {
        // Ensure filename is correctly captured and validated
        String filename = filenameField.getText().trim(); // Trim to remove any leading or trailing whitespace

        // Ensure the filename ends with ".midi"
        String finalFilename = filename.toLowerCase().endsWith(".midi") ? filename : filename + ".midi";

        // Get selected instruments
        Map<String, Boolean> selectedInstruments = new HashMap<>();
        for (Map.Entry<String, JCheckBox> entry : instrumentCheckBoxes.entrySet()) {
            selectedInstruments.put(entry.getKey(), entry.getValue().isSelected());
        }

        MusicGenerator generator = new MusicGenerator();
        // Run music generation in a separate thread to keep the UI responsive
        new Thread(() -> generator.generate(finalFilename, selectedInstruments, progressBar)).start();
    }
}