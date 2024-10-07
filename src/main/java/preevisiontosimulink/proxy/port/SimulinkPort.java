package preevisiontosimulink.proxy.port;

import preevisiontosimulink.proxy.block.SimulinkBlock;

// Class representing a generic Simulink port
public class SimulinkPort implements ISimulinkPort {
    private String name;               // Name of the port
    private SimulinkBlock parent;      // The parent Simulink block to which this port belongs

    // Constructor to initialize the port with a unique name based on the index
    public SimulinkPort(int index, SimulinkBlock parent) {
        this.name = "" + index;        // Convert the index to a String for the port name
        this.parent = parent;          // Assign the parent Simulink block
    }

    // Method to get the name of the port
    @Override
    public String getName() {
        return name;                   // Return the name of the port
    }

    // Method to get the parent Simulink block of this port
    @Override
    public SimulinkBlock getParent() {
        return parent;                 // Return the parent Simulink block
    }
}
