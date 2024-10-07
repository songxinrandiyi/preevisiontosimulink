package preevisiontosimulink.library;

import preevisiontosimulink.proxy.block.SimulinkBlock;
import preevisiontosimulink.proxy.block.SimulinkParameter;
import preevisiontosimulink.proxy.port.SimulinkPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

/**
 * Class representing a "Constant" block in Simulink.
 * It extends the base class SimulinkBlock and defines the specific behavior
 * for the Constant block.
 */
public class Constant extends SimulinkBlock {
    // Static variable to assign unique default names to each instance of the block.
    private static int num = 1;

    /**
     * Constructor for the Constant block.
     * @param parent The parent system where this block is added.
     * @param name The name of the block (optional, will auto-generate if null).
     */
    public Constant(ISimulinkSystem parent, String name) {
        super(parent, name);  // Call the parent class constructor.
    }

    /**
     * Initializes the block by setting block-specific properties such as its path,
     * output ports, and block parameters.
     */
    @Override
    public void initialize() {
        // Set the block name and path to the Simulink library.
        this.BLOCK_NAME = "Constant";
        this.BLOCK_PATH = "simulink/Sources/Constant";

        // If no name was provided, generate a default name using a counter.
        if (name == null) {
            this.name = BLOCK_NAME + num;
        }
        num++;  // Increment the counter for the next instance.

        // The Constant block typically has only an output port.
        this.outPorts.add(new SimulinkPort(1, this));  // Output port.

        // Initialize parameters specific to the Constant block.
        // The "Value" parameter holds the constant value to be output.
        this.parameters.add(new SimulinkParameter<Double>("Value", this));

        // The "Orientation" parameter may define the block's graphical orientation (e.g., vertical, horizontal).
        this.parameters.add(new SimulinkParameter<String>("Orientation", this));
    }
}
