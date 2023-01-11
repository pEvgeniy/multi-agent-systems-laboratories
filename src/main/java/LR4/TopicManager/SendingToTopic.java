package LR4.TopicManager;

import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SendingToTopic extends WakerBehaviour {

    private AID topic;
    private double power;
    private double price;
    private String protocol;
    private int performative;

    public SendingToTopic(Agent a, long timeout, AID topic, String protocol, int performative, double power, double price) {
        super(a,timeout);
        this.topic = topic;
        this.power = power;
        this.price = price;
        this.protocol = protocol;
        this.performative = performative;
    }

    @Override
    public void onWake() {
        JsonMessage jsonMessage = new JsonMessage();
        ACLMessage message = new ACLMessage(performative);
        message.setProtocol(protocol);
        message.addReceiver(topic);
        String content = jsonMessage.toJson(power, price);
        message.setContent(content);
        myAgent.send(message);
//        log.info("Agent {} sending to {} message = {}", myAgent.getLocalName(), topic.getLocalName(), content);
    }
}
