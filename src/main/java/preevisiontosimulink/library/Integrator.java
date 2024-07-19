package preevisiontosimulink.library;

import preevisiontosimulink.proxy.block.SimulinkBlock;
import preevisiontosimulink.proxy.block.SimulinkParameter;
import preevisiontosimulink.proxy.port.SimulinkPort;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

public class Integrator extends SimulinkBlock {
	private static int num = 1;

	public Integrator(ISimulinkSystem parent, String name) {
		super(parent, name);
	}

	@Override
	public void initialize() {
		this.BLOCK_NAME = "Integrator";
		this.BLOCK_PATH = "simulink/Continuous/Integrator";
		if (name == null) {
			this.name = BLOCK_NAME + num;
		}
		num++;
		this.inPorts.add(new SimulinkPort(1, this));
		this.outPorts.add(new SimulinkPort(1, this));
		
		this.parameters.add(new SimulinkParameter<Double>("InitialCondition", this));
	}
}