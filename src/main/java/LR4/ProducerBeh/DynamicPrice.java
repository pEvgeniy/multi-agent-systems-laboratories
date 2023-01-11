package LR4.ProducerBeh;

public class DynamicPrice {

    private double availablePower;
    private double currPrice;
    private String agentName;

    public DynamicPrice(double currPrice, double availablePower, String agentName) {
        this.currPrice = currPrice;
        this.availablePower = availablePower;
        this.agentName = agentName;
    }

    public double priceCutter(double availablePower) {
        if (agentName.contains("TEC")) {
            if (currPrice > 5 && availablePower > 2) {
                currPrice = currPrice - 10/availablePower;
            } else {
                currPrice = 5;
            }
        }
        if (agentName.contains("WES")) {
            if (currPrice > 4 && availablePower > 3) {
                currPrice = currPrice - 10/availablePower;
            } else {
                currPrice = 4;
            }
        }
        if (agentName.contains("SES")) {
            if (currPrice > 3 && availablePower > 3) {
                currPrice = currPrice - 10/availablePower;
            } else {
                currPrice = 3;
            }
        }
        return currPrice;
    }

}
