package preevisiontosimulink.library;

import preevisiontosimulink.proxy.*;


public class Resistor extends SimulinkBlock {

    private static int num = 0;

    public Resistor(ISimulinkSystem parent, String name) {
		super(parent, name);
    }    

    @Override
    public void initialize() {
    	this.BLOCK_NAME = "Resistor";
    	this.BLOCK_PATH = "fl_lib/Electrical/Electrical Elements/Resistor";
    	if(name == null) {
        	this.name = BLOCK_NAME + num;
    	}
		num++;
        // Initialize inputs and outputs if necessary
        this.inputs.add(new SimulinkPort(1, this));
        this.outputs.add(new SimulinkPort(2, this)); 

        // Initialize parameters specific to the Sine Wave block
        this.parameters.add(new SimulinkParameter<Double>("R", this));
    }
}

