package LR2;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WakerAgentBehaviour extends WakerBehaviour {

    private boolean stopBehaviour = false;
    private Agent myAgent;
    private Date myDate;
    private MessageTemplate mt;
    private String initiatorAgent;

    public WakerAgentBehaviour(Agent a, long timeout) {
        super(a, timeout);
    }


    @Override
    protected void onWake() {
        ACLMessage arguments = myAgent.receive(mt);
        if(arguments != null){
            String[] XDelta = arguments.getContent().split(":");
            double X = Double.parseDouble(XDelta[0]), delta = Double.parseDouble(XDelta[1]);
            List<Double> fuuRes = new ArrayList<>();

            if (myAgent.getLocalName().equals("FirstAgentSolver")) {
                fuuRes = FunctionMethods.Solver(FunctionMethods.functionsList(X).get(1), X, delta); /*расчет функции*/
            } else if (myAgent.getLocalName().equals("SecondAgentSolver")) {
                fuuRes = FunctionMethods.Solver(FunctionMethods.functionsList(X).get(2), X, delta); /*расчет функции*/
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

            System.out.println(myAgent.getLocalName() + " in action counts X = " + X + ", delta = " + delta + " and f(x) = |X-d:X:X+d| = " + fuuResStr);

            if (delta <= 0.01) {
                stopBehaviour = true;
            }
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
        System.out.println(myAgent.getLocalName()+" died :(");
        return 0;
    }

}
