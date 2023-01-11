package LR4.DistributorBeh;

import LR4.DF.DFHelper;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;

import java.util.List;

public class SendTopicName {

    public void sendTopicNameToProd(Agent myAgent, String topicName) {
        ACLMessage messageWithTopic = new ACLMessage(ACLMessage.INFORM);
        messageWithTopic.setProtocol("TopicName");
        messageWithTopic.setContent(topicName);
        List<AID> distAIDs = DFHelper.findAgents(myAgent, "Producer");
        for (AID distAID: distAIDs) {
            messageWithTopic.addReceiver(distAID);
        }
        myAgent.send(messageWithTopic);
    }
}
