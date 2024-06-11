package preevisiontosimulink.ui;

import preevisiontosimulink.parser.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UIRunner {
    private static JTextField modelNameField;
    private static JTextField currentValueField;
    private static JLabel fileLabel;
    private static List<File> selectedFiles = new ArrayList<>();
    private static JComboBox<String> generatorComboBox;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Simulink Model Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350);

        JPanel panel = new JPanel();
        frame.add(panel);
        placeComponents(panel);

        // Set the location of the main frame to the center of the screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel titleLabel = new JLabel("Simulink Model Generator");
        titleLabel.setBounds(160, 20, 200, 25);
        panel.add(titleLabel);

        JLabel modelNameLabel = new JLabel("Model Name:");
        modelNameLabel.setBounds(50, 60, 80, 25);
        panel.add(modelNameLabel);

        modelNameField = new JTextField(20);
        modelNameField.setBounds(140, 60, 300, 25);
        panel.add(modelNameField);

        JLabel currentValueLabel = new JLabel("Default Current Value:");
        currentValueLabel.setBounds(50, 100, 150, 25);
        panel.add(currentValueLabel);

        currentValueField = new JTextField(20);
        currentValueField.setBounds(200, 100, 240, 25);
        panel.add(currentValueField);

        JLabel fileChooserLabel = new JLabel("Select Files:");
        fileChooserLabel.setBounds(50, 140, 80, 25);
        panel.add(fileChooserLabel);

        JButton fileButton = new JButton("Browse");
        fileButton.setBounds(140, 140, 100, 25);
        panel.add(fileButton);

        fileLabel = new JLabel("");
        fileLabel.setBounds(250, 140, 200, 25);
        panel.add(fileLabel);

        JLabel generatorLabel = new JLabel("Generator:");
        generatorLabel.setBounds(50, 180, 80, 25);
        panel.add(generatorLabel);

        String[] generators = {"Wiring Harness From KBL", "Wiring Harness From Excel"};
        generatorComboBox = new JComboBox<>(generators);
        generatorComboBox.setBounds(140, 180, 300, 25);
        panel.add(generatorComboBox);

        JButton runButton = new JButton("Generate");
        runButton.setBounds(200, 220, 100, 25);
        panel.add(runButton);

        JLabel statusLabel = new JLabel("");
        statusLabel.setBounds(160, 260, 200, 25);
        panel.add(statusLabel);

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir"))); // Set the current directory
                fileChooser.setMultiSelectionEnabled(true); // Enable multiple selection

                int result = fileChooser.showOpenDialog(panel);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] files = fileChooser.getSelectedFiles();
                    StringBuilder fileNames = new StringBuilder(fileLabel.getText());
                    for (File file : files) {
                        if (!selectedFiles.contains(file)) {
                            selectedFiles.add(file);
                            fileNames.append(file.getName()).append("; ");
                        }
                    }
                    fileLabel.setText(fileNames.toString());
                    fileLabel.setToolTipText(fileNames.toString()); // Show the file names on hover
                }
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String modelName = modelNameField.getText();
                String currentValueStr = currentValueField.getText();
                Double currentValue = null;
                if (!currentValueStr.isEmpty()) {
                    try {
                        currentValue = Double.parseDouble(currentValueStr);
                    } catch (NumberFormatException ex) {
                        statusLabel.setText("Invalid current value. Please enter a valid number.");
                        return;
                    }
                }

                if (modelName.isEmpty() || selectedFiles.isEmpty()) {
                    statusLabel.setText("Model name or files not selected.");
                    return;
                }

                String selectedGenerator = (String) generatorComboBox.getSelectedItem();
                if (selectedGenerator == null) {
                    statusLabel.setText("No generator selected.");
                    return;
                }

                statusLabel.setText("Generating Simulink model...");

                List<String> filePaths = new ArrayList<>();
                for (File file : selectedFiles) {
                    filePaths.add(file.getAbsolutePath());
                }

                switch (selectedGenerator) {
                    case "Wiring Harness From KBL":
                        WiringHarnessFromKBL wiringHarnessFromKBL = new WiringHarnessFromKBL(modelName, filePaths, currentValue);
                        wiringHarnessFromKBL.generateModel();
                        break;
                    case "Wiring Harness From Excel":
                        WiringHarnessFromExcel wiringHarnessFromExcel = new WiringHarnessFromExcel(modelName, filePaths);
                        wiringHarnessFromExcel.generateModel();
                        break;
                    default:
                        statusLabel.setText("Unknown generator selected.");
                        return;
                }

                statusLabel.setText("Simulink model generated.");
            }
        });
    }
}
