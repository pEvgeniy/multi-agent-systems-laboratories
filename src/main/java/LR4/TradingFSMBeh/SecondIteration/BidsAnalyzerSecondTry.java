package LR4.TradingFSMBeh.SecondIteration;

import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class BidsAnalyzerSecondTry {
    private final List<Double> bestPrice = new ArrayList<>();
    private final List<Double> bestPower = new ArrayList<>();
    private final List<AID> bestSeller = new ArrayList<>();
    private boolean success;
    private Double requiredPower;

    public BidsAnalyzerSecondTry(double requiredPower) {
        this.requiredPower = requiredPower;
    }

    public void putBid(ACLMessage message){
        JsonMessage jsonMessage = new JsonMessage();
        String content = message.getContent();
        double power = jsonMessage.fromJson(content).getPowerAmount();
        double price = jsonMessage.fromJson(content).getPrice();

        if (power <= 0){
            log.info("received refuse with power =  {}", power);
            return;
        } else {
            log.info("received propose with power =  {}", power);
        }

        bestPrice.add(price);
        bestPower.add(power);
        bestSeller.add(message.getSender());

        log.info("BidAnalyzer made bids = {}", bestSeller.get(bestSeller.size()-1).getLocalName());
    }

    public void findMinPrices() {
        if (bestSeller.size() == 0 && bestPower.size() == 0) {
            log.info("NOT ENOUGH POWER");
        } else if (bestSeller.size() == 1 && bestPower.get(0) >= requiredPower) {

        } else if (bestSeller.size() == 2) {

        } else {
            List<Double> tmpList = new ArrayList<>();
            tmpList.addAll(bestPrice);
            Collections.sort(tmpList);
            List<Double> minPrices = new ArrayList<>(2);
            for (int i = 0; i < 2; i++) {
                minPrices.add(tmpList.get(i));
            }
            int ind = 0;
            while (bestPrice.size() != 2) {
                if (!minPrices.contains(bestPrice.get(ind))) {
                    bestPrice.remove(ind);
                    bestPower.remove(ind);
                    bestSeller.remove(ind);
                } else {
                    ind++;
                }
            }
        }
    }

    @SneakyThrows
    public List<AID> getBestSeller(){
        return bestSeller.size() == 0 ? null : bestSeller;
    }

    public List<Double> getBestPrice() {
        return bestPrice;
    }

    public List<Double> getBestPower() {
        return bestPower;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
