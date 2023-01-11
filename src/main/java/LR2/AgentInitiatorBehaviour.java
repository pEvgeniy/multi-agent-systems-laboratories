package LR2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AgentInitiatorBehaviour extends Behaviour {

    private MessageTemplate mt;
    private Agent myAgent;
    private String neighbourAgent1;
    private String neighbourAgent2;
    private boolean doneFlag;
    private String fuuResStr;
    private double X, delta;

    public AgentInitiatorBehaviour(Agent myAgent, String neighbourAgent1, String neighbourAgent2) {
        this.myAgent = myAgent;
        this.neighbourAgent1 = neighbourAgent1;
        this.neighbourAgent2 = neighbourAgent2;
    }


    @Override
    public void onStart() { /*На старте выбираем рандомные Х и delta, отправляем их агентам, решаем три функции*/
        X = Math.ceil(Math.random() * 10);
        System.out.println("Randomly generated X = " + X);
        delta = 1.0;
        System.out.println("Randomly generated delta = " + delta);
        ACLMessage arguments = new ACLMessage(ACLMessage.REQUEST);
        AID n1 = new AID(neighbourAgent1, false); /*получатель №1*/
        AID n2 = new AID(neighbourAgent2, false); /*получатель №2*/

        List<Double> firstFuncRes = FunctionMethods.Solver(FunctionMethods.functionsList(X).get(0), X, delta);
        fuuResStr = firstFuncRes.toString();
        fuuResStr = fuuResStr.replace("[", "")
                .replace("]", "")
                .replace(", ", ":");

        arguments.addReceiver(n1);
        arguments.addReceiver(n2);

        String XDelta = X + ":" + delta;
        arguments.setContent(XDelta);
        myAgent.send(arguments);

        System.out.println(myAgent.getLocalName() + " on start counts X = " + X + ", delta = " + delta + ", f(x) = " + fuuResStr);
    }

    @Override
    public void action() {
        try {
            TimeUnit.MICROSECONDS.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ACLMessage message1 = myAgent.receive(mt);
        ACLMessage message2 = myAgent.receive(mt);

        if ((message1 != null)&&(message2 != null)) {

            List<Double> newXandDelta = FunctionMethods.xAndDeltaFinder(fuuResStr, message1.getContent(), message2.getContent(), X, delta);
            X = newXandDelta.get(0);
            System.out.println("New X = " + newXandDelta.get(0));
            delta = newXandDelta.get(1);
            System.out.println("New delta = " + newXandDelta.get(1));

            ACLMessage arguments = new ACLMessage(ACLMessage.REQUEST);
            AID n1 = new AID(neighbourAgent1, false); /*получатель №1*/
            AID n2 = new AID(neighbourAgent2, false); /*получатель №2*/

            List<Double> fuuRes = FunctionMethods.Solver(FunctionMethods.functionsList(X).get(0), X, delta);

            fuuResStr = fuuRes.toString();
            fuuResStr = fuuResStr.replace("[", "")
                    .replace("]", "")
                    .replace(", ", ":");

            arguments.addReceiver(n1);
            arguments.addReceiver(n2);

            String XDelta = X + ":" + delta;
            arguments.setContent(XDelta);
            myAgent.send(arguments);

            System.out.println(myAgent.getLocalName() + " in action counts X = " + X + ", delta = " + delta + ", f(x) = " + fuuResStr);

        } else {
            System.out.println("block");
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
        try {
            TimeUnit.MICROSECONDS.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (delta <= 0.01) {
            System.out.println("Last X = " + X);
            System.out.println("Last delta = " + delta);
            doneFlag = true;
        }
        return doneFlag;
    }
}
