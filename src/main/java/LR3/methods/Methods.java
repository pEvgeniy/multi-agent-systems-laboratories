package LR3.methods;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class Methods {
    public static int parser(String NodeChain) {
        int routeLength = 0;
        String[] chain = NodeChain.split(";");
        for(int i = 1; i < chain.length; i++) {
            routeLength += Integer.parseInt(chain[i].split("_")[1]);
        }
        return routeLength;
    }

    public static void shortestRouteFinder(List<Integer> routes, List<String> chains) {
        List<Integer> sortedRoutes = new ArrayList<>(routes);
        Collections.sort(sortedRoutes);

        int shortest = sortedRoutes.get(0);
        Map<Integer, String> routeMap = new TreeMap<>(); /*[2, a_1;a_2],[2, a_1;a_2],[5, a_1;a_2]*/
        for (int i = 0; i < routes.size(); i++) {
            routeMap.put(routes.get(i), chains.get(i));
        }

        for (Map.Entry<Integer, String> item : routeMap.entrySet()) {
            if (item.getKey() == shortest) {
                String destination = item.getValue().split(";")[0];
                String finalRoute = resultParser(item.getValue(),destination);
                log.info("Shotrest route weight is {} which ends in {} and it's route is: {}", item.getKey(), destination, finalRoute);
            } else {
                break;
            }
        }
    }

    public static String resultParser(String route, String destination) {
        route = route.replace(destination + ";","");
        String[] tempRouteWithWeight = route.split(";");
        String newRoute = "";
        for (int i = 0; i < tempRouteWithWeight.length; i++) {
            newRoute += tempRouteWithWeight[i].split("_")[0] + " -> ";
        }
        newRoute += destination;
        return newRoute;
    }
}
