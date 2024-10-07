package preevisiontosimulink.samples;

import com.mathworks.engine.MatlabEngine;

import preevisiontosimulink.library.DCVoltageSource;
import preevisiontosimulink.library.ElectricalReference;
import preevisiontosimulink.library.Resistor;
import preevisiontosimulink.library.SolverConfiguration;
import preevisiontosimulink.proxy.connection.SimulinkConnection;
import preevisiontosimulink.proxy.system.SimulinkSystem;
import preevisiontosimulink.proxy.system.SimulinkSystemType;

public class ModelGenerator {
	public static void main(String[] args) {
		generateModel("simpleCircuit");
	}

	public static void generateModel(String modelName) {
		// Create the simulink system
		SimulinkSystem system = new SimulinkSystem(modelName, SimulinkSystemType.WIRING_HARNESS, null);

		// Add blocks
		system.addBlock(new DCVoltageSource(system, "DC"));
		system.addBlock(new Resistor(system, "R1"));
		system.addBlock(new ElectricalReference(system, "Ref1"));
		system.addBlock(new SolverConfiguration(system, "Solver1"));

		// Set parameters
		system.getBlock("DC").setParameter("v0", 10);
		system.getBlock("R1").setParameter("R", 50);

		// Bind blocks
		system.addRelation(
				new SimulinkConnection(system.getBlock("DC").getInPort(0), system.getBlock("R1").getInPort(0), system));
		system.addRelation(new SimulinkConnection(system.getBlock("R1").getOutPort(0),
				system.getBlock("Ref1").getInPort(0), system));
		system.addRelation(new SimulinkConnection(system.getBlock("DC").getOutPort(0),
				system.getBlock("Ref1").getInPort(0), system));
		system.addRelation(new SimulinkConnection(system.getBlock("Solver1").getInPort(0),
				system.getBlock("Ref1").getInPort(0), system));

		try {
			MatlabEngine matlab = MatlabEngine.startMatlab();
			system.generateModel(matlab);
			matlab.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
