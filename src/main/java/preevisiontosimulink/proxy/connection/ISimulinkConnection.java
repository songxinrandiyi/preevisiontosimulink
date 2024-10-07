package preevisiontosimulink.proxy.connection;

import com.mathworks.engine.MatlabEngine;
import preevisiontosimulink.proxy.system.ISimulinkSystem;

// Interface representing a connection between Simulink blocks.
public interface ISimulinkConnection {

    /**
     * Retrieves the parent Simulink system associated with this connection.
     *
     * @return ISimulinkSystem representing the parent system
     */
    ISimulinkSystem getParent();

    /**
     * Generates the model in the Simulink environment using the provided MatlabEngine instance.
     *
     * @param matlab the MatlabEngine instance to be used for generating the model
     */
    void generateModel(MatlabEngine matlab);

    /**
     * Gets the name of this connection.
     *
     * @return String representing the name of the connection
     */
    String getName();
}
