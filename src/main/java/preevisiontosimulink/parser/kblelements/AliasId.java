package preevisiontosimulink.parser.kblelements;

import javax.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class AliasId {

    @XmlAttribute(name = "id")
    private String id;

    @XmlElement(name = "Alias_id")
    private String aliasId;

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAliasId() {
        return aliasId;
    }

    public void setAliasId(String aliasId) {
        this.aliasId = aliasId;
    }
}