package LR4.TradingFSMBeh;

import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class RequestToMakePriceHigher extends OneShotBehaviour {
    private double receivedPower;
    private double receivedPrice;
    private AID consumerAID;

    public RequestToMakePriceHigher(AID consumerAID, double receivedPower, double receivedPrice) {
        this.receivedPower = receivedPower;
        this.receivedPrice = receivedPrice;
        this.consumerAID = consumerAID;
    }

    @Override
    public void action() {
        JsonMessage jsonMessage = new JsonMessage();
        ACLMessage messageToConsumer = new ACLMessage(ACLMessage.INFORM);
        messageToConsumer.addReceiver(consumerAID);
        messageToConsumer.setContent(jsonMessage.toJson(receivedPower, 0));
        myAgent.send(messageToConsumer);
    }
}
