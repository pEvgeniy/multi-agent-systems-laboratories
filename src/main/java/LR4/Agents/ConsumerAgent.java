package LR4.Agents;

import LR4.ConsumerBeh.ConsumerBehaviour;
import LR4.Time.TimeSimulation;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConsumerAgent extends Agent {

    @Override
    protected void setup() {
        TimeSimulation timeSimulation = new TimeSimulation(System.currentTimeMillis());

        log.info("{} initialized", this.getLocalName());

        addBehaviour( new ConsumerBehaviour(this, timeSimulation));
    }
}
