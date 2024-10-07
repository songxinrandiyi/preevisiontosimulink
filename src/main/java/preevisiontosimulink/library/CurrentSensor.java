package preevisiontosimulink.library;

import preevisiontosimulink.proxy.block.SimulinkBlock;
import preevisiontosimulink.proxy.port.LConnectionPort;
import preevisiontosimulink.proxy.port.RConnectionPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

/**
 * Class representing a "Current Sensor" block in Simulink for electrical systems.
 * Extends the SimulinkBlock class and models the behavior of the block.
 */
public class CurrentSensor extends SimulinkBlock {
    // Static counter for generating unique default block names.
    private static int num = 1;

    /**
     * Constructor for the CurrentSensor block.
     * @param parent The parent system where the block is placed.
     * @param name The name of the block, optional (will auto-generate if null).
     */
    public CurrentSensor(ISimulinkSystem parent, String name) {
        super(parent, name);  // Call the superclass constructor.
    }

    /**
     * Initializes the block by setting the block name, Simulink path,
     * ports, and any other block-specific details.
     */
    @Override
    public void initialize() {
        // Set the block name and the Simulink library path.
        this.BLOCK_NAME = "CurrentSensor";
        this.BLOCK_PATH = "fl_lib/Electrical/Electrical Sensors/Current Sensor";

        // If no name was provided, generate a default name using the counter.
        if (name == null) {
            this.name = BLOCK_NAME + num;  // Assign name like "CurrentSensor1", "CurrentSensor2", etc.
        }
        num++;  // Increment the counter for unique default names.

        // Initialize the ports.
        // LConnectionPort represents the left-side electrical input port.
        this.inPorts.add(new LConnectionPort(1, this));

        // RConnectionPort represents the right-side electrical output ports.
        this.outPorts.add(new RConnectionPort(1, this));  // First output port.
        this.outPorts.add(new RConnectionPort(2, this));  // Second output port.
    }
}
