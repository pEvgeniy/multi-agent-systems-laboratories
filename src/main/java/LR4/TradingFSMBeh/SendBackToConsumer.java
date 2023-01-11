package LR4.TradingFSMBeh;

import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class SendBackToConsumer extends Behaviour {

    private AID consumerAID;
    private double receivedPower;
    private double receivedPrice;
    private JsonMessage jsonMessage = new JsonMessage();
    private boolean stopBehaviour;

    public SendBackToConsumer(AID consumerAID, double receivedPower, double receivedPrice) {
        this.consumerAID = consumerAID;
        this.receivedPower = receivedPower;
        this.receivedPrice = receivedPrice;
    }

    @Override
    public void action() {
        ACLMessage messageToConsumer = new ACLMessage(ACLMessage.INFORM);
        messageToConsumer.addReceiver(consumerAID);
        messageToConsumer.setContent(jsonMessage.toJson(receivedPower, receivedPrice));
        myAgent.send(messageToConsumer);
        stopBehaviour = true;
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
