package preevisiontosimulink.proxy.system;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.mathworks.engine.MatlabEngine;

import preevisiontosimulink.library.DCCurrentSource;
import preevisiontosimulink.library.InPort;
import preevisiontosimulink.library.LConnection;
import preevisiontosimulink.library.OutPort;
import preevisiontosimulink.library.RConnection;
import preevisiontosimulink.library.Resistor;
import preevisiontosimulink.library.VoltageSensor;
import preevisiontosimulink.pojo.KabelInformation;
import preevisiontosimulink.proxy.block.ISimulinkBlock;
import preevisiontosimulink.proxy.connection.ISimulinkConnection;
import preevisiontosimulink.proxy.port.Contact;
import preevisiontosimulink.util.SimulinkSubsystemHelper;

//Class representing a subsystem in Simulink
public class SimulinkSubsystem implements ISimulinkSystem {
	private static final String BLOCK_NAME = "Subsystem";
	private static final String BLOCK_PATH = "simulink/Ports & Subsystems/Subsystem";
	private ISimulinkSystem parent; // Reference to the parent system
	private String name;
	private static int num = 1;
	private SimulinkSubsystemType type;
	private Integer numOfPins = 0;
	private KabelInformation kblInformation = new KabelInformation();

	// Lists to manage connections, ports, blocks, relations, and sub-subsystems
	private List<LConnection> inConnections = new ArrayList<>();
	private List<RConnection> outConnections = new ArrayList<>();
	private List<InPort> inPorts = new ArrayList<>();
	private List<OutPort> outPorts = new ArrayList<>();
	private List<ISimulinkBlock> blockList = new ArrayList<>();
	private List<ISimulinkConnection> relationList = new ArrayList<>();
	private List<SimulinkSubsystem> subsystemList = new ArrayList<>();
	private List<Contact> contactPoints = new ArrayList<>();

	// Constructor for creating a new SimulinkSubsystem
	public SimulinkSubsystem(ISimulinkSystem parent, String name, SimulinkSubsystemType type) {
		this.parent = parent;
		if (name == null) {
			this.name = BLOCK_NAME + num;
		} else {
			this.name = name;
		}
		num++;
		this.type = type != null ? type : SimulinkSubsystemType.STECKER;
	}

	public KabelInformation getKabelInformation() {
		return kblInformation;
	}

	public void setKabelInformation(KabelInformation kblInformation) {
		this.kblInformation = kblInformation;
	}

	public Integer getNumOfPins() {
		return numOfPins;
	}

	public void setNumOfPins(Integer numOfPins) {
		this.numOfPins = numOfPins;
	}

	public void addNumOfPins() {
		this.numOfPins++;
	}

	// Retrieve contacts by the pin number
	public List<Contact> getContactsByPinNumber(Integer pinNumberFrom) {
		if (pinNumberFrom == null) {
			throw new IllegalArgumentException("Pin number cannot be null");
		}
		return contactPoints.stream().filter(contact -> pinNumberFrom.equals(contact.getPinNumberFrom()))
				.collect(Collectors.toList());
	}

	// Method to get the entire list of contacts
	public List<Contact> getContactPoints() {
		return new ArrayList<>(contactPoints); // Return a copy to protect the internal list
	}

	// Method to set the entire list of contacts
	public void setContactPoints(List<Contact> contactPoints) {
		if (contactPoints == null) {
			throw new IllegalArgumentException("Contact list cannot be null");
		}
		this.contactPoints = new ArrayList<>(contactPoints); // Make a copy to protect the internal list
	}

	// Method to add a contact
	public void addContact(Contact contact) {
		if (contact == null) {
			throw new IllegalArgumentException("Contact cannot be null");
		}
		contactPoints.add(contact);
	}

	// Method to remove the last contact
	public void removeLastContact() {
		if (contactPoints.isEmpty()) {
			throw new IllegalStateException("No contacts to remove");
		}
		contactPoints.remove(contactPoints.size() - 1);
	}

	public ISimulinkSystem getParent() {
		return parent;
	}

	public InPort addInPort(InPort port) {
		inPorts.add(port);
		return port;
	}

	public InPort getInPort(int index) {
		return inPorts.get(index);
	}

	public InPort getInPort(String name) {
		for (InPort port : inPorts) {
			if (port.getName().equals(name)) {
				return port;
			}
		}
		return null;
	}

	public int getInPortIndex(String name) {
		for (int i = 0; i < inPorts.size(); i++) {
			if (inPorts.get(i).getName().equals(name)) {
				return i + 1;
			}
		}
		// If the port with the given name is not found, return -1 or handle it as
		// needed
		return -1; // or throw new IllegalArgumentException("Input port not found: " + name);
	}

	public List<InPort> getInPorts() {
		return inPorts;
	}

	public OutPort addOutPort(OutPort port) {
		outPorts.add(port);
		return port;
	}

	public OutPort getOutPort(int index) {
		return outPorts.get(index);
	}

	public OutPort getOutPort(String name) {
		for (OutPort port : outPorts) {
			if (port.getName().equals(name)) {
				return port;
			}
		}
		return null;
	}

	public int getOutPortIndex(String name) {
		for (int i = 0; i < outPorts.size(); i++) {
			if (outPorts.get(i).getName().equals(name)) {
				return i + 1;
			}
		}
		// If the port with the given name is not found, return -1 or handle it as
		// needed
		return -1; // or throw new IllegalArgumentException("Input port not found: " + name);
	}

	public List<OutPort> getOutPorts() {
		return outPorts;
	}

	public LConnection addInConnection(LConnection port) {
		inConnections.add(port);
		return port;
	}

	public LConnection getInConnection(int index) {
		return inConnections.get(index);
	}

	public LConnection getInConnection(String name) {
		for (LConnection port : inConnections) {
			if (port.getName().equals(name)) {
				return port;
			}
		}
		return null;
	}

	public int getInConnectionIndex(String name) {
		for (int i = 0; i < inConnections.size(); i++) {
			if (inConnections.get(i).getName().equals(name)) {
				return i + 1;
			}
		}
		// If the port with the given name is not found, return -1 or handle it as
		// needed
		return -1; // or throw new IllegalArgumentException("Input port not found: " + name);
	}

	public List<LConnection> getInConnections() {
		return inConnections;
	}

	public RConnection addOutConnection(RConnection port) {
		outConnections.add(port);
		return port;
	}

	public RConnection getOutConnection(int index) {
		return outConnections.get(index);
	}

	public RConnection getOutConnection(String name) {
		for (RConnection port : outConnections) {
			if (port.getName().equals(name)) {
				return port;
			}
		}
		return null;
	}

	public int getOutConnectionIndex(String name) {
		for (int i = 0; i < outConnections.size(); i++) {
			if (outConnections.get(i).getName().equals(name)) {
				return i + 1;
			}
		}
		// If the port with the given name is not found, return -1 or handle it as
		// needed
		return -1; // or throw new IllegalArgumentException("Output port not found: " + name);
	}

	public List<RConnection> getOutConnections() {
		return outConnections;
	}

	public String getConnectionPath(String name) {
		for (int i = 0; i < inConnections.size(); i++) {
			if (inConnections.get(i).getName().equals(name)) {
				int n = i + 1;
				return "LConn" + n;
			}
		}
		for (int i = 0; i < outConnections.size(); i++) {
			if (outConnections.get(i).getName().equals(name)) {
				int n = i + 1;
				return "RConn" + n;
			}
		}
		return null;
	}

	public String getPortPath(String name) {
		for (int i = 0; i < inPorts.size(); i++) {
			if (inPorts.get(i).getName().equals(name)) {
				int n = i + 1;
				return "" + n;
			}
		}
		for (int i = 0; i < outPorts.size(); i++) {
			if (outPorts.get(i).getName().equals(name)) {
				int n = i + 1;
				return "" + n;
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ISimulinkConnection addRelation(ISimulinkConnection relation) {
		relationList.add(relation);
		return relation;
	}

	@Override
	public ISimulinkBlock getBlock(String name) {
		for (ISimulinkBlock block : blockList) {
			if (block.getName().equals(name)) {
				return block;
			}
		}
		return null;
	}

	public void generateModel(MatlabEngine matlab) {
		try {
			String combinedPath = SimulinkSubsystemHelper.generateCombinedPath(parent, name);

			matlab.eval("add_block('" + BLOCK_PATH + "', '" + combinedPath + "')");
			matlab.eval("delete_line('" + combinedPath + "','In1/1','Out1/1')");
			matlab.eval("delete_block('" + combinedPath + "/In1')");
			matlab.eval("delete_block('" + combinedPath + "/Out1')");

			// Generate the Simulink model for each subsystem in the subsystemList
			for (SimulinkSubsystem subsystem : subsystemList) {
				subsystem.generateModel(matlab);
			}

			if (this.type == SimulinkSubsystemType.STECKER) {
				for (VoltageSensor voltageSensor : getAllVoltageSensorBlocks()) {
					voltageSensor.setParameter("Orientation", "Right");
				}
			}

			// Generate the Simulink model for each block in the blockList
			for (ISimulinkBlock block : blockList) {
				block.generateModel(matlab);
			}

			// Generate the Simulink model for each LConnection in the inPorts
			for (LConnection port : inConnections) {
				port.generateModel(matlab);
			}

			// Generate the Simulink model for each RConnection in the outPorts
			for (RConnection port : outConnections) {
				port.generateModel(matlab);
			}

			// Generate the Simulink model for each port in the inPorts
			for (InPort port : inPorts) {
				port.generateModel(matlab);
			}

			// Generate the Simulink model for each port in the outPorts
			for (OutPort port : outPorts) {
				port.generateModel(matlab);
			}

			switch (this.type) {
			case STECKER:
				SimulinkSubsystemHelper.arrangeStecker(matlab, inConnections, combinedPath, name);
				break;
			case KABEL:
				SimulinkSubsystemHelper.arrangeKabel(matlab, inConnections, outConnections, combinedPath);
				break;
				// Add other cases if there are more types in SimulinkSubsystemType
			case THERMAL_KABEL:
				SimulinkSubsystemHelper.arrangeThermalKabel(matlab, inPorts, outPorts, combinedPath);
				break;
			default:
				// Handle unexpected types if necessary
				break;
			}

			// Generate the Simulink model for each relation in the relationList
			for (ISimulinkConnection relation : relationList) {
				relation.generateModel(matlab);
			}

			if (this.type != SimulinkSubsystemType.KABEL) {
				matlab.eval("Simulink.BlockDiagram.arrangeSystem('" + combinedPath + "')");
			}

			System.out.println("Simulink subsystem generated: " + combinedPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public SimulinkSubsystemType getType() {
		return type;
	}

	public void setType(SimulinkSubsystemType type) {
		this.type = type;
	}

	@Override
	public List<ISimulinkBlock> getBlockList() {
		return blockList;
	}

	@Override
	public List<ISimulinkConnection> getRelationList() {
		return relationList;
	}

	@Override
	public SimulinkSubsystem addSubsystem(SimulinkSubsystem subsystem) {
		subsystemList.add(subsystem);
		return subsystem;
	}

	@Override
	public ISimulinkBlock addBlock(ISimulinkBlock block) {
		blockList.add(block);
		return block;
	}

	@Override
	public SimulinkSubsystem getSubsystem(String name) {
		for (SimulinkSubsystem subsystem : subsystemList) {
			if (subsystem.getName().equals(name)) {
				return subsystem;
			}
		}
		return null;
	}

	@Override
	public ISimulinkConnection getRelation(String name) {
		for (ISimulinkConnection relation : relationList) {
			if (relation.getName().equals(name)) {
				return relation;
			}
		}
		return null;
	}

	@Override
	public List<Resistor> getAllResistorBlocks() {
		return blockList.stream().filter(block -> block instanceof Resistor).map(block -> (Resistor) block)
				.collect(Collectors.toList());
	}

	@Override
	public List<DCCurrentSource> getAllCurrentSourceBlocks() {
		return blockList.stream().filter(block -> block instanceof DCCurrentSource)
				.map(block -> (DCCurrentSource) block).collect(Collectors.toList());
	}

	@Override
	public List<VoltageSensor> getAllVoltageSensorBlocks() {
		return blockList.stream().filter(block -> block instanceof VoltageSensor).map(block -> (VoltageSensor) block)
				.collect(Collectors.toList());
	}

	@Override
	public List<SimulinkSubsystem> getSubsystemList(SimulinkSubsystemType type) {
		return subsystemList.stream().filter(subsystem -> subsystem.getType() == type).collect(Collectors.toList());
	}

	@Override
	public List<SimulinkSubsystem> getSubsystemsContainingString(String searchString) {
		return subsystemList.stream().filter(subsystem -> subsystem.getName().contains(searchString))
				.collect(Collectors.toList());
	}
}
