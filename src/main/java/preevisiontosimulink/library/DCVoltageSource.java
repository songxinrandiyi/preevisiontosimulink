package preevisiontosimulink.library;

import preevisiontosimulink.proxy.block.SimulinkBlock;
import preevisiontosimulink.proxy.block.SimulinkParameter;
import preevisiontosimulink.proxy.port.LConnectionPort;
import preevisiontosimulink.proxy.port.RConnectionPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

/**
 * Class representing a "DC Voltage Source" block in Simulink.
 * This class defines the behavior for a constant DC voltage source.
 */
public class DCVoltageSource extends SimulinkBlock {
    // Static counter to generate unique default names for instances of the block.
    private static int num = 1;

    /**
     * Constructor for the DCVoltageSource block.
     * @param parent The parent system to which this block belongs.
     * @param name The name of the block, can be null (in which case a default name will be generated).
     */
    public DCVoltageSource(ISimulinkSystem parent, String name) {
        super(parent, name);  // Call the parent constructor.
    }

    /**
     * Initializes the block, setting the block name, library path, ports, and parameters.
     */
    @Override
    public void initialize() {
        // Set the block's name and its corresponding path in the Simulink library.
        this.BLOCK_NAME = "DCVoltageSource";
        this.BLOCK_PATH = "fl_lib/Electrical/Electrical Sources/DC Voltage Source";

        // If no name is provided, generate a unique default name (e.g., "DCVoltageSource1").
        if (name == null) {
            this.name = BLOCK_NAME + num;
        }
        num++;  // Increment the counter for generating unique names.

        // Initialize input and output ports.
        // DC Voltage Source typically has an input (left) connection and an output (right) connection.
        this.inPorts.add(new LConnectionPort(1, this));  // Left-side connection port.
        this.outPorts.add(new RConnectionPort(1, this)); // Right-side connection port.

        // Initialize parameters.
        // The "v0" parameter represents the DC voltage value of the source.
        this.parameters.add(new SimulinkParameter<Double>("v0", this));
    }
}
