package preevisiontosimulink.proxy.block;

import java.util.List;

// Interface representing a parameterizable entity in a Simulink context
public interface ISimulinkParameterizable {
    
    // Get a list of all parameters associated with this parameterizable entity
    List<SimulinkParameter<?>> getParameters();

    // Add a parameter of a specified type to the entity
    <T> void addParameter(SimulinkParameter<T> parameter);

    // Set the value of a parameter by its name
    <T> void setParameter(String name, T value);
}
