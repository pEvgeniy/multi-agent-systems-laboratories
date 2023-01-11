package LR2new.agentsBeh;

import LR2new.Methods;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.concurrent.TimeUnit;

public class SolverBehaviour extends Behaviour {
    private MessageTemplate mt;
    private MessageTemplate mt2;
    private Agent myAgent;
    private String receiver;
    private boolean stopBehaviour;
    private double X;
    private double delta;

    public SolverBehaviour(Agent myAgent, String receiver) {
        this.myAgent = myAgent;
        this.receiver = receiver;
    }


    @Override
    public void action() {

        mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
        ACLMessage initiationUpdate = myAgent.receive(mt);
        if (initiationUpdate != null) {
            String updateMessage = initiationUpdate.getContent();
            myAgent.addBehaviour(new InitiationReceiverBehaviour(myAgent, updateMessage));
            myAgent.removeBehaviour(this);
        } else {
            block();
        }

        mt2 = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
        ACLMessage message = myAgent.receive(mt2);
        if (message != null) {
            String[] XDelta = message.getContent().split(":");
            X = Double.parseDouble(XDelta[0]);
            delta = Double.parseDouble(XDelta[1]);

            String funcResStr = Methods.funcCounterAndParser(myAgent, X, delta); /*решаем уравнения и парсим результат в String*/

            ACLMessage funcResults = new ACLMessage(ACLMessage.REQUEST);
            AID n = new AID(receiver, false); /*получатель receiverInitiator*/
            System.out.println(myAgent.getLocalName()+"'s receiver = "+receiver);
            funcResults.addReceiver(n);
            funcResults.setContent(funcResStr); /*заворачиваем 3 числа в формает String с разделением через :*/
            myAgent.send(funcResults);

            System.out.println(myAgent.getLocalName() + " in action counts X = " + X + ", delta = " + delta + ", f(x) = " + funcResStr);

        } else {
//            System.out.println("blocking " + myAgent.getLocalName() +  "'s action");
            block();
        }

    }

    @Override
    public int onEnd() {
        System.out.println(myAgent.getLocalName() + " ended behaviour "+getBehaviourName());
        return 0;
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }

}
