package LR3.behaviours;

import LR3.methods.Methods;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
@Slf4j
public class SearchInitiationBeh extends Behaviour {
    private MessageTemplate mt;
    private final Agent myAgent;
    private final List<String> neighbours;
    private final List<String> weight;
    private final List<Integer> routes = new ArrayList<>();
    private final List<String> chains = new ArrayList<>();

    private boolean stopBehaviour;

    public SearchInitiationBeh(Agent myAgent, List<String> neighbours, List<String> weight) {
        this.weight = weight;
        this.myAgent = myAgent;
        this.neighbours = neighbours;
    }

    @Override
    public void onStart() {
        for (int i = 0; i < neighbours.size(); i++) {
            ACLMessage messageToNeighbours = new ACLMessage(ACLMessage.INFORM);
            AID neighbour = new AID(neighbours.get(i), false);
            messageToNeighbours.addReceiver(neighbour);
            messageToNeighbours.setContent(myAgent.getLocalName() + "_" + weight.get(i));
            myAgent.send(messageToNeighbours);
            log.debug("{} send first message to {}", myAgent.getLocalName(), neighbours.get(i));
        }
    }

    @Override
    public void action() {
        mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
        ACLMessage confirmation = myAgent.receive(mt);
        if (confirmation != null) {
            String NodeChain = confirmation.getContent();
            int parsedRoute = Methods.parser(NodeChain);
            routes.add(parsedRoute);
            chains.add(NodeChain);
            if (myAgent.getCurQueueSize() == 0) {
                myAgent.doWait(100);
                if (myAgent.getCurQueueSize() == 0) {
                    stopBehaviour = true;
                }
            }
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
        Methods.shortestRouteFinder(routes, chains);
        return 0;
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
