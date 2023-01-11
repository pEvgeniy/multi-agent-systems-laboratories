package LR4.TradingFSMBeh.SecondIteration;

import LR4.DF.DFHelper;
import LR4.TopicManager.SendingToTopic;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CollectBidsWithTwoWinners extends Behaviour {
    private int requestsCount;
    private final List<ACLMessage> responses = new ArrayList<>();
    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.PROPOSE),
            MessageTemplate.MatchProtocol("buying"));    /*!!!!!!!*/
    private final BidsAnalyzerSecondTry bidsAnalyzer;
    private final double power;
    private final double price;
    private final AID jadeTopic;

    public CollectBidsWithTwoWinners(BidsAnalyzerSecondTry bidsAnalyzer, AID jadeTopic, double power, double price) {
        this.bidsAnalyzer = bidsAnalyzer;
        this.power = power;
        this.price = price;
        this.jadeTopic = jadeTopic;
    }

    @Override
    public void onStart() {
        myAgent.addBehaviour(new SendingToTopic(myAgent,10, jadeTopic, "buying_from_two", 3, power, price));
        log.info("Send needed power = {} and price = {} to Topic with protocol = buying and prop = {}", power, price, ACLMessage.CFP);
        requestsCount = DFHelper.findAgents(myAgent, "Producer").size();
    }

    @Override
    public void action() {
        ACLMessage response = myAgent.receive(mt);    /*Сюда приходит от HandleCFP onStart availPower availPrice*/
        if (response != null){
            responses.add(response);
            bidsAnalyzer.putBid(response);
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
        bidsAnalyzer.findMinPrices();
        log.info("Bids were sorted and minimum prices were found : {}", bidsAnalyzer.getBestPrice());
        return 0;
    }

    @Override
    public boolean done() {
        return responses.size() == requestsCount;
    }
}