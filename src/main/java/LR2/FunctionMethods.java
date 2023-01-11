package LR2;

import java.util.ArrayList;
import java.util.List;

public class FunctionMethods {

    public static double x;
    public static List<Double> functionsList(double x){
        double function1 = -0.5 * Math.pow(x, 2) - 4,
                function2 = Math.pow(2, -0.1*x),
                function3 = Math.cos(x * Math.PI/180);

        List<Double> functions = new ArrayList<>(3);
        functions.add(function1);
        functions.add(function2);
        functions.add(function3);
        return functions;
    }

    public static List<Double> Solver(double func, double X, double delta) {

        int funcInd = functionsList(X).lastIndexOf(func);
        if (X == 0 && delta == 0) {
            X = Math.random() * 10;
            delta = 1;
        }

        List<Double> firstFuncResults = new ArrayList<>(3);

        /*Находим значения функции при X-d, X, X+d и записываем в новый лист*/
        for (int i = -1; i < 2; i++) {
            double x = X + i;
            firstFuncResults.add(functionsList(x).get(funcInd));
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
}
