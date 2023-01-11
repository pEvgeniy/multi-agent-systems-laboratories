package LR3.behaviours;

import LR3.CFG.CFGClass;
import LR3.CFG.xml;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

public class ProcessingRequestBeh extends Behaviour {
    private MessageTemplate mt;
    private final List<String> neighbours;
    private final List<String> weight;

    public ProcessingRequestBeh(Agent myAgent, List<String> neighbours, List<String> weight) {
        this.myAgent = myAgent;
        this.neighbours = neighbours;
        this.weight = weight;
    }

    @Override
    public void action() {
        boolean destinationAgent = false;
        CFGClass cfg = xml.parser(myAgent.getLocalName());
        mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt);
        if (message != null) {
            String NodeChain = message.getContent();
            if (cfg.isDestinationMark()) {
                destinationAgent = true;
            }
            int sendingCounter = 0;
            for (int i = 0; i < neighbours.size(); i++) {
                if (!NodeChain.contains(neighbours.get(i)) && !destinationAgent) {
                    ACLMessage messageToNeighbours = new ACLMessage(ACLMessage.INFORM);
                    AID neighbour = new AID(neighbours.get(i), false);
                    messageToNeighbours.addReceiver(neighbour);
                    messageToNeighbours.setContent(NodeChain + ";" + myAgent.getLocalName() + "_" + weight.get(i));
                    myAgent.send(messageToNeighbours);
                    sendingCounter++;

                }
            }
            if (sendingCounter == 0 || destinationAgent) {
                myAgent.addBehaviour(new ReplyBeh(myAgent, NodeChain));
            }
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
