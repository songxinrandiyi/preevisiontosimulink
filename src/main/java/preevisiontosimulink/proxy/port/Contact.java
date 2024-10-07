package preevisiontosimulink.proxy.port;

// Class representing a contact point between components, including the name and pin numbers
public class Contact {
    
    // Name of the connector
    private String name;
    
    // Pin number that this contact connects to
    private Integer pinNumberTo;
    
    // Pin number that this contact connects from
    private Integer pinNumberFrom;

    // Constructor initializing the contact with a connector name and pin numbers
    public Contact(String connectorName, Integer pinNumberTo, Integer pinNumberFrom) {
        this.name = connectorName; // Set the name of the connector
        this.pinNumberTo = pinNumberTo; // Set the destination pin number
        this.pinNumberFrom = pinNumberFrom; // Set the source pin number
    }

    // Method to get the name of the connector
    public String getName() {
        return name; // Return the connector's name
    }

    // Method to set the name of the connector
    public void setName(String name) {
        this.name = name; // Update the connector's name
    }

    // Method to get the pin number this contact connects to
    public Integer getPinNumberTo() {
        return pinNumberTo; // Return the destination pin number
    }

    // Method to set the pin number this contact connects to
    public void setPinNumberTo(Integer pinNumberTo) {
        this.pinNumberTo = pinNumberTo; // Update the destination pin number
    }

    // Method to get the pin number this contact connects from
    public Integer getPinNumberFrom() {
        return pinNumberFrom; // Return the source pin number
    }

    // Method to set the pin number this contact connects from
    public void setPinNumberFrom(Integer pinNumberFrom) {
        this.pinNumberFrom = pinNumberFrom; // Update the source pin number
    }

    // Override the equals method to compare contacts based on name and pinNumberTo
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true; // Check if the same instance
        if (o == null || getClass() != o.getClass())
            return false; // Check for null or different class

        Contact that = (Contact) o; // Cast to Contact for comparison

        // Check if names and pinNumberTo are equal
        if (!name.equals(that.name))
            return false; 
        return pinNumberTo.equals(that.pinNumberTo); // Compare pinNumberTo
    }

    // Override the hashCode method to generate a unique hash based on name and pinNumberTo
    @Override
    public int hashCode() {
        int result = name.hashCode(); // Start with the hash of the name
        result = 31 * result + pinNumberTo.hashCode(); // Incorporate pinNumberTo
        return result; // Return the combined hash code
    }

    // Override the toString method for a string representation of the contact
    @Override
    public String toString() {
        return "Contact{" + "connectorName='" + name + '\'' + ", pinNumberTo=" + pinNumberTo + '}'; // Format string output
    }
}
