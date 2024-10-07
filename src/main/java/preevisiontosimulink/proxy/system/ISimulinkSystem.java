package preevisiontosimulink.proxy.system;

import java.util.List;

import preevisiontosimulink.library.DCCurrentSource;  // Import DCCurrentSource class
import preevisiontosimulink.library.Resistor;          // Import Resistor class
import preevisiontosimulink.library.VoltageSensor;     // Import VoltageSensor class
import preevisiontosimulink.proxy.block.ISimulinkBlock; // Import ISimulinkBlock interface
import preevisiontosimulink.proxy.connection.ISimulinkConnection; // Import ISimulinkConnection interface

// The ISimulinkSystem interface defines the operations that can be performed on a Simulink-like system
public interface ISimulinkSystem {
    
    // Method to retrieve the parent system of the current system
    ISimulinkSystem getParent();

    // Method to add a subsystem to the current system
    SimulinkSubsystem addSubsystem(SimulinkSubsystem subsystem);

    // Method to add a block to the current system
    ISimulinkBlock addBlock(ISimulinkBlock block);

    // Method to retrieve a block by its name
    ISimulinkBlock getBlock(String name);

    // Method to add a connection (relation) to the current system
    ISimulinkConnection addRelation(ISimulinkConnection relation);

    // Method to get a list of all blocks in the current system
    List<ISimulinkBlock> getBlockList();

    // Method to get a list of all connections (relations) in the current system
    List<ISimulinkConnection> getRelationList();

    // Method to get a list of subsystems of a specific type
    List<SimulinkSubsystem> getSubsystemList(SimulinkSubsystemType type);

    // Method to retrieve a subsystem by its name
    SimulinkSubsystem getSubsystem(String name);

    // Method to retrieve the name of the current system
    String getName();

    // Method to get a specific connection (relation) by its name
    ISimulinkConnection getRelation(String name);

    // Method to retrieve a list of all resistor blocks in the current system
    List<Resistor> getAllResistorBlocks();

    // Method to retrieve a list of all current source blocks in the current system
    List<DCCurrentSource> getAllCurrentSourceBlocks();

    // Method to retrieve a list of all voltage sensor blocks in the current system
    List<VoltageSensor> getAllVoltageSensorBlocks();

    // Method to retrieve a list of subsystems containing a specific string in their name
    List<SimulinkSubsystem> getSubsystemsContainingString(String searchString);
}
