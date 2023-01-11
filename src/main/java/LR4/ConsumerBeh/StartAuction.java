package LR4.ConsumerBeh;

import LR4.DF.DFHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class StartAuction {

    public void sendMessageToDistributor(Agent myAgent, String jsonMessage) {
        ACLMessage messageToDistr = new ACLMessage(ACLMessage.INFORM);
        List<AID> distAIDs = DFHelper.findAgents(myAgent, "Distributor");
        for (AID distAID: distAIDs) {
            if (distAID.getLocalName().contains(myAgent.getLocalName().split("")[myAgent.getLocalName().length()-1])) {
                messageToDistr.addReceiver(distAID);
            }
        }
        messageToDistr.setContent(jsonMessage);
        myAgent.send(messageToDistr);
    }
}
