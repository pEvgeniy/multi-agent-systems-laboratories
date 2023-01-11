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
public class ConsumerBehaviour extends Behaviour {
    private Agent myAgent;
    private TimeSimulation timeSimulation;
    private final ConsumerCFGClass consumerCFGClass;
    private int currHour;
    private MessageTemplate mt;
    private boolean stopBehaviour;
    private double maximumPrice;
    private double requiredPower;
    private JsonMessage jsonMessage = new JsonMessage();
    private StartAuction startAuction = new StartAuction();
    private ACLMessage messageToDistr;

    public ConsumerBehaviour(Agent myAgent, TimeSimulation timeSimulation) {
        this.myAgent = myAgent;
        this.timeSimulation = timeSimulation;
        consumerCFGClass = ConsumerXMLParser.parser(myAgent.getLocalName());
    }

    @Override
    public void onStart() {
        currHour = timeSimulation.getCurrentHour();
        maximumPrice = 4.0;
        requiredPower = consumerCFGClass.getPowerConsumption()[timeSimulation.getCurrentHour()] * consumerCFGClass.getPower100percent() / 100;
        String messageContent = jsonMessage.toJson(requiredPower, maximumPrice);
        startAuction.sendMessageToDistributor(myAgent, messageContent);
    }

    @Override
    public void action() {
        if (currHour != timeSimulation.getCurrentHour()) {
            log.info("\nTIME CHANGED FROM {} TO {}. NEW POWER AUCTION STARTED...\n", currHour, timeSimulation.getCurrentHour());
            currHour = timeSimulation.getCurrentHour();
            requiredPower = consumerCFGClass.getPowerConsumption()[timeSimulation.getCurrentHour()] * consumerCFGClass.getPower100percent() / 100;
            messageToDistr = new ACLMessage(ACLMessage.INFORM);
            String messageContent = jsonMessage.toJson(requiredPower, maximumPrice);
            startAuction.sendMessageToDistributor(myAgent, messageContent);
        }

        mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage messageFromDistr = myAgent.receive(mt);
        if (messageFromDistr != null) {
            double proposedPower = jsonMessage.fromJson(messageFromDistr.getContent()).getPowerAmount();
            double proposedPrice = jsonMessage.fromJson(messageFromDistr.getContent()).getPrice();

            if ((proposedPower >= requiredPower) && (proposedPrice >= maximumPrice)) {
                log.info("SUCCESSFUL OPERATION. Bought {} kVt for {} currency units", proposedPower, proposedPrice);
            } else if (proposedPrice < maximumPrice) {
                double firstMaximumPrice = maximumPrice;
                maximumPrice = maximumPrice*2;
                messageToDistr = new ACLMessage(ACLMessage.INFORM);
                startAuction.sendMessageToDistributor(myAgent, jsonMessage.toJson(requiredPower, maximumPrice));
                log.info("UNSUCCESSFUL OPERATION. Increasing price form {} to {} in order to make a deal", firstMaximumPrice, maximumPrice);
            } else {
                log.info("UNSUCCESSFUL OPERATION. NO Power for {}", myAgent.getLocalName());
            }
        }

    }

    @Override
    public boolean done() {
        return false;
    }
}
