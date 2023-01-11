package LR3;

import LR3.CFG.CFGClass;
import LR3.CFG.xml;
import LR3.behaviours.ProcessingRequestBeh;
import LR3.behaviours.SearchInitiationBeh;
import jade.core.Agent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NodeAgent extends Agent {
    @Override
    protected void setup() {
        log.info("{} initialised", this.getName());

        CFGClass cfg = xml.parser(this.getLocalName());
        if (cfg.isInitiationMark()) {
            this.addBehaviour(new SearchInitiationBeh(this, cfg.getNeighboursArray(), cfg.getWeightArray()));
        } else {
            this.addBehaviour(new ProcessingRequestBeh(this, cfg.getNeighboursArray(), cfg.getWeightArray()));
        }

    }
}
