package LR4.TradingFSMBeh.FirstIteration;

import LR4.DF.DFHelper;
import LR4.JSON.JsonMessage;
import LR4.TopicManager.SendingToTopic;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CollectBids extends Behaviour {
    private int requestsCount;
    private final List<ACLMessage> responses = new ArrayList<>();
    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
            MessageTemplate.MatchProtocol("buying"));    /*!!!!!!!*/
    private final BidsAnalyzer bidsAnalyzer;
    private final double power;
    private final double price;
    private final AID jadeTopic;

    public CollectBids(BidsAnalyzer bidsAnalyzer, AID jadeTopic, double power, double price) {
        this.bidsAnalyzer = bidsAnalyzer;
        this.power = power;
        this.price = price;
        this.jadeTopic = jadeTopic;
    }


    @Override
    public void onStart() {
        bidsAnalyzer.refreshBids();
        myAgent.addBehaviour(new SendingToTopic(myAgent,10, jadeTopic, "buying", 3, power, price));
        requestsCount = DFHelper.findAgents(myAgent, "Producer").size();
        log.info("Send needed power = {} and price = {} to Topic with protocol = buying and prop = {} to topic {}", power, price, ACLMessage.CFP, jadeTopic.getLocalName());
    }

    @Override
    public void action() {
        ACLMessage response = myAgent.receive(mt);    /*Сюда приходит от HandleCFP onStart availPower availPrice*/
        if (response != null){
            JsonMessage jsonMessage = new JsonMessage();
//            log.info("Trying to add bid from {}", response.getSender().getLocalName());
            double receivedPrice = jsonMessage.fromJson(response.getContent()).getPrice();
            responses.add(response);
            bidsAnalyzer.putBid(response);
        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return responses.size() == requestsCount;
    }
}
