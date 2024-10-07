package preevisiontosimulink.proxy.port;

import preevisiontosimulink.proxy.block.ISimulinkBlock;

// Interface representing a port in a Simulink block
public interface ISimulinkPort {
    
    // Method to get the name of the port
    String getName();

    // Method to get the parent Simulink block of this port
    ISimulinkBlock getParent();
}
