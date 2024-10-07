package preevisiontosimulink.util;

import java.util.List;

import preevisiontosimulink.library.Constant;
import preevisiontosimulink.library.CurrentSensor;
import preevisiontosimulink.library.Display;
import preevisiontosimulink.library.ElectricalReference;
import preevisiontosimulink.library.Gain;
import preevisiontosimulink.library.InPort;
import preevisiontosimulink.library.Integrator;
import preevisiontosimulink.library.LConnection;
import preevisiontosimulink.library.MathFunction;
import preevisiontosimulink.library.OutPort;
import preevisiontosimulink.library.PSSimulinkConverter;
import preevisiontosimulink.library.Product;
import preevisiontosimulink.library.RConnection;
import preevisiontosimulink.library.Resistor;
import preevisiontosimulink.library.Scope;
import preevisiontosimulink.library.Subtract;
import preevisiontosimulink.library.Sum;
import preevisiontosimulink.library.VoltageSensor;
import preevisiontosimulink.parser.kblelements.Cavity;
import preevisiontosimulink.parser.kblelements.Connection;
import preevisiontosimulink.parser.kblelements.ConnectorHousing;
import preevisiontosimulink.parser.kblelements.ConnectorOccurrence;
import preevisiontosimulink.parser.kblelements.GeneralWire;
import preevisiontosimulink.parser.kblelements.GeneralWireOccurrence;
import preevisiontosimulink.proxy.block.ISimulinkBlock;
import preevisiontosimulink.proxy.block.SimulinkBlock;
import preevisiontosimulink.proxy.connection.SimulinkConnection;
import preevisiontosimulink.proxy.connection.SimulinkSubToSubConnection;
import preevisiontosimulink.proxy.port.Contact;
import preevisiontosimulink.proxy.system.SimulinkSubsystem;
import preevisiontosimulink.proxy.system.SimulinkSubsystemType;
import preevisiontosimulink.proxy.system.SimulinkSystem;

public class SimulinkInitHelper {
	public static void initStecker(SimulinkSubsystem subsystem) {
		SimulinkSubsystemHelper.reorderConnections(subsystem);
		subsystem.addBlock(new ElectricalReference(subsystem, subsystem.getName() + "_E"));

		List<LConnection> inConnections = subsystem.getInConnections();
		if (inConnections != null) {
			for (LConnection inPort : inConnections) {
				subsystem.addBlock(new CurrentSensor(subsystem, inPort.getName() + "_I"));
				subsystem.addBlock(new PSSimulinkConverter(subsystem, inPort.getName() + "_PS"));
				subsystem.addBlock(new Scope(subsystem, inPort.getName() + "_Display"));

				subsystem.addRelation(new SimulinkConnection(inPort.getInPort(0),
						subsystem.getBlock(inPort.getName() + "_I").getInPort(0), subsystem));
				subsystem.addRelation(new SimulinkConnection(subsystem.getBlock(inPort.getName() + "_I").getOutPort(1),
						subsystem.getBlock(subsystem.getName() + "_E").getInPort(0), subsystem));
				subsystem.addRelation(new SimulinkConnection(subsystem.getBlock(inPort.getName() + "_I").getOutPort(0),
						subsystem.getBlock(inPort.getName() + "_PS").getInPort(0), subsystem));
				subsystem.addRelation(new SimulinkConnection(subsystem.getBlock(inPort.getName() + "_PS").getOutPort(0),
						subsystem.getBlock(inPort.getName() + "_Display").getInPort(0), subsystem));
			}
		}
	}

	public static void initCabel(SimulinkSubsystem subsystem, Double resistance) {
		LConnection inPort = subsystem.addInConnection(new LConnection(subsystem, "in1"));
		RConnection outPort = subsystem.addOutConnection(new RConnection(subsystem, "out1"));

		subsystem.addBlock(new Resistor(subsystem, "R"));
		ISimulinkBlock resistor = subsystem.getBlock("R");
		resistor.setParameter("R", resistance);

		subsystem.addBlock(new VoltageSensor(subsystem, "U"));
		ISimulinkBlock voltageSensor = subsystem.getBlock("U");

		subsystem.addBlock(new PSSimulinkConverter(subsystem, "PS"));
		ISimulinkBlock converter = subsystem.getBlock("PS");

		subsystem.addBlock(new Scope(subsystem, "Display"));
		ISimulinkBlock display = subsystem.getBlock("Display");

		subsystem.addRelation(new SimulinkConnection(inPort.getInPort(0), resistor.getInPort(0), subsystem));

		subsystem.addRelation(new SimulinkConnection(resistor.getOutPort(0), outPort.getInPort(0), subsystem));

		subsystem.addRelation(new SimulinkConnection(resistor.getOutPort(0), voltageSensor.getOutPort(1), subsystem));
		subsystem.addRelation(new SimulinkConnection(resistor.getInPort(0), voltageSensor.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(voltageSensor.getOutPort(0), converter.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(converter.getOutPort(0), display.getInPort(0), subsystem));
	}

	public static void initThermalKabel(SimulinkSubsystem subsystem, Connection connection) {
		InPort inPort = subsystem.addInPort(new InPort(subsystem, "in1"));
		OutPort outPort = subsystem.addOutPort(new OutPort(subsystem, "out1"));
		
		ISimulinkBlock square =  subsystem.addBlock(new MathFunction(subsystem, "Square"));
		ISimulinkBlock resistance =  subsystem.addBlock(new Gain(subsystem, "Resistance"));
		ISimulinkBlock powerDiff =  subsystem.addBlock(new Sum(subsystem, "PowerDiff"));
		ISimulinkBlock thermalCapacityInverse =  subsystem.addBlock(new Gain(subsystem, "ThermalCapacity"));
		ISimulinkBlock integrator =  subsystem.addBlock(new Integrator(subsystem, "Integrator"));
		
		ISimulinkBlock environmentTemperature =  subsystem.addBlock(new Constant(subsystem, "EnvironmentTemperature"));
		ISimulinkBlock temperatureDiff =  subsystem.addBlock(new Subtract(subsystem, "TemperatureDiff"));
		ISimulinkBlock thermalResistance =  subsystem.addBlock(new Constant(subsystem, "ThermalResistance"));
		ISimulinkBlock inverse =  subsystem.addBlock(new Product(subsystem, "Inverse"));
		ISimulinkBlock product =  subsystem.addBlock(new Product(subsystem, "Product"));
		
		temperatureDiff.setParameter("Orientation", "left");
		environmentTemperature.setParameter("Orientation", "left");
		thermalResistance.setParameter("Orientation", "left");
		inverse.setParameter("Orientation", "left");
		product.setParameter("Orientation", "left");

		resistance.setParameter("Gain", CalculatorUtils.calculateResistance(connection.getCurrent(), 
				connection.getCrossSectionArea(), connection.getMaterial()));
		thermalCapacityInverse.setParameter("Gain", 1/CalculatorUtils.calculateThermalCapacity(connection.getCrossSectionArea(), 
				connection.getThicknessIso() ,connection.getMaterial()));
		double radiusWire = CalculatorUtils.calculateRadiusFromCrossSectionArea(connection.getCrossSectionArea());
		thermalResistance.setParameter("Value", CalculatorUtils.calculateTotalThermalResistance(radiusWire, 
				radiusWire + connection.getThicknessIso()));
		
		integrator.setParameter("InitialCondition", 45);
		environmentTemperature.setParameter("Value", 45);
		powerDiff.setParameter("Inputs", "|+-");
		inverse.setParameter("Inputs", "/");
		square.setParameter("Operator", "square");
		
		subsystem.addRelation(new SimulinkConnection(inPort.getOutPort(0), square.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(square.getOutPort(0), resistance.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(resistance.getOutPort(0), powerDiff.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(powerDiff.getOutPort(0), thermalCapacityInverse.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(thermalCapacityInverse.getOutPort(0), integrator.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(integrator.getOutPort(0), outPort.getInPort(0), subsystem));
		
		subsystem.addRelation(new SimulinkConnection(integrator.getOutPort(0), temperatureDiff.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(environmentTemperature.getOutPort(0), 
				temperatureDiff.getInPort(1), subsystem));
		subsystem.addRelation(new SimulinkConnection(temperatureDiff.getOutPort(0), product.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(thermalResistance.getOutPort(0), inverse.getInPort(0), subsystem));
		subsystem.addRelation(new SimulinkConnection(inverse.getOutPort(0), product.getInPort(1), subsystem));
		subsystem.addRelation(new SimulinkConnection(product.getOutPort(0), powerDiff.getInPort(1), subsystem));
	}

	public static void initConnection(Connection connection, SimulinkSystem system, SimulinkSubsystem subsystem,
			String name, SimulinkSubsystemType type) {
		String startConnectorName;
		String endConnectorName;
		Integer startPin = connection.getStartPin();
		Integer endPin = connection.getEndPin();
		switch (type) {
		case KABEL:
			startConnectorName = connection.getStartConnector().getLargeId();
			endConnectorName = connection.getEndConnector().getLargeId();
			break;
		case THERMAL_KABEL:
			startConnectorName = connection.getStartConnectorName();
			endConnectorName = connection.getEndConnectorName();
			break;
		default:
			startConnectorName = connection.getStartConnector().getLargeId();
			endConnectorName = connection.getEndConnector().getLargeId();
			break;
		}
		SimulinkSubsystem startStecker = system.getSubsystem(startConnectorName);
		SimulinkSubsystem endStecker = system.getSubsystem(endConnectorName);
		
		String pathStart;
		String pathEnd;
		SimulinkSubToSubConnection startRelation;
		SimulinkSubToSubConnection endRelation;
		switch (type) {
		case KABEL:
			pathStart = startConnectorName + "_" + startStecker.getConnectionPath(startPin.toString()) + "_" + name
					+ "_LConn1";
			pathEnd = endConnectorName + "_" + endStecker.getConnectionPath(endPin.toString()) + "_" + name
					+ "_RConn1";
			startRelation = new SimulinkSubToSubConnection(startConnectorName,
					startStecker.getConnectionPath(startPin.toString()), name, "LConn1", system);
			endRelation = new SimulinkSubToSubConnection(endConnectorName,
					endStecker.getConnectionPath(endPin.toString()), name, "RConn1", system);
			break;
		case THERMAL_KABEL:
			pathStart = startConnectorName + "_" + startStecker.getPortPath(startPin.toString()) + "_" + name + "_1";
			pathEnd = endConnectorName + "_" + endStecker.getPortPath(endPin.toString()) + "_" + name + "_1";
			startRelation = new SimulinkSubToSubConnection(startConnectorName,
					startStecker.getPortPath(startPin.toString()), name, "1", system);
			endRelation = new SimulinkSubToSubConnection(name, "1", endConnectorName,
					endStecker.getPortPath(endPin.toString()), system);
			break;
		default:
			pathStart = startConnectorName + "_" + startStecker.getConnectionPath(startPin.toString()) + "_" + name
					+ "_LConn1";
			pathEnd = endConnectorName + "_" + endStecker.getConnectionPath(endPin.toString()) + "_" + name
					+ "_RConn1";
			startRelation = new SimulinkSubToSubConnection(startConnectorName,
					startStecker.getConnectionPath(startPin.toString()), name, "LConn1", system);
			endRelation = new SimulinkSubToSubConnection(endConnectorName,
					endStecker.getConnectionPath(endPin.toString()), name, "RConn1", system);
			break;
		}
		if (system.getRelation(pathStart) == null) {
			system.addRelation(startRelation);
			Contact leftContactPoint = new Contact(startConnectorName, startPin, 1);
			subsystem.addContact(leftContactPoint);
			Contact startSteckerContactPoint = new Contact(endStecker.getName(), endPin, startPin);
			startStecker.addContact(startSteckerContactPoint);
		}
		if (system.getRelation(pathEnd) == null) {
			system.addRelation(endRelation);
			Contact rightContactPoint = new Contact(endConnectorName, endPin, 2);
			subsystem.addContact(rightContactPoint);
			Contact endSteckerContactPoint = new Contact(startStecker.getName(), startPin, endPin);
			endStecker.addContact(endSteckerContactPoint);
		}
	}

	public static void initInformation(Connection connection, SimulinkSubsystem subsystem) {
		GeneralWireOccurrence generalWireOccurrence = connection.getGeneralWireOccurrence();
		if (generalWireOccurrence != null && generalWireOccurrence.getWireNumber() != null) {
			if (generalWireOccurrence.getWireNumber() != null) {
				subsystem.getKabelInformation().setWireNumber(generalWireOccurrence.getWireNumber());
			}
			if (generalWireOccurrence.getId() != null) {
				subsystem.getKabelInformation().setGeneralWireOccurrenceId(generalWireOccurrence.getId());
			}
			if (generalWireOccurrence.getPart() != null) {
				subsystem.getKabelInformation().setGeneralWireId(generalWireOccurrence.getPart());
			}
		}
		if (connection.getLength() != null) {
			subsystem.getKabelInformation().setLength(connection.getLength());
		}
		if (connection.getCrossSectionArea() != null) {
			subsystem.getKabelInformation().setCrossSectionArea(connection.getCrossSectionArea());
		}
		if (connection.getSignalName() != null) {
			subsystem.getKabelInformation().setSignalName(connection.getSignalName());
		}
	}
	
	public static void generateConnectorOccurrences(ConnectorOccurrence connectorOccurrence, 
			SimulinkSubsystemType type, SimulinkSystem system) {
		ConnectorHousing connectorHousing = connectorOccurrence.getConnectorHousing();
		if (system.getSubsystem(connectorOccurrence.getLargeId()) == null && connectorHousing != null) {
			SimulinkSubsystem subsystem = system
					.addSubsystem(new SimulinkSubsystem(system, connectorOccurrence.getLargeId(), type));
			List<Cavity> cavities = connectorOccurrence.getSlots().getCavities();
			for (Cavity cavity : cavities) {
				Integer cavityNumber = KBLUtils.getCavityNumberById(connectorHousing, cavity.getPart());
				subsystem.addInConnection(new LConnection(subsystem, cavityNumber.toString()));
				subsystem.addNumOfPins();
			}
			SimulinkInitHelper.initStecker(subsystem);
		}
	}
	
	public static void generateConnection(Connection connection, SimulinkSystem system, SimulinkSubsystemType type) {
		String name = connection.getName();
		if (system.getSubsystem(name) != null) {
			name = StringUtils.generateUniqueName(system, name);
		}
		system.addSubsystem(new SimulinkSubsystem(system, name, type));
		SimulinkSubsystem subsystem = system.getSubsystem(name);
		switch (type) {
		case KABEL:
			initCabel(subsystem, connection.getResistance());
			initConnection(connection, system, subsystem, name, type);
			initInformation(connection, subsystem);
			break;
		case THERMAL_KABEL:
			initThermalKabel(subsystem, connection);
			initConnection(connection, system, subsystem, name, type);
			break;
		// Add other cases if there are more types in SimulinkSubsystemType
		default:
			// Handle unexpected types if necessary
			break;
		}
	}
	
	public static void generateThermalConnector(GeneralWire generalWire, SimulinkSystem system) {
		for (Connection connection : generalWire.getValidConnections()) {
			Integer startPin = connection.getStartPin();
			Integer endPin = connection.getEndPin();
			ConnectorOccurrence startConnector = connection.getStartConnector();
			ConnectorOccurrence endConnector = connection.getEndConnector();
			String startConnectorName = startConnector.getLargeId() + "_" + startPin.toString() + "_Left";
			if (system.getSubsystem(startConnectorName) != null) {
				startConnectorName = StringUtils.generateUniqueName(system, startConnectorName);
			} 
			connection.setStartConnectorName(startConnectorName);
			SimulinkSubsystem subsystemStart = system.addSubsystem(new SimulinkSubsystem(system, 
					startConnectorName, SimulinkSubsystemType.THERMAL_STECKER));
			ISimulinkBlock outPort = subsystemStart.addOutPort(new OutPort(subsystemStart, startPin.toString()));
			SimulinkSubsystemHelper.reorderConnections(subsystemStart);
			ISimulinkBlock currentSource = subsystemStart.addBlock(new Constant(subsystemStart,
					"CurrentSource_" + startPin.toString()));
			currentSource.setParameter("Value", connection.getCurrent());
			subsystemStart.addRelation(new SimulinkConnection(currentSource.getOutPort(0), 
					outPort.getInPort(0), subsystemStart));
			
			String endConnectorName = endConnector.getLargeId() + "_" + endPin.toString() + "_Right";
			if (system.getSubsystem(endConnectorName) != null) {
				endConnectorName = StringUtils.generateUniqueName(system, endConnectorName);
			} 
			connection.setEndConnectorName(endConnectorName);
            SimulinkSubsystem subsystemEnd = system.addSubsystem(new SimulinkSubsystem(system, 
                    endConnectorName, SimulinkSubsystemType.THERMAL_STECKER));
            ISimulinkBlock inPort = subsystemEnd.addInPort(new InPort(subsystemEnd, endPin.toString()));
            SimulinkSubsystemHelper.reorderConnections(subsystemEnd);
			ISimulinkBlock scope = subsystemEnd.addBlock(new Scope(subsystemEnd,
					"Scope_" + endPin.toString()));
			subsystemEnd.addRelation(new SimulinkConnection(inPort.getOutPort(0), 
					scope.getInPort(0), subsystemEnd));
		}
	}
	
	public static void generateThermalCable(GeneralWire generalWire, SimulinkSystem system) {
		for (Connection connection : generalWire.getValidConnections()) {
			generateConnection(connection, system, SimulinkSubsystemType.THERMAL_KABEL);
		}
	}
}
