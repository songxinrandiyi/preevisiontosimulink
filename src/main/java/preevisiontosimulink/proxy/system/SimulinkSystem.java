package preevisiontosimulink.proxy.system;

// Required imports for file handling, MATLAB engine, and other functionalities
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mathworks.engine.MatlabEngine;

import preevisiontosimulink.library.DCCurrentSource;
import preevisiontosimulink.library.Resistor;
import preevisiontosimulink.library.VoltageSensor;
import preevisiontosimulink.proxy.block.ISimulinkBlock;
import preevisiontosimulink.proxy.connection.ISimulinkConnection;
import preevisiontosimulink.proxy.port.Contact;
import preevisiontosimulink.util.StringUtils;

public class SimulinkSystem implements ISimulinkSystem {
    private ISimulinkSystem parent = null; // Parent system reference
    private String name; // Name of the system
    private SimulinkSystemType type; // Type of the system (e.g., Wiring Harness)
    private String path = null; // Path for saving the model
    private List<ISimulinkBlock> blockList = new ArrayList<>(); // List of blocks in the system
    private List<ISimulinkConnection> relationList = new ArrayList<>(); // List of connections
    private List<SimulinkSubsystem> subsystemList = new ArrayList<>(); // List of subsystems

    // Constructor to initialize the SimulinkSystem
    public SimulinkSystem(String name, SimulinkSystemType type, String path) {
        this.name = name;
        this.type = type;
        this.path = path;
    }

    @Override
    public String getName() {
        return name; // Return the system name
    }

    @Override
    public ISimulinkBlock addBlock(ISimulinkBlock block) {
        blockList.add(block); // Add a block to the system
        return block;
    }

    @Override
    public ISimulinkConnection addRelation(ISimulinkConnection relation) {
        relationList.add(relation); // Add a relation to the system
        return relation;
    }

    @Override
    public ISimulinkBlock getBlock(String name) {
        // Retrieve a block by name from the block list
        for (ISimulinkBlock block : blockList) {
            if (block.getName().equals(name)) {
                return block;
            }
        }
        return null; // Return null if not found
    }

    public void generateModel(MatlabEngine matlab) {
        try {
            // Generate the Simulink model in MATLAB
            matlab.eval("new_system('" + name + "', 'Model')");

            // Generate models for each subsystem in the subsystem list
            for (SimulinkSubsystem subsystem : subsystemList) {
                if (type == SimulinkSystemType.WIRING_HARNESS) {
                    // Only generate if contact points exist
                    if (subsystem.getContactPoints() != null && subsystem.getContactPoints().size() > 0) {
                        subsystem.generateModel(matlab);
                    }
                } else {
                    subsystem.generateModel(matlab); // Generate for other types
                }
            }

            // Generate the model for each block in the block list
            for (ISimulinkBlock block : blockList) {
                block.generateModel(matlab);
            }

            // Generate the model for each relation in the relation list
            for (ISimulinkConnection relation : relationList) {
                relation.generateModel(matlab);
            }

            // Log info if the system type is Wiring Harness
            if (this.type != null && this.type == SimulinkSystemType.WIRING_HARNESS) {
                logInfo();
            }

            // Arrange the blocks in the Simulink model
            matlab.eval("Simulink.BlockDiagram.arrangeSystem('" + name + "')");

            // Set the stop time for the simulation
            matlab.eval("set_param('" + name + "', 'StopTime', '400')");

            String modelFolderPath = "simulink/"; // Default folder path for saving the model
            if (path != null) {
                modelFolderPath += path + "/"; // Append additional path if provided
            }

            Path simulinkDir = Paths.get(modelFolderPath); // Create a path object
            if (!Files.exists(simulinkDir)) {
                Files.createDirectories(simulinkDir); // Create directories if they don't exist
            }

            // Save the model
            String modelFilePath = modelFolderPath + name + ".slx";
            matlab.eval("save_system('" + name + "', '" + modelFilePath + "')");

            System.out.println("Simulink model generated: " + name); // Log generation success

            // Close the MATLAB engine (commented out, can be called when needed)
            // matlab.close();
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
        }
    }

    public void logInfo() {
        // Log information about the system's connectors and their contact points
        String firstPartOfSystemName = StringUtils.getFirstPart(this.name); // Get the first part of the system name
        String logFileName = firstPartOfSystemName + "_log.txt"; // Log file name
        StringBuilder logContent = new StringBuilder(); // StringBuilder for log content

        // Iterate through each subsystem of type STECKER
        for (SimulinkSubsystem subsystem : getSubsystemList(SimulinkSubsystemType.STECKER)) {
            if (subsystem.getContactPoints() != null && subsystem.getContactPoints().size() > 0) {
                System.out.println("Connector: " + subsystem.getName());
                logContent.append("Connector: ").append(subsystem.getName()).append("\n");

                // Iterate through each pin in the subsystem
                for (int i = 1; i < subsystem.getNumOfPins() + 1; i++) {
                    System.out.println("Pin " + i + " has number of contact points: "
                            + subsystem.getContactsByPinNumber(i).size());
                    logContent.append("Pin ").append(i).append(" has number of contact points: ")
                            .append(subsystem.getContactsByPinNumber(i).size()).append("\n");

                    // Iterate through each contact point for the pin
                    for (Contact contact : subsystem.getContactsByPinNumber(i)) {
                        System.out.println(contact.getName() + ", Pin " + contact.getPinNumberTo());
                        logContent.append(contact.getName()).append(", Pin ").append(contact.getPinNumberTo())
                                .append("\n");
                    }
                    System.out.println(); // Print an empty line for formatting
                    logContent.append("\n"); // Append a new line to log content
                }
                System.out.println(); // Print an empty line for formatting
                System.out.println(); // Print an empty line for formatting
                logContent.append("\n\n"); // Append two new lines to log content
            }
        }

        // Write log content to a file, overwriting if it exists
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, false))) {
            writer.write(logContent.toString()); // Write the log content to the file
        } catch (IOException e) {
            e.printStackTrace(); // Print stack trace for any IO exceptions
        }
    }

    @Override
    public List<ISimulinkBlock> getBlockList() {
        return blockList; // Return the list of blocks
    }

    @Override
    public List<ISimulinkConnection> getRelationList() {
        return relationList; // Return the list of connections
    }

    @Override
    public SimulinkSubsystem addSubsystem(SimulinkSubsystem subsystem) {
        subsystemList.add(subsystem); // Add a subsystem to the list
        return subsystem;
    }

    @Override
    public ISimulinkSystem getParent() {
        return parent; // Return the parent system
    }

    @Override
    public List<SimulinkSubsystem> getSubsystemList(SimulinkSubsystemType type) {
        // Filter and return the list of subsystems by type
        return subsystemList.stream().filter(subsystem -> subsystem.getType() == type).collect(Collectors.toList());
    }

    @Override
    public SimulinkSubsystem getSubsystem(String name) {
        // Retrieve a subsystem by name from the subsystem list
        for (SimulinkSubsystem subsystem : subsystemList) {
            if (subsystem.getName().equals(name)) {
                return subsystem;
            }
        }
        return null; // Return null if not found
    }

    @Override
    public ISimulinkConnection getRelation(String name) {
        // Retrieve a relation by name from the relation list
        for (ISimulinkConnection relation : relationList) {
            if (relation.getName().equals(name)) {
                return relation;
            }
        }
        return null; // Return null if not found
    }

    @Override
    public List<Resistor> getAllResistorBlocks() {
        // Filter and return all Resistor blocks from the block list
        return blockList.stream().filter(block -> block instanceof Resistor).map(block -> (Resistor) block)
                .collect(Collectors.toList());
    }

    @Override
    public List<DCCurrentSource> getAllCurrentSourceBlocks() {
        // Filter and return all DCCurrentSource blocks from the block list
        return blockList.stream().filter(block -> block instanceof DCCurrentSource)
                .map(block -> (DCCurrentSource) block).collect(Collectors.toList());
    }

    @Override
    public List<VoltageSensor> getAllVoltageSensorBlocks() {
        // Filter and return all VoltageSensor blocks from the block list
        return blockList.stream().filter(block -> block instanceof VoltageSensor).map(block -> (VoltageSensor) block)
                .collect(Collectors.toList());
    }

    @Override
    public List<SimulinkSubsystem> getSubsystemsContainingString(String searchString) {
        // Filter and return all subsystems that contain the search string in their name
        return subsystemList.stream().filter(subsystem -> subsystem.getName().contains(searchString))
                .collect(Collectors.toList());
    }

    // Method to find a subsystem with specific contact points
    public SimulinkSubsystem findSubsystemWithContactPoints(Contact leftContactPoint, Contact rightContactPoint) {
        // Check subsystems of type KABEL
        for (SimulinkSubsystem subsystem : getSubsystemList(SimulinkSubsystemType.KABEL)) {
            // Check if the subsystem has exactly two contact points
            if (subsystem.getContactPoints().size() == 2) {
                // Check if the contact points match in either order
                if (subsystem.getContactPoints().get(0).equals(leftContactPoint)
                        && subsystem.getContactPoints().get(1).equals(rightContactPoint)) {
                    return subsystem; // Return the matching subsystem
                } else if (subsystem.getContactPoints().get(0).equals(rightContactPoint)
                        && subsystem.getContactPoints().get(1).equals(leftContactPoint)) {
                    return subsystem; // Return the matching subsystem
                }
            }
        }
        return null; // Return null if no matching subsystem is found
    }
}
