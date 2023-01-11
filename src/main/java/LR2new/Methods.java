package LR2new;

import jade.core.Agent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Methods {
    public static double x;
    public static List<Double> functionsList(double x){
        double function1 = -0.5 * Math.pow(x, 2) - 4,
                function2 = Math.pow(2, -0.1*x),
                function3 = Math.cos(x);

        List<Double> functions = new ArrayList<>(3);
        functions.add(function1);
        functions.add(function2);
        functions.add(function3);
        return functions;
    }

    public static List<Double> Solver(double func, double X, double delta) {

        int funcInd = functionsList(X).lastIndexOf(func);

        List<Double> firstFuncResults = new ArrayList<>(3);
        double scale = Math.pow(10,3);
        /*Находим значения функции при X-d, X, X+d и записываем в новый лист*/
        for (int i = -1; i < 2; i++) {
            double newX = X + i*delta;
            firstFuncResults.add(Math.ceil(functionsList(newX).get(funcInd)*scale)/scale);
        }
        return firstFuncResults;
    }

    public static List<Double> xAndDeltaFinder(String firstFuncRes, String secondFuncRes,String thirdFuncRes, double X, double delta) {
        String[] funcList = {firstFuncRes, secondFuncRes, thirdFuncRes};
        double firstFuncSum = 0, secondFuncSum = 0, thirdFuncSum = 0;
        for (String fuu : funcList) {
            String[] funcToDouble = fuu.split(":");
            firstFuncSum += Double.parseDouble(funcToDouble[0]);
            secondFuncSum += Double.parseDouble(funcToDouble[1]);
            thirdFuncSum += Double.parseDouble(funcToDouble[2]);
        }
        double maxFuncSum = secondFuncSum;

        if (firstFuncSum > maxFuncSum) {
            X = X - delta;
        } else if (thirdFuncSum > maxFuncSum) {
            X = X + delta;
        } else {
            delta = delta/2;
        }

        List<Double> newXAndDelta = new ArrayList<>();
        newXAndDelta.add(X);
        newXAndDelta.add(delta);
        return newXAndDelta;
    }

    public static String funcCounterAndParser(Agent myAgent, double X, double delta) {
        List<Double> funcRes = new ArrayList<>();
        if (myAgent.getLocalName().equals("FirstSolver")) {
            funcRes = Solver(functionsList(X).get(0), X, delta);
        } else if (myAgent.getLocalName().equals("SecondSolver")) {
            funcRes = Solver(functionsList(X).get(1), X, delta);
        } else if (myAgent.getLocalName().equals("ThirdSolver")) {
            funcRes = Solver(functionsList(X).get(2), X, delta);
        }

        String funcResStr = funcRes.toString();
        funcResStr = funcResStr.replace("[", "")
                .replace("]", "")
                .replace(", ", ":");
        return funcResStr;
    }

    public static void hidedWaiter(){
        try {
            TimeUnit.MICROSECONDS.sleep(15000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
