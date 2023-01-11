package LR4.ConsumerBeh;

import LR4.DF.DFHelper;
import LR4.JSON.JsonMessage;
import LR4.Time.TimeSimulation;
import LR4.XML.ConsumerXMLParser;
import LR4.cfgClasses.ConsumerCFGClass;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ConsumerBehaviourTest extends Behaviour {

    private final ConsumerCFGClass consumerCFGClass = new ConsumerCFGClass();
    private MessageTemplate mt;
    private boolean stopBehaviour;
    private int onEnd;
    private double maximumPrice;
    private double requiredPower;
    private JsonMessage jsonMessage = new JsonMessage();
    private StartAuction startAuction = new StartAuction();
    private ACLMessage messageToDistr;

    public ConsumerBehaviourTest(Agent a, double maximumPrice) {
        super(a);
        this.maximumPrice = maximumPrice;
    }

    @Override
        public void onStart() {
            requiredPower = 10;
            String messageContent = jsonMessage.toJson(requiredPower, maximumPrice);
            startAuction.sendMessageToDistributor(myAgent, messageContent);
        }

        @Override
        public void action() {
            mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage messageFromDistr = myAgent.receive(mt);
            if (messageFromDistr != null) {
                double proposedPower = jsonMessage.fromJson(messageFromDistr.getContent()).getPowerAmount();
                double proposedPrice = jsonMessage.fromJson(messageFromDistr.getContent()).getPrice();

                if ((proposedPower >= requiredPower) && (proposedPrice >= maximumPrice)) {
                    log.info("SUCCESSFUL OPERATION. Bought {} kVt for {} currency units", proposedPower, proposedPrice);
                    onEnd = 2;
                } else if (proposedPrice < maximumPrice) {
//                                double firstMaximumPrice = maximumPrice;
//                                maximumPrice = maximumPrice*2;
//                                messageToDistr = new ACLMessage(ACLMessage.INFORM);
//                                startAuction.sendMessageToDistributor(myAgent, jsonMessage.toJson(requiredPower, maximumPrice));
                    log.info("SUCCESSFUL OPERATION.");
                    onEnd = 1;
                } else {
                    log.info("UNSUCCESSFUL OPERATION.");
                }
                stopBehaviour = true;
            }

        }

        @Override
        public int onEnd() {
            if (onEnd == 1) {
                log.info("return = 1");
                return 1;
            }
            if (onEnd == 2) {
                log.info("return = 1");
                return 2;
            }
            log.info("return = 0");
            return 0;
        }

        @Override
        public boolean done() {
            return stopBehaviour;
        }
}