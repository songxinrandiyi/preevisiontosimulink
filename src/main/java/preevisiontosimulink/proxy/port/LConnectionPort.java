package preevisiontosimulink.proxy.port;

import preevisiontosimulink.proxy.block.SimulinkBlock;

// Class representing a connection port in a Simulink block 
public class LConnectionPort implements ISimulinkPort {
    private String name;               // Name of the connection port
    private SimulinkBlock parent;      // The parent Simulink block to which this port belongs

    // Constructor to initialize the port with a unique name based on the index
    public LConnectionPort(int index, SimulinkBlock parent) {
        this.name = "LConn" + index;   // Set the name as "LConn" followed by the index
        this.parent = parent;           // Assign the parent Simulink block
    }

    // Method to get the name of the connection port
    @Override
    public String getName() {
        return name;
    }

    // Method to get the parent Simulink block of this port
    @Override
    public SimulinkBlock getParent() {
        return parent;
    }
}
