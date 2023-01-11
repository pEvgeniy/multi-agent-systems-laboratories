package LR2new;


import LR2new.agentsBeh.InitiatorBehaviour;
import LR2new.agentsBeh.SolverBehaviour;
import jade.core.Agent;

public class FunctionAgent extends Agent {

    @Override
    protected void setup() {
//        -gui -agents FirstSolver:LR2new.FunctionAgent;SecondSolver:LR2new.FunctionAgent;ThirdSolver:LR2new.FunctionAgent;

        System.out.println(this.getName()+" initialised");

        String solverName1 = "FirstSolver";
        String solverName2 = "SecondSolver";
        String solverName3 = "ThirdSolver";

        if (this.getLocalName().equals(solverName1)) {
            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            double X = Math.ceil(Math.random()*10), delta = 1;
            this.addBehaviour(new InitiatorBehaviour(this, solverName2, solverName3, X, delta));
        } else {
            this.addBehaviour(new SolverBehaviour(this, solverName1));
        }
    }
}
