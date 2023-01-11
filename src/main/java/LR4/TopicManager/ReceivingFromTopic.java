package LR4.TopicManager;

import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

@Slf4j
public class ReceivingFromTopic extends Behaviour {

    private AID topic;
    private MessageTemplate mt;
    private MessageReceiver msgContainer;

    public ReceivingFromTopic(AID topic, MessageTemplate mt, MessageReceiver msgContainer) {
        this.topic = topic;
        this.mt = mt;
        this.msgContainer = msgContainer;
    }

    private boolean stopBehaviour = false;

    @Override
    public void action() {
        ACLMessage receive = getAgent().receive(mt);
        if (receive != null){
            JsonMessage jsonMessage = new JsonMessage();
            double power = jsonMessage.fromJson(receive.getContent()).getPowerAmount();
            double price = jsonMessage.fromJson(receive.getContent()).getPrice();
            msgContainer.setReceivedPower(power);
            msgContainer.setReceivedPrice(price);
            msgContainer.setResponse(receive);
            log.info("Received message {}, {} from topic", power, price);
            stopBehaviour = true;
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
