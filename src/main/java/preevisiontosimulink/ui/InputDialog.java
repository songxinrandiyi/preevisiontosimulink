package preevisiontosimulink.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import preevisiontosimulink.util.UIUtils;

// Class representing a dialog for user input
public class InputDialog extends Dialog {
    private Shell dialogShell; // Shell for the dialog window
    private Text inputText; // Text field for user input
    private Button acceptButton; // Button to accept the input
    private double inputValue; // Variable to store the accepted input value

    // Constructor to initialize the dialog with a parent shell
    public InputDialog(Shell parent) {
        super(parent); // Call the superclass constructor

        // Create a new shell for the dialog with specified styles
        this.dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
        this.dialogShell.setText("Input Current Value"); // Set the dialog title
        this.dialogShell.setLayout(new GridLayout(2, false)); // Set layout for dialog

        // Label for the input field
        new org.eclipse.swt.widgets.Label(dialogShell, SWT.NONE).setText("Current (A):");
        // Text input field for the user to enter a current value
        this.inputText = new Text(dialogShell, SWT.BORDER);
        inputText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false)); // Set layout data

        // Button for accepting the input
        this.acceptButton = new Button(dialogShell, SWT.PUSH);
        acceptButton.setText("Accept"); // Set button text
        acceptButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1)); // Set layout data

        // Listener for the button click event
        acceptButton.addListener(SWT.Selection, event -> {
            try {
                // Parse the input text to a double
                inputValue = Double.parseDouble(inputText.getText());
                dialogShell.close(); // Close the dialog on successful input
            } catch (NumberFormatException e) {
                // Handle invalid input by clearing the text field
                inputText.setText(""); // Clear the text field on invalid input
            }
        });

        // Set the size of the dialog window
        dialogShell.setSize(300, 150);
        UIUtils.centerWindow(dialogShell); // Center the dialog on the parent window
    }

    // Method to open the dialog and wait for user interaction
    public Double open() {
        dialogShell.open(); // Open the dialog shell
        Display display = getParent().getDisplay(); // Get the display of the parent shell
        // Event loop to keep the dialog open until it is disposed
        while (!dialogShell.isDisposed()) {
            // Process events
            if (!display.readAndDispatch()) {
                display.sleep(); // Sleep if there are no events to process
            }
        }
        return inputValue; // Return the input value (or 0.0 if not set)
    }
}
