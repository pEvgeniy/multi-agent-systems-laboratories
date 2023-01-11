package LR2;

import jade.core.Agent;

import java.util.*;

public class FunctionAgent extends Agent{


    @Override
    protected void setup() {
//        -gui -agents InitiatorSolver:LR2.FunctionAgent;FirstAgentSolver:LR2.FunctionAgent;SecondAgentSolver:LR2.FunctionAgent;

        System.out.println("Hello, i'm "+this.getName());
        String iName = "InitiatorSolver";
        String sName1 = "FirstAgentSolver";
        String sName2 = "SecondAgentSolver";

        if (this.getLocalName().equals(iName)) {
            this.addBehaviour(new AgentInitiatorBehaviour(this, sName1, sName2));
        } else {
            this.addBehaviour(new AgentSolverBehaviour(this, iName));
        }
    }
}
