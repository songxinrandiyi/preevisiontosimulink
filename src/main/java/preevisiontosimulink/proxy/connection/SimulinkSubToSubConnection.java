package preevisiontosimulink.proxy.connection;

import com.mathworks.engine.MatlabEngine;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

// The SimulinkSubToSubConnection class implements the ISimulinkConnection interface
public class SimulinkSubToSubConnection implements ISimulinkConnection {
    // Instance variables to hold connection details
    private ISimulinkSystem parent;         // Parent Simulink system of this connection
    private String name;                    // Unique name for the connection
    private String firstSubsystemName;      // Name of the first subsystem
    private String firstPortName;           // Name of the port in the first subsystem
    private String secondSubsystemName;     // Name of the second subsystem
    private String secondPortName;          // Name of the port in the second subsystem

    // Constructor to initialize the connection with relevant subsystem and port names
    public SimulinkSubToSubConnection(String firstSubsystemName, String firstPortName, 
                                     String secondSubsystemName, String secondPortName,
                                     ISimulinkSystem parent) {
        this.firstSubsystemName = firstSubsystemName; // Initialize first subsystem name
        this.firstPortName = firstPortName;           // Initialize first port name
        this.secondSubsystemName = secondSubsystemName; // Initialize second subsystem name
        this.secondPortName = secondPortName;         // Initialize second port name
        // Create a unique name for the connection using the subsystem and port names
        this.name = firstSubsystemName + "_" + firstPortName + "_" + secondSubsystemName + "_" + secondPortName;
        this.parent = parent; // Set the parent system
    }

    // Method to get the parent system of this connection
    @Override
    public ISimulinkSystem getParent() {
        return parent; // Return the parent system
    }

    // Method to generate the model in Simulink using MATLAB engine commands
    @Override
    public void generateModel(MatlabEngine matlab) {
        // Construct the source and destination block paths for the connection
        String destinationBlockPath = secondSubsystemName + "/" + secondPortName;
        String sourceBlockPath = firstSubsystemName + "/" + firstPortName;
        
        String parentPath = parent.getName(); // Initialize the parent path
        ISimulinkSystem currentParent = parent.getParent(); // Get the current parent system
        
        // Traverse up the hierarchy to build the full parent path
        while (currentParent != null) {
            parentPath = currentParent.getName() + "/" + parentPath; // Concatenate parent names
            currentParent = currentParent.getParent(); // Move to the next parent
        }

        // Use MATLAB commands to add a line connecting the two subsystems
        try {
            matlab.eval("add_line('" + parentPath + "', '" + sourceBlockPath + "', '" + destinationBlockPath
                    + "', 'autorouting', 'on')");
            // Print a confirmation message to the console
            System.out.println("Simulink relation generated: " + sourceBlockPath + " -> " + destinationBlockPath);
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace on error
        }
    }

    // Method to retrieve the unique name of the connection
    @Override
    public String getName() {
        return name; // Return the unique name of the connection
    }
}
