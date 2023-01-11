package LR3.CFG;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.*;
import java.util.Arrays;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cfg")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CFGClass {

    @XmlElement
    private String AgentName;

    @XmlElement
    private boolean InitiationMark;
    
    @XmlElement
    private boolean DestinationMark;

    @XmlElementWrapper(name = "NeighboursArray")
    @XmlElement(name = "neighbour")
    private List<String> NeighboursArray;

    @XmlElementWrapper(name = "WeightArray")
    @XmlElement(name = "weight")
    private List<String> WeightArray;

    @Override
    public String toString() {
        return "ConsumerCFGClass{" +
                "AgentName='" + AgentName + '\'' +
                ", InitiationMark=" + InitiationMark +
                ", DestinationMark=" + DestinationMark +
                ", NeighboursArray=" + NeighboursArray +
                ", WeightArray=" + WeightArray +
                '}';
    }
}