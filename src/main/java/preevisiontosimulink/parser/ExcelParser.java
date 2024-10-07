package preevisiontosimulink.parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mathworks.engine.MatlabEngine;

import preevisiontosimulink.library.LConnection;
import preevisiontosimulink.library.Resistor;
import preevisiontosimulink.proxy.block.ISimulinkBlock;
import preevisiontosimulink.proxy.connection.SimulinkExternConnection;
import preevisiontosimulink.proxy.connection.SimulinkConnection;
import preevisiontosimulink.proxy.system.SimulinkSubsystem;
import preevisiontosimulink.proxy.system.SimulinkSubsystemType;
import preevisiontosimulink.proxy.system.SimulinkSystem;
import preevisiontosimulink.proxy.system.SimulinkSystemType;
import preevisiontosimulink.util.ExcelUtils;
import preevisiontosimulink.util.SimulinkSubsystemHelper;

public class ExcelParser {
    private SimulinkSystem system; // The Simulink system being created
    private String modelName; // The name of the model
    private List<String> filePaths = new ArrayList<>(); // List of file paths to Excel files

    // Constructor to initialize the ExcelParser with model name and file paths
    public ExcelParser(String modelName, List<String> filePaths) {
        this.modelName = modelName;
        this.filePaths = filePaths;
    }

    // Main method to generate the Simulink model
    public void generateModel() {
        // Initialize a new Simulink system with the specified model name
        system = new SimulinkSystem(modelName, SimulinkSystemType.WIRING_HARNESS, null);
        
        // Iterate over each file path provided
        for (String path : filePaths) {
            try (FileInputStream fis = new FileInputStream(path); 
                 Workbook workbook = new XSSFWorkbook(fis)) {

                // Loop through each sheet in the workbook
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    Sheet sheet = workbook.getSheetAt(i);
                    System.out.println("Sheet: " + sheet.getSheetName()); // Print sheet name
                    
                    // Generate blocks and connections from the sheet
                    generateBlocks(sheet);
                    generateConnections(sheet);
                    generateReplacement(); // Generate replacements (if necessary)
                }

            } catch (IOException e) {
                e.printStackTrace(); // Print stack trace for any IO exceptions
            }
        }
        
        // Start Matlab engine and generate the model
        try {
            MatlabEngine matlab = MatlabEngine.startMatlab();
            system.generateModel(matlab); // Generate the Simulink model in Matlab
            matlab.close(); // Close Matlab engine
        } catch (Exception e) {
            e.printStackTrace(); // Print stack trace for any exceptions
        }
    }

    // Method to generate blocks from the Excel sheet
    private void generateBlocks(Sheet sheet) {
        int rowBegin = 1; // Start from the second row (first row is usually headers)
        for (int i = rowBegin; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue; // Skip if row is null
            }

            // Extract relevant cell data for component 1
            Cell component1 = row.getCell(1);
            Cell pinnummer1 = row.getCell(3);
            Cell schematicPin1 = row.getCell(4);
            String pin1Name = replaceSlashWithUnderscore(
                    pinnummer1.getStringCellValue() + "_" + schematicPin1.getStringCellValue());
            String component1Name = component1.getStringCellValue();
            boolean hasComponent1 = !component1Name.isEmpty() && !pinnummer1.getStringCellValue().isEmpty()
                    && !schematicPin1.getStringCellValue().isEmpty();

            // Extract relevant cell data for component 2
            Cell component2 = row.getCell(10);
            Cell pinnummer2 = row.getCell(12);
            Cell schematicPin2 = row.getCell(13);
            String pin2Name = replaceSlashWithUnderscore(
                    pinnummer2.getStringCellValue() + "_" + schematicPin2.getStringCellValue());
            String component2Name = component2.getStringCellValue();
            boolean hasComponent2 = !component2Name.isEmpty() && !pinnummer2.getStringCellValue().isEmpty()
                    && !schematicPin2.getStringCellValue().isEmpty();

            // If both components exist, add them to the system
            if (hasComponent1 && hasComponent2) {
                // Check if the subsystem for component 1 exists; if not, create it
                if (system.getSubsystem(component1Name) == null) {
                    system.addSubsystem(new SimulinkSubsystem(system, component1Name, null));
                }
                SimulinkSubsystem subsystem1 = system.getSubsystem(component1Name);
                // Check if the pin connection exists; if not, add it
                if (subsystem1.getInConnection(pin1Name) == null) {
                    subsystem1.addInConnection(new LConnection(subsystem1, pin1Name));
                    SimulinkSubsystemHelper.reorderConnectionsForExcel(subsystem1); // Reorder connections if needed
                }

                // Check if the subsystem for component 2 exists; if not, create it
                if (system.getSubsystem(component2Name) == null) {
                    system.addSubsystem(new SimulinkSubsystem(system, component2Name, null));
                }
                SimulinkSubsystem subsystem2 = system.getSubsystem(component2Name);
                // Check if the pin connection exists; if not, add it
                if (subsystem2.getInConnection(pin2Name) == null) {
                    subsystem2.addInConnection(new LConnection(subsystem2, pin2Name));
                    SimulinkSubsystemHelper.reorderConnectionsForExcel(subsystem2);
                }
            }
        }
    }

    // Method to generate connections between blocks from the Excel sheet
    private void generateConnections(Sheet sheet) {
        int rowBegin = 1; // Start from the second row
        for (int i = rowBegin; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue; // Skip if row is null
            }

            // Extract relevant cell data for component 1
            Cell component1 = row.getCell(1);
            Cell pinnummer1 = row.getCell(3);
            Cell schematicPin1 = row.getCell(4);
            String pin1Name = replaceSlashWithUnderscore(
                    pinnummer1.getStringCellValue() + "_" + schematicPin1.getStringCellValue());
            String component1Name = component1.getStringCellValue();
            boolean hasComponent1 = !component1Name.isEmpty() && !pinnummer1.getStringCellValue().isEmpty()
                    && !schematicPin1.getStringCellValue().isEmpty();

            // Extract relevant cell data for component 2
            Cell component2 = row.getCell(10);
            Cell pinnummer2 = row.getCell(12);
            Cell schematicPin2 = row.getCell(13);
            String pin2Name = replaceSlashWithUnderscore(
                    pinnummer2.getStringCellValue() + "_" + schematicPin2.getStringCellValue());
            String component2Name = component2.getStringCellValue();
            boolean hasComponent2 = !component2Name.isEmpty() && !pinnummer2.getStringCellValue().isEmpty()
                    && !schematicPin2.getStringCellValue().isEmpty();

            // If both components exist, create a connection
            if (hasComponent1 && hasComponent2) {
                // Create a unique name for the block (Resistor) connecting the components
                String name = component1Name + "  " + pin1Name + "  " + component2Name + "  " + pin2Name;
                system.addBlock(new Resistor(system, name)); // Add a Resistor block
                double resistance = calculateResistance(row.getCell(21), row.getCell(20)); // Calculate resistance
                system.getBlock(name).setParameter("R", resistance); // Set resistance parameter for the block
                
                // Create relations for input and output ports
                String pin1Path = system.getSubsystem(component1Name).getConnectionPath(pin1Name);
                system.addRelation(new SimulinkExternConnection(system.getBlock(name).getInPort(0), component1Name,
                        pin1Path, system, 0));
                String pin2Path = system.getSubsystem(component2Name).getConnectionPath(pin2Name);
                system.addRelation(new SimulinkExternConnection(system.getBlock(name).getOutPort(0), component2Name,
                        pin2Path, system, 0));
            }
        }
    }

    // Method to generate replacements for certain subsystem types
    private void generateReplacement() {
        // Get all subsystems of type STECKER
        List<SimulinkSubsystem> subsystems = system.getSubsystemList(SimulinkSubsystemType.STECKER);
        for (SimulinkSubsystem subsystem : subsystems) {
            List<LConnection> inPorts = subsystem.getInConnections(); // Get all input connections
            if (inPorts != null) {
                for (LConnection inPort : inPorts) {
                    // Add a resistor block for each input connection
                    subsystem.addBlock(new Resistor(subsystem, inPort.getName() + "_R"));
                    subsystem.getBlock(inPort.getName() + "_R").setParameter("R", 3); // Set fixed resistance value
                    // Create a relation between the input connection and the resistor
                    subsystem.addRelation(new SimulinkConnection(inPort.getInPort(0),
                            subsystem.getBlock(inPort.getName() + "_R").getInPort(0), subsystem));
                }
                // If there are multiple input connections, connect them in series
                if (inPorts.size() > 1) {
                    for (int i = 0; i < inPorts.size() - 1; i++) {
                        LConnection endPort = inPorts.get(inPorts.size() - 1);
                        LConnection startPort = inPorts.get(i);
                        ISimulinkBlock endResistor = subsystem.getBlock(endPort.getName() + "_R");
                        ISimulinkBlock startResistor = subsystem.getBlock(startPort.getName() + "_R");
                        subsystem.addRelation(new SimulinkConnection(endResistor.getOutPort(0),
                                startResistor.getOutPort(0), subsystem));
                    }
                }
            }
        }
    }

    // Utility method to replace slashes with underscores in a string
    private String replaceSlashWithUnderscore(String input) {
        if (input == null) {
            return null; // Return null if input is null
        }
        return input.replace("/", "_"); // Replace slashes with underscores
    }

    // Method to print the cell value for debugging purposes
    private void printCellValue(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                System.out.print(cell.getStringCellValue() + "\t");
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    System.out.print(cell.getDateCellValue() + "\t");
                } else {
                    System.out.print(cell.getNumericCellValue() + "\t");
                }
                break;
            case BOOLEAN:
                System.out.print(cell.getBooleanCellValue() + "\t");
                break;
            default:
                System.out.print(" \t"); // Print empty space for other types
                break;
        }
    }

    // Method to calculate resistance based on length and cross-sectional area
    public double calculateResistance(double length, double crossSectionalArea) {
        // Convert cross-sectional area from mm² to m² (1 mm² = 1e-6 m²)
        double crossSectionalAreaM2 = crossSectionalArea * 1e-6;

        // Convert length from mm to meters (1 mm = 1e-3 m)
        double lengthM = length * 1e-3;

        // Calculate and return resistance using the formula R = ρ * (L / A)
        return 1.77e-8 * (lengthM / crossSectionalAreaM2); // ρ (rho) is the resistivity of copper in Ω·m
    }

    // Method to extract a double value from the middle part of a string
    private double extractMiddleDouble(String str) {
        // Split the string based on underscores
        String[] parts = str.split("_");

        // Check if the middle part exists
        if (parts.length < 3) {
            throw new IllegalArgumentException("String does not have a middle part: " + str);
        }

        // The middle part is at index 1
        String middlePart = parts[1];

        // Convert the middle part to a double
        try {
            return Double.parseDouble(middlePart);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid format for a double in the middle part: " + middlePart);
        }
    }

    // Overloaded method to calculate resistance using cell values
    private double calculateResistance(Cell cellLength, Cell cellCrossSectionalArea) {
        double length;
        double crossSectionalArea;

        // Extract length value from the cell
        if (cellLength.getCellType() == CellType.NUMERIC) {
            length = cellLength.getNumericCellValue();
        } else if (cellLength.getCellType() == CellType.STRING) {
            length = ExcelUtils.convertStringToDouble(cellLength.getStringCellValue());
        } else {
            // Handle other cell types or invalid values appropriately
            throw new IllegalArgumentException("Invalid cell type for length");
        }

        // Extract cross-sectional area value from the cell
        if (cellCrossSectionalArea.getCellType() == CellType.NUMERIC) {
            crossSectionalArea = cellCrossSectionalArea.getNumericCellValue();
        } else if (cellCrossSectionalArea.getCellType() == CellType.STRING) {
            crossSectionalArea = ExcelUtils.convertStringToDouble(cellCrossSectionalArea.getStringCellValue());
        } else {
            // Handle other cell types or invalid values appropriately
            throw new IllegalArgumentException("Invalid cell type for cross-sectional area");
        }

        return calculateResistance(length, crossSectionalArea); // Call the resistance calculation method
    }
}