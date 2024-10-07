package preevisiontosimulink.library;

import preevisiontosimulink.proxy.block.SimulinkBlock;
import preevisiontosimulink.proxy.block.SimulinkParameter;
import preevisiontosimulink.proxy.port.SimulinkPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

/**
 * Class representing an "Add" block in Simulink.
 * This class extends from SimulinkBlock, inheriting its core functionality
 * and adding specific behavior for the Add block.
 */
public class Add extends SimulinkBlock {
    // Static variable to track and increment the block instance number.
    private static int num = 1;

    /**
     * Constructor for Add block.
     * @param parent The parent system where this block is added.
     * @param name The name of the block (optional, generated if null).
     */
    public Add(ISimulinkSystem parent, String name) {
        super(parent, name);  // Calling the superclass constructor to set up the basic block.
    }

    /**
     * Initialize the block by setting its name, block path, ports, and parameters.
     */
    @Override
    public void initialize() {
        // Set the block name and Simulink block library path.
        this.BLOCK_NAME = "Add";
        this.BLOCK_PATH = "simulink/Math Operations/Add";

        // If no name was provided during object construction, generate a default name.
        if (name == null) {
            this.name = BLOCK_NAME + num;  // Default name is "Add" followed by an instance number.
        }
        num++;  // Increment the static counter to ensure unique default names.

        // Initialize the input and output ports for the Add block.
        // Add block generally has two input ports and one output port.
        this.inPorts.add(new SimulinkPort(1, this));  // Input port 1.
        this.inPorts.add(new SimulinkPort(2, this));  // Input port 2.
        this.outPorts.add(new SimulinkPort(1, this)); // Output port.

        // Initialize any parameters specific to this block.
        // In this case, the parameter "Inputs" might indicate the number of inputs for the Add block.
        this.parameters.add(new SimulinkParameter<String>("Inputs", this));
    }
}
