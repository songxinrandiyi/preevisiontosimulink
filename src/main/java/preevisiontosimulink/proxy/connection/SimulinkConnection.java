package preevisiontosimulink.proxy.connection;

// Importing necessary classes
import com.mathworks.engine.MatlabEngine;
import preevisiontosimulink.proxy.port.ISimulinkPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

// Class representing a connection between Simulink ports
public class SimulinkConnection implements ISimulinkConnection {
    // Class variables
    private String name;           // Name of the connection
    private ISimulinkPort inPort;  // Input port of the connection
    private ISimulinkPort outPort; // Output port of the connection
    private ISimulinkSystem parent; // Parent system that holds the connection

    // Constructor to initialize the connection with output and input ports, and a parent system
    public SimulinkConnection(ISimulinkPort outPort, ISimulinkPort inPort, ISimulinkSystem parent) {
        // Create a name based on the names of the parent systems of the input and output ports
        this.name = outPort.getParent().getName() + "_" + inPort.getParent().getName();
        this.inPort = inPort;         // Assign the input port
        this.outPort = outPort;       // Assign the output port
        this.parent = parent;         // Assign the parent system
    }

    // Method to get the parent system of this connection
    @Override
    public ISimulinkSystem getParent() {
        return parent;
    }

    // Method to generate the model in Simulink using the MATLAB engine
    @Override
    public void generateModel(MatlabEngine matlab) {
        // Constructing paths for the source and destination blocks using their parent names and port names
        String sourceBlockPath = outPort.getParent().getName() + "/" + outPort.getName();
        String destinationBlockPath = inPort.getParent().getName() + "/" + inPort.getName();
        String parentPath = parent.getName(); // Initializing parent path with the parent's name
        ISimulinkSystem currentParent = parent.getParent(); // Getting the current parent

        // Building the full path for the parent system, going up the hierarchy
        while (currentParent != null) {
            parentPath = currentParent.getName() + "/" + parentPath; // Append current parent name to path
            currentParent = currentParent.getParent(); // Move up to the next parent
        }

        // Try-catch block to handle any exceptions during MATLAB execution
        try {
            // Evaluate MATLAB command to add a line (connection) between the source and destination blocks
            matlab.eval("add_line('" + parentPath + "', '" + sourceBlockPath + "', '" + destinationBlockPath
                    + "', 'autorouting', 'on')");
            // Log successful connection creation
            System.out.println("Simulink relation generated: " + sourceBlockPath + " -> " + destinationBlockPath);
        } catch (Exception e) {
            // Print stack trace if an exception occurs
            e.printStackTrace();
        }
    }

    // Method to get the name of the connection
    @Override
    public String getName() {
        return name;
    }
}
