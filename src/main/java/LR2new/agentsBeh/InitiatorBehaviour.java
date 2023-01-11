package LR2new.agentsBeh;

import LR2new.Methods;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;

public class InitiatorBehaviour extends Behaviour {
    private MessageTemplate mt;
    private MessageTemplate mt2;
    private Agent myAgent;
    private String solverReceiver1;
    private String solverReceiver2;
    private String funcResStr;
    private double X;
    private double delta;
    private boolean stopBehaviour;

    public InitiatorBehaviour(Agent myAgent, String solverReceiver1, String solverReceiver2, double X, double delta) {
        this.myAgent = myAgent;
        this.solverReceiver1 = solverReceiver1;
        this.solverReceiver2 = solverReceiver2;
        this.X = X;
        this.delta = delta;
    }

    @Override
    public void onStart() {
        System.out.println("\nNew initiator is "+myAgent.getLocalName()+"\n");

        System.out.println("Starting cycle with X = " + X + " (" + myAgent.getLocalName() + "_" + getBehaviourName() + ")");
        System.out.println("Starting cycle with delta = " + delta + " (" + myAgent.getLocalName() + "_" + getBehaviourName() + ")");

        ACLMessage arguments = new ACLMessage(ACLMessage.INFORM);
        AID n1 = new AID(solverReceiver1, false);
        AID n2 = new AID(solverReceiver2, false);
        arguments.addReceiver(n1);
        arguments.addReceiver(n2);
        String XDelta = X + ":" + delta;
        arguments.setContent(XDelta);
        myAgent.send(arguments); /*отправляем Х и Дельта двум другим агентам*/

        funcResStr = Methods.funcCounterAndParser(myAgent, X, delta); /*решаем уравнения и парсим результат в String*/
        System.out.println(myAgent.getLocalName() + " in action counts X = " + X + ", delta = " + delta + ", f(x) = " + funcResStr);

    }

    @Override
    public void action() {
        Methods.hidedWaiter();
        mt2 = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        ACLMessage message1 = myAgent.receive(mt2);
        ACLMessage message2 = myAgent.receive(mt2);

        if ((message1 != null)&&(message2 != null)) {

            List<Double> newXandDelta = Methods.xAndDeltaFinder(funcResStr, message1.getContent(), message2.getContent(), X, delta); /*находим новые Х и Дельта*/
            X = newXandDelta.get(0);
            System.out.println("New X = " + newXandDelta.get(0) + " (" + myAgent.getLocalName() + "_" + getBehaviourName() + ")");
            delta = newXandDelta.get(1);
            System.out.println("New delta = " + newXandDelta.get(1) + " (" + myAgent.getLocalName() + "_" + getBehaviourName() + ")\n");

            if (delta <= 0.01) {
                System.out.println("Last X (extremum) = " + X);
                System.out.println("Last delta = " + delta);
                stopBehaviour = true;
            } else {
                myAgent.addBehaviour(new InitiationQueueBehaviour(myAgent, newXandDelta.get(0), newXandDelta.get(1)));
                myAgent.removeBehaviour(this);
            }
//            stopBehaviour = true;

        } else {
            System.out.println("blocking " + myAgent.getLocalName() +  "'s action");
            block();
        }
    }

    @Override
    public int onEnd() {
        System.out.println(myAgent.getLocalName()+" ended behaviour "+getBehaviourName());
        return 0;
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
