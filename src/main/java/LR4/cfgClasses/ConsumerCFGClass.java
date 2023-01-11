package LR4.cfgClasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cfg")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumerCFGClass {

    @XmlElement
    private String ConsumerName;

    @XmlElement
    private double Power100percent;

    @XmlElementWrapper(name = "PowerConsumptionList")
    @XmlElement(name = "power")
    private int[] PowerConsumption;
}