package preevisiontosimulink.proxy.block;

import java.util.ArrayList;
import java.util.List;

import com.mathworks.engine.MatlabEngine;

import preevisiontosimulink.proxy.port.ISimulinkPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

// Class representing a Simulink block, implementing the ISimulinkBlock interface
public class SimulinkBlock implements ISimulinkBlock {
    
    // Name of the block
    protected String name;
    
    // Lists to hold input and output ports
    protected List<ISimulinkPort> inPorts;
    protected List<ISimulinkPort> outPorts;
    
    // List to hold parameters associated with this block
    protected List<SimulinkParameter<?>> parameters;
    
    // Reference to the parent system of this block
    protected ISimulinkSystem parent;
    
    // Placeholder strings for block name and path
    protected String BLOCK_NAME = "";
    protected String BLOCK_PATH = "";

    // Constructor initializing the block with a name and its parent system
    public SimulinkBlock(ISimulinkSystem parent, String name) {
        this.name = name;
        this.parent = parent;
        this.inPorts = new ArrayList<>(); // Initialize input ports list
        this.outPorts = new ArrayList<>(); // Initialize output ports list
        this.parameters = new ArrayList<>(); // Initialize parameters list
        this.initialize(); // Initialize the block
    }

    @Override
    public ISimulinkPort addInPort(ISimulinkPort port) {
        inPorts.add(port); // Add input port to the list
        return port;
    }

    @Override
    public ISimulinkPort getInPort(int index) {
        return inPorts.get(index); // Return the input port at the specified index
    }

    @Override
    public List<ISimulinkPort> getInPorts() {
        return inPorts; // Return the list of input ports
    }

    @Override
    public ISimulinkPort addOutPort(ISimulinkPort port) {
        outPorts.add(port); // Add output port to the list
        return port;
    }

    @Override
    public ISimulinkPort getOutPort(int index) {
        return outPorts.get(index); // Return the output port at the specified index
    }

    @Override
    public List<ISimulinkPort> getOutPorts() {
        return outPorts; // Return the list of output ports
    }

    @Override
    public String getName() {
        return name; // Return the name of the block
    }

    @Override
    public void generateModel(MatlabEngine matlab) {
        try {
            String combinedPath = generateCombinedPath(); // Generate the path for the block
            matlab.eval("add_block('" + BLOCK_PATH + "', '" + combinedPath + "')"); // Add the block to MATLAB

            System.out.println("Simulink block generated: " + combinedPath);
            // Set parameters for the block in MATLAB
            for (SimulinkParameter<?> param : getParameters()) {
                if (param.getValue() != null) {
                    matlab.eval("set_param('" + combinedPath + "', '" + param.getName() + "', '"
                            + param.getValue().toString() + "')");

                    System.out.println("set_param('" + combinedPath + "', '" + param.getName() + "', '"
                            + param.getValue().toString() + "')");
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace in case of an error
        }
    }

    @Override
    public <T> void addParameter(SimulinkParameter<T> parameter) {
        parameters.add(parameter); // Add a new parameter to the block
    }

    @Override
    public <T> void setParameter(String name, T value) {
        boolean found = false; // Flag to check if the parameter was found
        // Search for the parameter by name and set its value
        for (SimulinkParameter<?> parameter : parameters) {
            if (parameter.getName().equals(name)) {
                @SuppressWarnings("unchecked") // Suppress warnings for type casting
                SimulinkParameter<T> typedParameter = (SimulinkParameter<T>) parameter;
                typedParameter.setValue(value);
                found = true; // Mark as found
                break;
            }
        }

        if (!found) {
            // Provide feedback when the parameter name is not found
            System.out.println("Parameter with name '" + name + "' not found in the SimulinkBlock.");
        }
    }

    @Override
    public List<SimulinkParameter<?>> getParameters() {
        return parameters; // Return the list of parameters
    }

    @Override
    public void initialize() {
        // Initialization logic can be added here
    }

    @Override
    public ISimulinkSystem getParent() {
        return parent; // Return the parent system of this block
    }

    @Override
    public String generateCombinedPath() {
        // Build the combined path for this block based on its parent systems
        StringBuilder pathBuilder = new StringBuilder(name);
        ISimulinkSystem currentParent = parent;
        while (currentParent != null) {
            pathBuilder.insert(0, currentParent.getName() + "/"); // Prepend parent names to the path
            currentParent = currentParent.getParent(); // Move up to the parent system
        }
        return pathBuilder.toString(); // Return the full path
    }

    @Override
    public void setName(String name) {
        this.name = name; // Set a new name for the block
    }
}
