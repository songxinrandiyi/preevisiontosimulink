package preevisiontosimulink.proxy.block;

// Class representing a parameter for a Simulink block with a generic type T
public class SimulinkParameter<T> {
    
    // Name of the parameter
    private String name;
    
    // Reference to the parent Simulink block
    private SimulinkBlock parent;
    
    // Value of the parameter, initialized to null
    private T value = null;

    // Constructor initializing the parameter with a name and its parent block
    public SimulinkParameter(String name, SimulinkBlock parent) {
        this.name = name; // Set the name of the parameter
        this.parent = parent; // Set the parent block
    }

    // Method to get the name of the parameter
    public String getName() {
        return name; // Return the parameter's name
    }

    // Method to get the parent block of this parameter
    public SimulinkBlock getParent() {
        return parent; // Return the parent Simulink block
    }

    // Method to get the value of the parameter
    public T getValue() {
        return value; // Return the current value of the parameter
    }

    // Method to set the value of the parameter
    public void setValue(T value) {
        this.value = value; // Assign the new value to the parameter
    }
}
