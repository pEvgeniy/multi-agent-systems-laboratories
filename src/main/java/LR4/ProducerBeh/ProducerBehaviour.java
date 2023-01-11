package LR4.ProducerBeh;

import LR4.DF.DFHelper;
import LR4.GenerationModeller.Generation;
import LR4.JSON.JsonMessage;
import LR4.Time.TimeSimulation;
import LR4.TopicManager.MessageReceiver;
import LR4.TopicManager.ReceivingFromTopic;
import LR4.TopicManager.SendingToTopic;
import LR4.TopicManager.TopicCreator;
import LR4.TradingFSMBeh.HandleCFP;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ProducerBehaviour extends Behaviour {

    private final MessageTemplate mt = MessageTemplate.or(
            MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.INFORM),
                    MessageTemplate.or(
                            MessageTemplate.MatchProtocol("TopicName"),
                            MessageTemplate.MatchProtocol("reducing_power")
                    )
            ),
            MessageTemplate.and(
                    MessageTemplate.MatchPerformative(ACLMessage.CFP),
                    MessageTemplate.or(
                            MessageTemplate.MatchProtocol("buying"),
                            MessageTemplate.MatchProtocol("buying_from_two")
                    )
            )
    );

    private Agent myAgent;
    private double receivedPower;
    private double receivedPrice;
    private List<AID> jadeTopic = new ArrayList<>();
    private AID currJadeTopic;
    private double availablePower;
    private double availablePrice;
    private final JsonMessage jsonMessage = new JsonMessage();
    private AvailablePower avPower = new AvailablePower();
    private Generation generation = new Generation();
    private DynamicPrice dynamicPrice;
    private TimeSimulation timeSimulation;
    private int currHour;

    public ProducerBehaviour(Agent myAgent , TimeSimulation timeSimulation, double availablePrice) {
        this.myAgent = myAgent;
        this.timeSimulation = timeSimulation;
        this.availablePrice = availablePrice;
    }

    @Override
    public void onStart() {
        currHour = timeSimulation.getCurrentHour();
        availablePower += generation.generatedPower(myAgent.getLocalName(), currHour);
        avPower.setAvailablePower(availablePower);
        DFHelper.registerAgent(myAgent, "Producer");
        dynamicPrice = new DynamicPrice(availablePrice, availablePower, myAgent.getLocalName());
        availablePrice = dynamicPrice.priceCutter(availablePower);
        log.info("{} started with availablePower = {}, availablePrice = {}", myAgent.getLocalName(), availablePower, availablePrice);
    }

    @Override
    public void action() {
        ACLMessage messageToSeller = myAgent.receive(mt);
        if (messageToSeller != null) {
            if (currHour != timeSimulation.getCurrentHour()) {
                avPower.setAvailablePower(avPower.getAvailablePower() + generation.generatedPower(myAgent.getLocalName(), currHour));
                dynamicPrice = new DynamicPrice(availablePrice, availablePower, myAgent.getLocalName());
//                availablePower += generation.generatedPower(myAgent.getLocalName(), currHour);
            }
            if (messageToSeller.getPerformative() == 7 && messageToSeller.getProtocol().equals("TopicName")) {
                String topicName = messageToSeller.getContent();
                TopicCreator topicCreator = new TopicCreator(myAgent);
                jadeTopic.add(topicCreator.createTopic(topicName));
            }
            if (messageToSeller.getPerformative() == 3) {
                double receivedPower = jsonMessage.fromJson(messageToSeller.getContent()).getPowerAmount();
                double receivedPrice = jsonMessage.fromJson(messageToSeller.getContent()).getPrice();

                for(AID topic : jadeTopic) {
                    if (topic.getLocalName().contains(messageToSeller.getSender().getLocalName())) {
                        currJadeTopic = topic;
                        break;
                    }
                }

                boolean awaitingFromTwo = false;
                if (messageToSeller.getProtocol().equals("buying_from_two")) {
                    awaitingFromTwo = true;
                }

                availablePower = avPower.getAvailablePower();
                availablePrice = dynamicPrice.priceCutter(availablePower);
//                log.info("{} subscribed on topic {}. receivedPower = {} and availablePower = {}",myAgent.getLocalName(), currJadeTopic.getLocalName(), receivedPower, availablePower);

                if (availablePower >= receivedPower){
//                    log.info("{} send message to topic with availPower = {} and availPrice = {}", myAgent.getLocalName(), availablePower, availablePrice);
                    myAgent.addBehaviour( new HandleCFP(messageToSeller, currJadeTopic, avPower, awaitingFromTwo, avPower.getAvailablePower(), availablePrice));
                } else {
//                    log.info("{} send message to topic with availPower = {} and availPrice = {}", myAgent.getLocalName(), -1, availablePrice);
                    myAgent.addBehaviour( new HandleCFP(messageToSeller, currJadeTopic, avPower, awaitingFromTwo, -1, availablePrice));
                }
            }
            if (messageToSeller.getPerformative() == 7 && messageToSeller.getProtocol().equals("reducing_power")) {
                log.info("{} power reduced to {}", myAgent.getLocalName(), avPower.getAvailablePower());
            }

        } else {
            block();
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
