package preevisiontosimulink.parser.kblelements;

import javax.xml.bind.annotation.*;


@XmlAccessorType(XmlAccessType.FIELD)
public class LengthValue {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "Unit_component")
    private String unitComponent;

    @XmlElement(name = "Value_component")
    private Double valueComponent;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitComponent() {
        return unitComponent;
    }

    public void setUnitComponent(String unitComponent) {
        this.unitComponent = unitComponent;
    }

    public Double getValueComponent() {
        return valueComponent;
    }

    public void setValueComponent(Double valueComponent) {
        this.valueComponent = valueComponent;
    }
}