package LR4.TradingFSMBeh.FirstIteration;

import LR4.JSON.JsonMessage;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class BidsAnalyzer {
    private double bestPrice;
    private double bestPower;
    private AID bestSeller;
    private boolean success;

    public void putBid(ACLMessage message){
        JsonMessage jsonMessage = new JsonMessage();
        String content = message.getContent();
        double power = jsonMessage.fromJson(content).getPowerAmount();
        double price = jsonMessage.fromJson(content).getPrice();

        if (power <= 0){
            log.info("received refuse with power = {} and price = {} from {}", power, price, message.getSender().getLocalName());
            return;
        } else {
            log.info("received propose with power = {} and price = {} from {}", power, price, message.getSender().getLocalName());
        }

        if (bestSeller == null || price < bestPrice){
            bestPrice = price;
            bestPower = power;
            bestSeller = message.getSender();
            log.info("BidAnalyzer putted a bid = {}", bestSeller.getLocalName());
        }
    }

    public void refreshBids() {
        bestPower = 0;
        bestPrice = 0;
        bestSeller = null;
    }

    public Optional<AID> getBestSeller(){
        return bestSeller == null ? Optional.empty() : Optional.of(bestSeller);
    }

    public double getBestPrice() {
        return bestPrice;
    }

    public double getBestPower() {
        return bestPower;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
