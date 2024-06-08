package preevisiontosimulink.parser.kblelements;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Module")
@XmlAccessorType(XmlAccessType.FIELD)
public class Module {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "Part_number")
    private String partNumber;

    @XmlElement(name = "Company_name")
    private String companyName;

    @XmlElement(name = "Version")
    private String version;

    @XmlElement(name = "Abbreviation")
    private String abbreviation;

    @XmlElement(name = "Description")
    private String description;

    @XmlElement(name = "Predecessor_part_number")
    private String predecessorPartNumber;

    @XmlElement(name = "Mass_information")
    private MassInformation massInformation;

    @XmlElement(name = "Car_classification_level_2")
    private String carClassificationLevel2;

    @XmlElement(name = "Model_year")
    private String modelYear;

    @XmlElement(name = "Content")
    private String content;

    @XmlElement(name = "Module_configuration")
    private ModuleConfiguration moduleConfiguration;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPredecessorPartNumber() {
		return predecessorPartNumber;
	}

	public void setPredecessorPartNumber(String predecessorPartNumber) {
		this.predecessorPartNumber = predecessorPartNumber;
	}

	public MassInformation getMassInformation() {
		return massInformation;
	}

	public void setMassInformation(MassInformation massInformation) {
		this.massInformation = massInformation;
	}

	public String getCarClassificationLevel2() {
		return carClassificationLevel2;
	}

	public void setCarClassificationLevel2(String carClassificationLevel2) {
		this.carClassificationLevel2 = carClassificationLevel2;
	}

	public String getModelYear() {
		return modelYear;
	}

	public void setModelYear(String modelYear) {
		this.modelYear = modelYear;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ModuleConfiguration getModuleConfiguration() {
		return moduleConfiguration;
	}

	public void setModuleConfiguration(ModuleConfiguration moduleConfiguration) {
		this.moduleConfiguration = moduleConfiguration;
	}
}