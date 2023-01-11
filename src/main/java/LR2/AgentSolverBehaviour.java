package LR2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AgentSolverBehaviour extends Behaviour {

    private boolean stopBehaviour = false;
    private Agent myAgent;
    private MessageTemplate mt;
    private String initiatorAgent;
    public AgentSolverBehaviour(Agent myAgent, String initiatorAgent) {
        this.myAgent = myAgent;
        this.initiatorAgent = initiatorAgent;
    }



    @Override
    public void action() {
        ACLMessage arguments = myAgent.receive(mt);
        if(arguments != null){
            String[] XDelta = arguments.getContent().split(":");
            double X = Double.parseDouble(XDelta[0]), delta = Double.parseDouble(XDelta[1]);

            List<Double> fuuRes = new ArrayList<>();
            if (myAgent.getLocalName().equals("FirstAgentSolver")) {
                fuuRes = FunctionMethods.Solver(FunctionMethods.functionsList(X).get(1), X, delta);
            } else if (myAgent.getLocalName().equals("SecondAgentSolver")) {
                fuuRes = FunctionMethods.Solver(FunctionMethods.functionsList(X).get(2), X, delta);
            }

            String fuuResStr = fuuRes.toString();
            fuuResStr = fuuResStr.replace("[", "")
                    .replace("]", "")
                    .replace(", ", ":");

            ACLMessage FuncResults = new ACLMessage(ACLMessage.REQUEST);
            AID n = new AID(initiatorAgent, false); /*получатель initiatorAgent*/
            FuncResults.addReceiver(n);
            FuncResults.setContent(fuuResStr); /*заворачиваем 3 числа в формает String с разделением через :*/
            myAgent.send(FuncResults);

            System.out.println(myAgent.getLocalName() + " in action counts X = " + X + ", delta = " + delta + ", f(x) = " + fuuResStr);

            if (delta <= 0.01) {
                stopBehaviour = true;
            }
        } else {
            block();
        }

    }

    @Override
    public int onEnd() {
        try {
            TimeUnit.MICROSECONDS.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(myAgent.getLocalName() + " died :(");
        return 0;
    }

    @Override
    public boolean done() {
        return stopBehaviour;
    }
}
