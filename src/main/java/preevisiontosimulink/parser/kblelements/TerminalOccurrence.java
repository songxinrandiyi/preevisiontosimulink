package preevisiontosimulink.parser.kblelements;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Terminal_occurrence")
@XmlAccessorType(XmlAccessType.FIELD)
public class TerminalOccurrence {

	@XmlAttribute(name = "id")
	private String id;

	@XmlElement(name = "Part")
	private String part;

	// Getters and setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}
}
