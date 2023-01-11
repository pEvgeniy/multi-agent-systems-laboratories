package LR4.DistributorBeh;

import LR4.DF.DFHelper;
import LR4.JSON.JsonMessage;
import LR4.TopicManager.MessageReceiver;
import LR4.TopicManager.TopicCreator;
import LR4.TradingFSMBeh.TradingFSM;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistributorBehaviour extends Behaviour {
    private Agent myAgent;
    private MessageTemplate mt;
    private double receivedPower;
    private double receivedPrice;

    public DistributorBehaviour(Agent myAgent) {
        this.myAgent = myAgent;
    }

    @Override
    public void onStart() {
        DFHelper.registerAgent(myAgent, "Distributor");
    }

    @Override
    public void action() {
        JsonMessage jsonMessage = new JsonMessage();
        mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage messageFromCons = myAgent.receive(mt);
        if (messageFromCons != null) {
            double requiredPower = jsonMessage.fromJson(messageFromCons.getContent()).getPowerAmount();
            double requiredPrice = jsonMessage.fromJson(messageFromCons.getContent()).getPrice();

            TopicCreator topicCreator = new TopicCreator(myAgent);
            SendTopicName sendTN = new SendTopicName();
            String topicName = myAgent.getLocalName()+"Topic";
            AID jadeTopic = topicCreator.createTopic(topicName);
            sendTN.sendTopicNameToProd(myAgent, topicName);
            log.info("TOPIC CREATED. Topic is : {}", jadeTopic.getLocalName());

//            log.info("Received {} from {}, starting FSM", messageFromCons.getContent(), messageFromCons.getSender());

            myAgent.addBehaviour( new TradingFSM(jadeTopic, messageFromCons.getSender(), requiredPower, requiredPrice));

        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
