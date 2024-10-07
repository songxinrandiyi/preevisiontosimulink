package preevisiontosimulink.proxy.connection;

import com.mathworks.engine.MatlabEngine;
import preevisiontosimulink.proxy.port.ISimulinkPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

// The SimulinkExternConnection class implements the ISimulinkConnection interface
public class SimulinkExternConnection implements ISimulinkConnection {
    // Instance variables representing the connection's properties
    private ISimulinkPort outPort;        // Output port of the connection
    private ISimulinkSystem parent;        // Parent Simulink system of the connection
    private String name;                   // Unique name of the connection
    private String subsystemName;          // Name of the subsystem where the connection is used
    private String portName;               // Name of the port in the subsystem
    private int direction;                 // Direction of the connection (0 for output, 1 for input)

    // Constructor to initialize the connection with necessary parameters
    public SimulinkExternConnection(ISimulinkPort outPort, String subsystemName, String portName, ISimulinkSystem parent,
                                  int direction) {
        this.subsystemName = subsystemName; // Initialize subsystem name
        this.portName = portName;           // Initialize port name
        // Construct the unique name using the parent port's name, subsystem name, and port name
        this.name = outPort.getParent().getName() + "_" + subsystemName + "_" + portName;
        this.outPort = outPort;             // Set the output port
        this.parent = parent;                // Set the parent system
        this.direction = direction;          // Set the direction of the connection
    }

    // Method to retrieve the parent system of this connection
    @Override
    public ISimulinkSystem getParent() {
        return parent;
    }

    // Method to generate the model in Simulink using MATLAB engine commands
    @Override
    public void generateModel(MatlabEngine matlab) {
        // Construct the destination block path for the connection in Simulink
        String destinationBlockPath = subsystemName + "/" + portName;
        // Get the source block path from the output port's parent
        String sourceBlockPath = outPort.getParent().getName() + "/" + outPort.getName();
        String parentPath = parent.getName(); // Initialize the parent path
        ISimulinkSystem currentParent = parent.getParent(); // Get the current parent system
        
        // Traverse up the hierarchy to build the full parent path
        while (currentParent != null) {
            parentPath = currentParent.getName() + "/" + parentPath; // Concatenate parent names
            currentParent = currentParent.getParent(); // Move to the next parent
        }

        // Determine the direction of the connection to add the appropriate line in Simulink
        if (direction == 0) { // If the direction is 0 (output)
            try {
                // Use MATLAB command to add a line from source to destination
                matlab.eval("add_line('" + parentPath + "', '" + sourceBlockPath + "', '" + destinationBlockPath
                        + "', 'autorouting', 'on')");
                System.out.println("Simulink relation generated: " + sourceBlockPath + " -> " + destinationBlockPath);
            } catch (Exception e) {
                e.printStackTrace(); // Print stack trace on error
            }
        } else { // If the direction is not 0 (input)
            try {
                // Use MATLAB command to add a line from destination to source
                matlab.eval("add_line('" + parentPath + "', '" + destinationBlockPath + "', '" + sourceBlockPath
                        + "', 'autorouting', 'on')");
                System.out.println("Simulink relation generated: " + destinationBlockPath + " -> " + sourceBlockPath);
            } catch (Exception e) {
                e.printStackTrace(); // Print stack trace on error
            }
        }
    }

    // Method to get the name of the connection
    @Override
    public String getName() {
        return name; // Return the unique name of the connection
    }
}
