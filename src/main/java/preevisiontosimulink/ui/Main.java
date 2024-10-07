package preevisiontosimulink.ui;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import preevisiontosimulink.parser.KBLParser;
import preevisiontosimulink.util.StringUtils;
import preevisiontosimulink.util.UIUtils;

// Main class for the Wiring Harness Optimizer application
public class Main {

    // UI components
    private static Text modelNameField; // Field for entering model name
    private static Label fileLabel; // Label to display selected files
    private static List<File> selectedFiles = new ArrayList<>(); // List of selected files
    private static Combo operationComboBox; // Combo box for selecting operations
    private static Button generateButton; // Button to initiate generation
    private static Button clearFilesButton; // Button to clear selected files
    private static Label statusLabel; // Label to display status messages

    // Main method to launch the application
    public static void main(String[] args) {
        // Create display and shell for the UI
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("Wiring Harness Optimizer");
        shell.setLayout(new GridLayout(3, false)); // Set a layout with 3 columns

        // Load and set the application icon
        InputStream inputStream = Main.class.getResourceAsStream("/resources/Edag.png");
        if (inputStream != null) {
            Image icon = new Image(display, inputStream);
            shell.setImage(icon); // Set the shell icon
        } else {
            System.err.println("Failed to load icon: /resources/Edag.png");
        }

        createContents(shell); // Create UI components

        shell.setSize(800, 350); // Set initial size of the window
        UIUtils.centerWindow(shell); // Center the window on the screen
        shell.open(); // Open the shell
        // Event loop to keep the application running
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep(); // Sleep if there are no events to process
            }
        }
        display.dispose(); // Dispose of the display when done
    }

    // Method to create and arrange UI components within the shell
    private static void createContents(final Shell shell) {
        // Model Name components
        Label modelNameLabel = new Label(shell, SWT.NONE); // Label for model name input
        modelNameLabel.setText("Model Name:");
        modelNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        modelNameField = new Text(shell, SWT.BORDER); // Text field for entering the model name
        GridData modelNameGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        modelNameField.setLayoutData(modelNameGridData); // Set layout for model name field

        // File selection components
        Label fileChooserLabel = new Label(shell, SWT.NONE); // Label for file selection
        fileChooserLabel.setText("Select Files:");
        fileChooserLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        // Button to browse files
        Button fileButton = new Button(shell, SWT.PUSH);
        fileButton.setText("Browse");
        GridData fileButtonGridData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
        fileButton.setLayoutData(fileButtonGridData);
        fileButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // File dialog for selecting multiple files
                FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);
                fileDialog.setFilterPath(System.getProperty("user.dir")); // Default path
                fileDialog.setFilterExtensions(new String[] { "*.kbl", "*.*" }); // Filter for files
                String filePath = fileDialog.open(); // Open the dialog
                if (filePath != null) {
                    String[] fileNames = fileDialog.getFileNames(); // Get selected file names
                    for (String fileName : fileNames) {
                        File file = new File(fileDialog.getFilterPath() + File.separator + fileName);
                        // Add file to the list if it's not already included
                        if (!selectedFiles.contains(file)) {
                            selectedFiles.add(file);
                            fileLabel.setText(fileLabel.getText() + file.getName() + "; "); // Update label
                        }
                    }
                    checkEnableGenerateButtons(); // Check if generate button can be enabled
                }
            }
        });

        // Button to clear selected files
        clearFilesButton = new Button(shell, SWT.PUSH);
        clearFilesButton.setText("Clear Files");
        GridData clearFilesButtonGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
        clearFilesButton.setLayoutData(clearFilesButtonGridData);
        clearFilesButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Clear selected files and update the label
                selectedFiles.clear();
                fileLabel.setText("");
                checkEnableGenerateButtons(); // Update button state
            }
        });

        // Label to display selected files
        fileLabel = new Label(shell, SWT.NONE);
        GridData fileLabelGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        fileLabel.setLayoutData(fileLabelGridData);

        // Operation selection components
        Label operationLabel = new Label(shell, SWT.NONE); // Label for operation selection
        operationLabel.setText("Operation:");
        operationLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

        operationComboBox = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY); // Combo box for operations
        operationComboBox.setItems(new String[] { "Thermal Simulation", "Wiring Harness From KBL" });
        operationComboBox.select(0); // Select default operation
        GridData operationComboGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        operationComboBox.setLayoutData(operationComboGridData); // Set layout for combo box

        // Listener for operation selection
        operationComboBox.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // Clear selected files and reset status label when operation changes
                selectedFiles.clear();
                fileLabel.setText("");
                statusLabel.setText("");
                checkEnableGenerateButtons(); // Update button state
            }
        });

        // Generate button to perform the selected operation
        generateButton = new Button(shell, SWT.PUSH);
        generateButton.setText("Generate");
        GridData generateButtonGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        generateButton.setLayoutData(generateButtonGridData);
        generateButton.setEnabled(false); // Initially disabled
        generateButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String modelName = modelNameField.getText(); // Get model name from input field
                // Generate model name if not provided and files are selected
                if (modelName.isEmpty() && !selectedFiles.isEmpty()) {
                    List<String> fileNameParts = new ArrayList<>();
                    String fileName = null;
                    for (File file : selectedFiles) {
                        if (file.getName().endsWith(".kbl")) {
                            fileNameParts.add(StringUtils.getFirstPart(file.getName())); // Get the first part of the filename
                            fileName = StringUtils.removeEnding(file.getName()); // Remove file extension
                            if (fileNameParts.size() == 2) {
                                break; // Stop if we have two parts
                            }
                        }
                    }
                    // Create model name based on selected files
                    if (fileNameParts.size() == 2) {
                        modelName = fileNameParts.get(0) + "_" + fileNameParts.get(1);
                    } else {
                        modelName = fileName; // Use filename if not two parts
                    }
                }

                // Validate model name and file selection
                if (modelName.isEmpty() || selectedFiles.isEmpty()) {
                    statusLabel.setText("Model name or files not selected.");
                    return; // Exit if invalid
                }

                String selectedOperation = operationComboBox.getText(); // Get selected operation
                if (selectedOperation == null) {
                    statusLabel.setText("No operation selected.");
                    return; // Exit if no operation
                }

                List<String> filePaths = new ArrayList<>();
                for (File file : selectedFiles) {
                    filePaths.add(file.getAbsolutePath()); // Collect file paths
                }

                // Execute the selected operation
                switch (selectedOperation) {
                    case "Thermal Simulation":
                        statusLabel.setText("Getting Information from KBL file...");
                        KBLParser thermalSimulation = new KBLParser(modelName, filePaths);
                        thermalSimulation.getGeneralWireInformation(); // Get information from the KBL file
                        new GeneralWireWindow(thermalSimulation); // Open a new window with the result
                        break;
                    case "Wiring Harness From KBL":
                        statusLabel.setText("Generating Simulink model...");
                        KBLParser wiringHarness = new KBLParser(modelName, filePaths);
                        wiringHarness.generateModel(); // Generate the model
                        statusLabel.setText("Simulink model generated."); // Update status
                        break;
                    default:
                        statusLabel.setText("Unknown operation selected."); // Handle unknown operations
                        return;
                }

                // Clear status message after 2 seconds
                Display.getDefault().timerExec(2000, new Runnable() {
                    @Override
                    public void run() {
                        statusLabel.setText(""); // Clear status label
                    }
                });
            }
        });

        // Status label to display messages to the user
        statusLabel = new Label(shell, SWT.NONE);
        GridData statusLabelGridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
        statusLabel.setLayoutData(statusLabelGridData);

        // Adjust layout settings to increase spacing
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 15; // Increase vertical spacing between elements
        shell.setLayout(layout); // Set layout for the shell

        // Check initial state for enabling the Generate button
        checkEnableGenerateButtons();
    }

    // Method to determine if the Generate button should be enabled
    private static void checkEnableGenerateButtons() {
        String selectedOperation = operationComboBox.getText();
        boolean isEnabled = false;

        // Enable button based on selected operation and file selection
        if (selectedOperation.equals("Thermal Simulation")) {
            isEnabled = selectedFiles.size() == 1 && selectedFiles.get(0).getName().endsWith(".kbl"); // Check for a single KBL file
        } else if (selectedOperation.equals("Wiring Harness From KBL")) {
            isEnabled = selectedFiles.stream().anyMatch(file -> file.getName().endsWith(".kbl")); // Check for any KBL files
        }

        generateButton.setEnabled(isEnabled); // Enable or disable the button
    }
}
