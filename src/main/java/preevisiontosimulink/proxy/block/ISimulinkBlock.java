package preevisiontosimulink.proxy.block;

import java.util.List;

import com.mathworks.engine.MatlabEngine;

import preevisiontosimulink.proxy.port.ISimulinkPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

// Interface representing a Simulink block in a system
public interface ISimulinkBlock extends ISimulinkParameterizable {
    
    // Get the name of the block
    String getName();

    // Set a new name for the block
    void setName(String name);

    // Add an input port to the block and return it
    ISimulinkPort addInPort(ISimulinkPort port);

    // Get the input port at the specified index
    ISimulinkPort getInPort(int index);

    // Get a list of all input ports for the block
    List<ISimulinkPort> getInPorts();

    // Add an output port to the block and return it
    ISimulinkPort addOutPort(ISimulinkPort port);

    // Get the output port at the specified index
    ISimulinkPort getOutPort(int index);

    // Get a list of all output ports for the block
    List<ISimulinkPort> getOutPorts();

    // Get the parent system of this block
    ISimulinkSystem getParent();

    void generateModel(MatlabEngine matlab);

    // Initialize the block, setting up necessary parameters or states
    void initialize();

    // Generate a combined path representation for the block
    String generateCombinedPath();
}
