package preevisiontosimulink.ui;

import org.eclipse.swt.SWT; // Importing SWT for GUI components
import org.eclipse.swt.layout.GridData; // Layout for arranging components in a grid
import org.eclipse.swt.layout.GridLayout; // Layout for arranging components in a grid
import org.eclipse.swt.widgets.Button; // Button component
import org.eclipse.swt.widgets.Dialog; // Base class for dialog windows
import org.eclipse.swt.widgets.Display; // Display for managing the GUI display
import org.eclipse.swt.widgets.List; // List component for displaying multiple items
import org.eclipse.swt.widgets.Shell; // Window shell

import preevisiontosimulink.util.UIUtils; // Utility class for UI operations

// Class to create a selection dialog for choosing an insulation thickness
public class SelectionDialog extends Dialog {
    private Shell dialogShell; // Shell for the dialog window
    private List list; // List to display selectable items
    private Button selectButton; // Button to confirm selection
    private String selectedItem; // Variable to store the selected item

    // Constructor to initialize the dialog with a parent shell and items to select from
    public SelectionDialog(Shell parent, String[] items) {
        super(parent); // Call the superclass constructor
        this.dialogShell = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL); // Create the dialog shell
        this.dialogShell.setText("Select Insulation Thickness"); // Set the title of the dialog
        this.dialogShell.setLayout(new GridLayout()); // Set the layout of the shell to a grid

        // Create a list to display the items passed to the dialog
        this.list = new List(dialogShell, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
        list.setItems(items); // Set the items for the list
        list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true)); // Layout data for the list to fill the shell

        // Create a button for selecting an item from the list
        this.selectButton = new Button(dialogShell, SWT.PUSH);
        selectButton.setText("Select"); // Set button text
        selectButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false)); // Layout data for the button

        // Add a listener to handle button selection
        selectButton.addListener(SWT.Selection, event -> {
            int index = list.getSelectionIndex(); // Get the index of the selected item
            if (index != -1) { // Check if an item is selected
                selectedItem = list.getItem(index); // Store the selected item
                dialogShell.close(); // Close the dialog
            }
        });

        dialogShell.setSize(300, 200); // Set the size of the dialog
        UIUtils.centerWindow(dialogShell); // Center the dialog window on the screen
    }

    // Method to open the dialog and return the selected item
    public String open() {
        dialogShell.open(); // Open the dialog shell
        Display display = getParent().getDisplay(); // Get the display of the parent shell
        // Event loop to keep the dialog responsive
        while (!dialogShell.isDisposed()) {
            if (!display.readAndDispatch()) { // Process events
                display.sleep(); // Sleep if there are no events to process
            }
        }
        return selectedItem; // Return the selected item (null if none selected)
    }
}
