package LR4.Agents;

import LR4.ProducerBeh.ProducerBehaviour;
import LR4.Time.TimeSimulation;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProducerAgent extends Agent {
    @Override
    protected void setup() {
        TimeSimulation timeSimulation = new TimeSimulation(System.currentTimeMillis());

        log.info("{} initialized", this.getLocalName());
        if (this.getLocalName().equals("ProducerTEC")){
            addBehaviour( new ProducerBehaviour(this, timeSimulation, 5));
        }
        if (this.getLocalName().equals("ProducerWES")){
            addBehaviour( new ProducerBehaviour(this, timeSimulation , 5));
        }
        if (this.getLocalName().equals("ProducerSES")){
            addBehaviour( new ProducerBehaviour(this, timeSimulation , 5));
        }
    }
}
