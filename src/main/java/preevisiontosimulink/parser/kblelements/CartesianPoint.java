package preevisiontosimulink.parser.kblelements;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Cartesian_point")
@XmlAccessorType(XmlAccessType.FIELD)
public class CartesianPoint {
	@XmlAttribute(name = "id")
	private String id;

	@XmlElement(name = "Coordinates")
	private List<Double> coordinates;

	// Getter und Setter
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Double> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<Double> coordinates) {
		this.coordinates = coordinates;
	}
}
