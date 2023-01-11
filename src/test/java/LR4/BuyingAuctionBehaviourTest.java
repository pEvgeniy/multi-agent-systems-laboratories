package LR4;

import LR4.ConsumerBeh.ConsumerBehaviour;
import LR4.ConsumerBeh.ConsumerBehaviourTest;
import LR4.ConsumerBeh.StartAuction;
import LR4.DF.DFHelper;
import LR4.DistributorBeh.DistributorBehaviour;
import LR4.DistributorBeh.SendTopicName;
import LR4.GenerationModeller.Generation;
import LR4.JSON.JsonMessage;
import LR4.ProducerBeh.AvailablePower;
import LR4.ProducerBeh.DynamicPrice;
import LR4.Time.TimeSimulation;
import LR4.TopicManager.TopicCreator;
import LR4.TradingFSMBeh.HandleCFP;
import LR4.TradingFSMBeh.TradingFSM;
import LR4.XML.ConsumerXMLParser;
import LR4.cfgClasses.ConsumerCFGClass;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;


@Slf4j
class BuyingAuctionBehaviourTest {
    private JadeTestingKit kit = new JadeTestingKit();
    private ConsumerBehaviourTest inner;

    @Test
    @SneakyThrows
    void notEnoughMoney(){
        kit.createAgent("Consumer3", wrapConsumerBeh());
        kit.createAgent("Distributor3", wrapDistributorBeh());

        kit.createAgent("ProducerTEC", wrapProducerBeh(100));
        kit.createAgent("ProducerWES", wrapProducerBeh(1));
        kit.createAgent("ProducerSES", wrapProducerBeh(1));

        Thread.sleep(1500);
        Assertions.assertEquals(1, inner.onEnd());
    }

    @Test
    @SneakyThrows
    void twoSellers(){
        kit.createAgent("Consumer3", wrapConsumerBeh());
        kit.createAgent("Distributor3", wrapDistributorBeh());

        kit.createAgent("ProducerTEC", wrapProducerBeh(15));
        kit.createAgent("ProducerWES", wrapProducerBeh(15));
        kit.createAgent("ProducerSES", wrapProducerBeh(1));

        Thread.sleep(1500);
        Assertions.assertEquals(2, inner.onEnd());
    }

    @Test
    @SneakyThrows
    void powerDivision(){
        kit.createAgent("Consumer3", wrapConsumerBeh());
        kit.createAgent("Distributor3", wrapDistributorBeh());

        kit.createAgent("ProducerTEC", wrapProducerBeh(6));
        kit.createAgent("ProducerWES", wrapProducerBeh(6));
        kit.createAgent("ProducerSES", wrapProducerBeh(1));

        Thread.sleep(1500);
        Assertions.assertEquals(1, inner.onEnd());
    }

    private Behaviour wrapConsumerBeh(){
        return new OneShotBehaviour() {

            @Override
            public void action() {
                inner = new ConsumerBehaviourTest(myAgent, 2);
                myAgent.addBehaviour(inner);
            }
        };
    }

    private Behaviour wrapDistributorBeh(){
        return new OneShotBehaviour() {

            @Override
            public void action() {
                DFHelper.registerAgent(myAgent, "Distributor");
                myAgent.addBehaviour(new Behaviour() {
//                    private Agent myAgent;
                    private MessageTemplate mt;
                    private double receivedPower;
                    private double receivedPrice;

                    @Override
                    public void action() {
                        JsonMessage jsonMessage = new JsonMessage();
                        mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
                        ACLMessage messageFromCons = myAgent.receive(mt);
                        if (messageFromCons != null) {
                            double requiredPower = jsonMessage.fromJson(messageFromCons.getContent()).getPowerAmount();
                            double requiredPrice = jsonMessage.fromJson(messageFromCons.getContent()).getPrice();

                            TopicCreator topicCreator = new TopicCreator(myAgent);
                            SendTopicName sendTN = new SendTopicName();
                            String topicName = myAgent.getLocalName()+"Topic";
                            AID jadeTopic = topicCreator.createTopic(topicName);
                            sendTN.sendTopicNameToProd(myAgent, topicName);
                            log.info("TOPIC CREATED. Topic is : {}", jadeTopic.getLocalName());


                            myAgent.addBehaviour( new TradingFSM(jadeTopic, messageFromCons.getSender(), requiredPower, requiredPrice));

                        } else {
                            block();
                        }
                    }

                    @Override
                    public boolean done() {
                        return false;
                    }
                });
            }
        };
    }

    private Behaviour wrapProducerBeh(double neededPower){
        return  new OneShotBehaviour() {
            @Override
            public void action() {
                DFHelper.registerAgent(myAgent, "Producer");
                myAgent.addBehaviour(new Behaviour() {
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

//                    private Agent myAgent;
                    private List<AID> jadeTopic = new ArrayList<>();
                    private AID currJadeTopic;
                    private double availablePower;
                    private double availablePrice;
                    private final JsonMessage jsonMessage = new JsonMessage();
                    private AvailablePower avPower = new AvailablePower();



                    @Override
                    public void onStart() {
                        availablePower = neededPower;
                        avPower.setAvailablePower(availablePower);
                        availablePrice = 5;
//                        log.info("{} started with availablePower = {}, availablePrice = {}", myAgent.getLocalName(), availablePower, availablePrice);
                    }

                    @Override
                    public void action() {
                        ACLMessage messageToSeller = myAgent.receive(mt);
                        if (messageToSeller != null) {
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
                                    awaitingFromTwo = false;
                                }

                                availablePower = neededPower;
                                availablePrice = 1;

                                if (availablePower >= receivedPower){
                                    log.info("{} send message to topic with availPower = {} and availPrice = {}", myAgent.getLocalName(), availablePower, availablePrice);
                                    myAgent.addBehaviour( new HandleCFP(messageToSeller, currJadeTopic, avPower, awaitingFromTwo, avPower.getAvailablePower(), availablePrice));
                                } else {
                                    log.info("{} send message to topic with availPower = {} and availPrice = {}", myAgent.getLocalName(), -1, availablePrice);
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
                });
            }
        };
    }


    @BeforeEach
    void beforeEach(){
        kit.startJade();
    }




//    {
//        myAgent.addBehaviour(new Behaviour() {
////                    private Agent myAgent;
//
//            private final ConsumerCFGClass consumerCFGClass = new ConsumerCFGClass();
//            private MessageTemplate mt;
//            private boolean stopBehaviour;
//            private double maximumPrice;
//            private double requiredPower;
//            private JsonMessage jsonMessage = new JsonMessage();
//            private StartAuction startAuction = new StartAuction();
//            private ACLMessage messageToDistr;
//
//
//            @Override
//            public void onStart() {
//                maximumPrice = 4.0;
//                requiredPower = 10;
//                String messageContent = jsonMessage.toJson(requiredPower, maximumPrice);
//                startAuction.sendMessageToDistributor(myAgent, messageContent);
//            }
//
//            @Override
//            public void action() {
//                mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
//                ACLMessage messageFromDistr = myAgent.receive(mt);
//                if (messageFromDistr != null) {
//                    double proposedPower = jsonMessage.fromJson(messageFromDistr.getContent()).getPowerAmount();
//                    double proposedPrice = jsonMessage.fromJson(messageFromDistr.getContent()).getPrice();
//
//                    if ((proposedPower >= requiredPower) && (proposedPrice >= maximumPrice)) {
//                        log.info("SUCCESSFUL OPERATION. Bought {} kVt for {} currency units", proposedPower, proposedPrice);
//                    } else if (proposedPrice < maximumPrice) {
////                                double firstMaximumPrice = maximumPrice;
////                                maximumPrice = maximumPrice*2;
////                                messageToDistr = new ACLMessage(ACLMessage.INFORM);
////                                startAuction.sendMessageToDistributor(myAgent, jsonMessage.toJson(requiredPower, maximumPrice));
//                        log.info("UNSUCCESSFUL OPERATION. Not enough money.");
//                    } else {
//                        log.info("UNSUCCESSFUL OPERATION. NO Power for {}", myAgent.getLocalName());
//                    }
//                    stopBehaviour = true;
//                }
//
//            }
//
//            @Override
//            public int onEnd() {
//                if (stopBehaviour) {
//                    log.info("return = 1");
//                    return 1;
//                }
//                log.info("return = 0");
//                return 0;
//            }
//
//            @Override
//            public boolean done() {
//                return stopBehaviour;
//            }
//        });
//    }



}