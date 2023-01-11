package LR2new.agentsBeh;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InitiationQueueBehaviour extends Behaviour {
    private Agent myAgent;
    private double X;
    private double delta;
    private boolean stopBehaviour;
    private String newReceiver;
    private String secondSolver;

    public InitiationQueueBehaviour(Agent myAgent, double X, double delta) {
        this.myAgent = myAgent;
        this.X = X;
        this.delta = delta;
    }

    @Override
    public void action() {
        List<String> availableAgents = new ArrayList<>();
        availableAgents.add("FirstSolver");
        availableAgents.add("SecondSolver");
        availableAgents.add("ThirdSolver");

        if (myAgent.getLocalName().equals("FirstSolver")) {    /*пытаемся понять, кого делать инициатором*/
            newReceiver = Math.random()>0.5 ? "SecondSolver": "ThirdSolver";
        } else if (myAgent.getLocalName().equals("SecondSolver")) {
            newReceiver = Math.random()>0.5 ? "FirstSolver": "ThirdSolver";
        } else if (myAgent.getLocalName().equals("ThirdSolver")) {
            newReceiver = Math.random()>0.5 ? "FirstSolver": "SecondSolver";
        }

        for (String agent : availableAgents) {
            if ((!agent.equals(myAgent.getLocalName()))&&(!agent.equals(newReceiver))) {
                secondSolver = agent;
            }
        }

        myAgent.addBehaviour(new SolverBehaviour(myAgent, newReceiver)); /*добавляем поведение текущему иницатору, чтобы он стал обычным солвером*/

        ACLMessage messageToNewInitiator = new ACLMessage(ACLMessage.AGREE); /*создаем сообщение для нового инициатора (receiver)*/
        AID newInitiator = new AID(newReceiver, false);
        AID untouchedSolver = new AID(secondSolver, false);
        messageToNewInitiator.addReceiver(newInitiator);
        messageToNewInitiator.addReceiver(untouchedSolver);
        messageToNewInitiator.setContent(newReceiver+":"+X+":"+delta); /*передаем в сообщении имя нового инициатора*/
        myAgent.send(messageToNewInitiator);

        stopBehaviour = true;
    }

    @Override
    public int onEnd() {
        System.out.println(myAgent.getLocalName() + " made new initiator -> "+newReceiver);
        return 0;
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
