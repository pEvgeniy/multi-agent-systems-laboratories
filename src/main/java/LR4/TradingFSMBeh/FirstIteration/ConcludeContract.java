package LR4.TradingFSMBeh.FirstIteration;

import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConcludeContract extends Behaviour {
    private final BidsAnalyzer bidsAnalyzer;
    private final double requiredPower;
    private final double requiredPrice;
    private boolean done =false;
    private final AID consumerAID;
    private int eventState;

    private final MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchProtocol("buying"),
            MessageTemplate.or(
                    MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                    MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
            )
    );

    public ConcludeContract(BidsAnalyzer bidsAnalyzer, AID consumerAID, double requiredPower, double requiredPrice) {
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
            msg.addReceiver(bidsAnalyzer.getBestSeller().get());
            msg.setContent(jsonMessage.toJson(requiredPower, requiredPrice));
            msg.setProtocol("buying");
            myAgent.send(msg);
            log.info("Sending buy request to {}", bidsAnalyzer.getBestSeller().get().getLocalName());
        }

    }

    @Override
    public void action() {
        ACLMessage resp = myAgent.receive(mt);
        if (resp != null){
            if (resp.getPerformative() == ACLMessage.AGREE){
                log.info("Buying successful...");
                JsonMessage jsonMessage = new JsonMessage();
                ACLMessage messageToCons = new ACLMessage(ACLMessage.INFORM);
                messageToCons.setContent(jsonMessage.toJson(requiredPower, requiredPrice));
                messageToCons.addReceiver(consumerAID);
                myAgent.send(messageToCons);
            } else {
                log.info("Buying failed...");
//                JsonMessage jsonMessage = new JsonMessage();
//                ACLMessage messageToCons = new ACLMessage(ACLMessage.INFORM);
//                messageToCons.setContent(jsonMessage.toJson(-1,bidsAnalyzer.getBestPrice()));
//                messageToCons.addReceiver(consumerAID);
//                myAgent.send(messageToCons);
                eventState = 5;
            }
            done =true;
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
//        log.info("ENDING BEH {}", this.getBehaviourName());
        if (eventState == 5) {
            log.info("EVENT 5. PRODUCER FOUND, BUT DEAL TRY IS UNSUCCESSFUL. TRYING TO FIND NEW ONE.");
        }
        return eventState;
    }

    @Override
    public boolean done() {
        return done;
    }
}
