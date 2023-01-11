package LR4.TopicManager;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lombok.Data;

import java.util.List;


public class MessageReceiver {

    private List<Double> receivedPower;

    private List<Double> receivedPrice;

    private List<ACLMessage> response;

    public void makeAllParamNull() {
        response = null;
        receivedPower = null;
        receivedPrice = null;
    }

    public List<Double> getReceivedPower() {
        return receivedPower;
    }

    public void setReceivedPower(Double receivedPower) {
        this.receivedPower.add(receivedPower);
    }

    public List<Double> getReceivedPrice() {
        return receivedPrice;
    }

    public void setReceivedPrice(Double receivedPrice) {
        this.receivedPrice.add(receivedPrice);
    }

    public List<ACLMessage> getResponse() {
        return response;
    }

    public void setResponse(ACLMessage response) {
        this.response.add(response);
    }
}
