package preevisiontosimulink.library;

import preevisiontosimulink.proxy.block.SimulinkBlock;
import preevisiontosimulink.proxy.block.SimulinkParameter;
import preevisiontosimulink.proxy.port.LConnectionPort;
import preevisiontosimulink.proxy.port.RConnectionPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

/**
 * Class representing a "DC Current Source" block in Simulink for electrical systems.
 * This class extends SimulinkBlock, inheriting its core functionality,
 * and defines specific behavior for the DC Current Source block.
 */
public class DCCurrentSource extends SimulinkBlock {
    // Static variable to track and generate unique default names for block instances.
    private static int num = 1;

    /**
     * Constructor for the DCCurrentSource block.
     * @param parent The parent system to which this block belongs.
     * @param name The name of the block, optional (if null, a default name is generated).
     */
    public DCCurrentSource(ISimulinkSystem parent, String name) {
        super(parent, name);  // Call the parent constructor.
    }

    /**
     * Initialize the block by setting its name, path, ports, and parameters.
     */
    @Override
    public void initialize() {
        // Set the block name and library path for the Simulink environment.
        this.BLOCK_NAME = "DCCurrentSource";
        this.BLOCK_PATH = "fl_lib/Electrical/Electrical Sources/DC Current Source";

        // If no name is provided, generate a default name like "DCCurrentSource1", "DCCurrentSource2", etc.
        if (name == null) {
            this.name = BLOCK_NAME + num;
        }
        num++;  // Increment the static counter for generating unique block names.

        // Initialize the ports.
        // The DC Current Source block usually has an input (left) port and an output (right) port.
        this.inPorts.add(new LConnectionPort(1, this));  // Left-side connection port.
        this.outPorts.add(new RConnectionPort(1, this)); // Right-side connection port.

        // Initialize parameters for the DC Current Source block.
        // "i0" represents the DC current value parameter (e.g., the current in amperes).
        this.parameters.add(new SimulinkParameter<Double>("i0", this));

        // The "Orientation" parameter could represent the visual orientation of the block (e.g., horizontal/vertical).
        this.parameters.add(new SimulinkParameter<String>("Orientation", this));
    }
}
