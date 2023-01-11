package LR3.behaviours;

import LR3.CFG.CFGClass;
import LR3.CFG.xml;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReplyBeh extends Behaviour {
    private MessageTemplate mt;
    private final Agent myAgent;
    private String NodeChain;
    private boolean stopBehaviour;

    public ReplyBeh(Agent myAgent, String NodeChain) {
        this.myAgent = myAgent;
        this.NodeChain = NodeChain; /*'Agent1_4;Agent2_7;Agent4_9'*/
    }

    @Override
    public void action() {
        CFGClass cfg = xml.parser(myAgent.getLocalName());
        ACLMessage messageToNeighbours;
        if (cfg.isDestinationMark()) {
            NodeChain = myAgent.getLocalName() + ";" + NodeChain;
        } else {
            NodeChain = "Failure" + ";" + NodeChain;
        }
        messageToNeighbours = new ACLMessage(ACLMessage.CONFIRM);
        String receiver = NodeChain.split("_")[0].split(";")[1];
        AID initiationAgent = new AID(receiver, false);
        messageToNeighbours.addReceiver(initiationAgent);
        messageToNeighbours.setContent(NodeChain);
        myAgent.send(messageToNeighbours);
        stopBehaviour = true;
        log.debug("{} send message with route {} to {}", myAgent.getLocalName(), NodeChain, receiver);
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
