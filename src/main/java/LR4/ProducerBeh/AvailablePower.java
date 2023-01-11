package LR4.ProducerBeh;

import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.Queue;

public class AvailablePower {

    private double availablePower;
    private final int LIMIT = 1;
    private final Object lock = new Object();
    private Queue<Double> powerQueue = new LinkedList<>();

    public double getAvailablePower() {
        return availablePower;
    }

    public void setAvailablePower(double availablePower) {
        this.availablePower = availablePower;
    }

    public Queue<Double> getPowerQueue() {
        return powerQueue;
    }

    public void reducePower(double boughtPower) {
        synchronized (lock) {
            availablePower -= boughtPower;
//            lock.wait();
        }
//        lock.notify();
    }

    public void addToQueue(Double receivedPower) {
        powerQueue.add(receivedPower);
    }



}
