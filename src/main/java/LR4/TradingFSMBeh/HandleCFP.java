package LR4.TradingFSMBeh;

import LR4.JSON.JsonMessage;
import LR4.ProducerBeh.AvailablePower;
import LR4.TopicManager.MessageReceiver;
import LR4.TopicManager.ReceivingFromTopic;
import LR4.TopicManager.SendingToTopic;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HandleCFP extends Behaviour {
    private final ACLMessage cfp;
    private boolean replySent = false;
    private final double availablePrice;
    private double availablePower;
    private double boughtPower;
    private AID jadeTopic;
    private AvailablePower avPower;
    private final JsonMessage jsonMessage = new JsonMessage();
    private final boolean awaitingFromTwo;

    private MessageTemplate mt = MessageTemplate.and(
            MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
            MessageTemplate.or(
                    MessageTemplate.MatchProtocol("buying"),
                    MessageTemplate.MatchProtocol("confirming")
            )
    );

    public HandleCFP(ACLMessage cfp, AID jadeTopic, AvailablePower avPower, boolean awaitingFromTwo, double availablePower, double minimumPrice) {
        this.cfp = cfp;
        this.availablePrice = minimumPrice;
        this.availablePower = availablePower;
        this.jadeTopic = jadeTopic;
        this.avPower = avPower;
        this.awaitingFromTwo = awaitingFromTwo;
    }

    @Override
    public void onStart() {
        myAgent.addBehaviour(new SendingToTopic(myAgent,10, jadeTopic, "buying", 11, availablePower, availablePrice));
//        log.info("{} sent to topic availablePower = {}, availablePrice = {}", myAgent.getLocalName(), availablePower, availablePrice);
    }

    @Override
    public void action() {
        ACLMessage req = myAgent.receive(mt);
        if (req != null){
            if (req.getProtocol().equals("buying")) {
                String content = req.getContent();
                double receivedPower = jsonMessage.fromJson(content).getPowerAmount();
                double receivedPrice = jsonMessage.fromJson(content).getPrice();

                boughtPower = receivedPower;

                ACLMessage reply = req.createReply();

                avPower.addToQueue(receivedPower);


                if (avPower.getPowerQueue().size() > 0 && !awaitingFromTwo) {
                    Double firstPowerInQueue = avPower.getPowerQueue().element();
                    log.info("{} bid from {} added to queue", firstPowerInQueue, req.getSender().getLocalName());
                    if (receivedPrice != 0 && receivedPrice >= availablePrice && (0 < firstPowerInQueue && firstPowerInQueue <= avPower.getAvailablePower())){
                        log.info("Power reduced from {} to {}", avPower.getAvailablePower(), avPower.getAvailablePower() - firstPowerInQueue);
                        avPower.reducePower(boughtPower);
                        reply.setPerformative(ACLMessage.AGREE);
                        log.info("{} send AGREE to {}", myAgent.getLocalName(), req.getSender().getLocalName());
                    } else {
                        reply.setPerformative(ACLMessage.REFUSE);
                        log.info("{} send REFUSE to {}", myAgent.getLocalName(), req.getSender().getLocalName());
                    }
                    avPower.getPowerQueue().poll();
                }
                if (avPower.getPowerQueue().size() > 0 && awaitingFromTwo) {
                    int receivesCounter = 0;
                    Double firstPowerInQueue = avPower.getPowerQueue().element();
                    log.info("{} bid from {} added to queue with 2 expected", firstPowerInQueue, req.getSender().getLocalName());
                    if (receivedPrice != 0 && receivedPrice >= availablePrice && (0 < firstPowerInQueue && firstPowerInQueue <= avPower.getAvailablePower())){
                        receivesCounter++;
                    }
                    avPower.getPowerQueue().poll();

                    if (receivesCounter == 2 && avPower.getPowerQueue().size() == 0) {
                        for (int i = 0; i < 2; i++) {
                            log.info("Power reduced from {} to {}", avPower.getAvailablePower(), avPower.getAvailablePower() - firstPowerInQueue);
                            avPower.reducePower(boughtPower);
                            reply.setPerformative(ACLMessage.AGREE);
                            log.info("{} send AGREE to {}", myAgent.getLocalName(), req.getSender().getLocalName());
                        }
                    } else if (receivesCounter == 1 && avPower.getPowerQueue().size() == 0) {
                        for (int i = 0; i < 2; i++) {
                            reply.setPerformative(ACLMessage.REFUSE);
                            log.info("{} send REFUSE to {}", myAgent.getLocalName(), req.getSender().getLocalName());
                        }
                    }
                }

                myAgent.send(reply);
            }
            if (req.getProtocol().equals("confirming")) {
//                try {
//                    avPower.reducePower(boughtPower);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
                log.info("{} reduced his power to {}", myAgent.getLocalName(), avPower.getAvailablePower());
                ACLMessage messageToProd = new ACLMessage(ACLMessage.INFORM);
                messageToProd.setProtocol("reducing_power");
                messageToProd.addReceiver(myAgent.getAID());
                myAgent.send(messageToProd);
                replySent = true;
            }
        } else {
            block();
        }

    }

    @Override
    public boolean done() {
        if (availablePrice < 0 || availablePower < 0 || replySent) {
//            log.info("{} is done", getBehaviourName());
        }
        return availablePrice < 0 || availablePower < 0 || replySent;
    }
}