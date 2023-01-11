package LR2new.agentsBeh;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.MessageTemplate;


public class InitiationReceiverBehaviour extends Behaviour {
    private String updateMessage;
    private MessageTemplate mt;
    private Agent myAgent;
    private double X;
    private double delta;
    private boolean stopBehaviour;
    private String newReceiver1;
    private String newReceiver2;

    public InitiationReceiverBehaviour(Agent myAgent, String updateMessage) {
        this.myAgent = myAgent;
        this.updateMessage = updateMessage;
    }


    @Override
    public void action() {

        if (updateMessage.contains(myAgent.getLocalName())) {
            if (myAgent.getLocalName().contains("FirstSolver")) { /*пытаемся понять, кого делать ресивером*/
                newReceiver1 = "SecondSolver";
                newReceiver2 = "ThirdSolver";
            } else if (myAgent.getLocalName().contains("SecondSolver")) {
                newReceiver1 = "FirstSolver";
                newReceiver2 = "ThirdSolver";
            } else if (myAgent.getLocalName().contains("ThirdSolver")) {
                newReceiver1 = "FirstSolver";
                newReceiver2 = "SecondSolver";
            }
            String[] parsedMessage = updateMessage.split(":");
            X = Double.parseDouble(parsedMessage[1]);
            delta = Double.parseDouble(parsedMessage[2]);
            myAgent.addBehaviour( new InitiatorBehaviour(myAgent, newReceiver1, newReceiver2, X, delta)); /*добавляем поведение инициатору агетну*/
            System.out.println(myAgent.getLocalName()+" changes behaviour into InitiatorBehaviour");
        } else {
            myAgent.addBehaviour( new SolverBehaviour(myAgent, updateMessage.split(":")[0]));
            System.out.println(myAgent.getLocalName()+" changes behaviour into SolverBehaviour");
        }
        stopBehaviour = true;
    }

    @Override
    public int onEnd() {
        System.out.println(myAgent.getLocalName()+" is now initiator");
        return 0;
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
