package LR4.TradingFSMBeh.SecondIteration;


import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ConcludeContractWithTwoWinners extends Behaviour {
    private final BidsAnalyzerSecondTry bidsAnalyzer;
    private final double requiredPower;
    private final double requiredPrice;
    private boolean done =false;
    private final AID consumerAID;
    private List<AID> winners = new ArrayList<>();
    private double averagePrice;
    private int successfulDealCount;

    private MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchProtocol("buying"),
            MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                    MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
            )
    );

    public ConcludeContractWithTwoWinners(BidsAnalyzerSecondTry bidsAnalyzer, AID consumerAID, double requiredPower, double requiredPrice) {
        this.bidsAnalyzer = bidsAnalyzer;
        this.requiredPower = requiredPower;
        this.requiredPrice = requiredPrice;
        this.consumerAID = consumerAID;
    }

    @Override
    public void onStart() {
        if (bidsAnalyzer.getBestSeller().isEmpty()){
            log.info("Best seller has not found");
            done = true;
        } else {
            JsonMessage jsonMessage = new JsonMessage();
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            for (AID seller : bidsAnalyzer.getBestSeller()) {
                msg.addReceiver(seller);
            }
            msg.setContent(jsonMessage.toJson(requiredPower, requiredPrice));
            msg.setProtocol("buying");
            myAgent.send(msg);
            double sumPrice = 0;
            for(Double price : bidsAnalyzer.getBestPrice()) {
                sumPrice += price;
            }
            averagePrice = (sumPrice) / bidsAnalyzer.getBestPrice().size();
            log.info("Sending buy request to {} and {}", bidsAnalyzer.getBestSeller().get(0).getLocalName(), bidsAnalyzer.getBestSeller().get(1).getLocalName());
        }

    }

    @Override
    public void action() {
        ACLMessage resp = myAgent.receive(mt);
        if (resp != null){
            if (resp.getPerformative() == ACLMessage.AGREE){
                log.info("buying successful with {}", resp.getSender().getLocalName());
                winners.add(resp.getSender());
                successfulDealCount++;
//                ACLMessage messageToProd = new ACLMessage(ACLMessage.INFORM);
//                messageToProd.setProtocol("reducing_power");
//                messageToProd.setContent(jsonMessage.toJson(bidsAnalyzer.getBestPower().get(0),bidsAnalyzer.getBestPrice()));
//                for (AID seller : bidsAnalyzer.getBestSeller()) {
//                    messageToProd.addReceiver(seller);
//                }
//                myAgent.send(messageToProd);
            } else {
                log.info("buying failed, price was {}", averagePrice);
                JsonMessage jsonMessage = new JsonMessage();
                ACLMessage messageToCons = new ACLMessage(ACLMessage.INFORM);
                messageToCons.setContent(jsonMessage.toJson(requiredPower*2 ,averagePrice));
                messageToCons.addReceiver(consumerAID);
                myAgent.send(messageToCons);
            }
            if (successfulDealCount == 2 || bidsAnalyzer.getBestPrice().size() == 1) {
                JsonMessage jsonMessage = new JsonMessage();
                ACLMessage messageToCons = new ACLMessage(ACLMessage.INFORM);
                messageToCons.setContent(jsonMessage.toJson(requiredPower*2, averagePrice));
                messageToCons.addReceiver(consumerAID);
                myAgent.send(messageToCons);

//                ACLMessage messageToWinners = new ACLMessage(ACLMessage.REQUEST);
//                messageToWinners.setProtocol("confirming");
//                for (AID receiver : winners) {
//                    messageToWinners.addReceiver(receiver);
//                    log.info("Send msg with reducing power command to {}", receiver.getLocalName());
//                }
//                myAgent.send(messageToWinners);

                done =true;
            }

        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
//        log.info("ENDING BEH {}", this.getBehaviourName());
        return super.onEnd();
    }

    @Override
    public boolean done() {
        return done;
    }
}
