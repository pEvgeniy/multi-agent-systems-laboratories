package LR4.Agents;

import LR4.ConsumerBeh.ConsumerBehaviour;
import LR4.DistributorBeh.DistributorBehaviour;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistributorAgent extends Agent {
    @Override
    protected void setup() {
        log.info("{} initialized", this.getLocalName());
        addBehaviour( new DistributorBehaviour(this));
    }
}
