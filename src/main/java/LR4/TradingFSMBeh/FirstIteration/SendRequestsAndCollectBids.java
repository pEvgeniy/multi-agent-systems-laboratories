package LR4.TradingFSMBeh.FirstIteration;

import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import lombok.extern.slf4j.Slf4j;

/**
 * sends request and waits for responses. If timeout is over, then behaviour will be ended.
 */
@Slf4j
public class SendRequestsAndCollectBids extends ParallelBehaviour {

    private final double requiredPower;
    private final double requiredPrice;
    private final BidsAnalyzer bidsAnalyzer;
    private final AID jadeTopic;
    private int cycleCounter;

    public SendRequestsAndCollectBids(BidsAnalyzer bidsAnalyzer, AID jadeTopic, double requiredPower, double requiredPrice) {
        super(ParallelBehaviour.WHEN_ANY);
        this.bidsAnalyzer = bidsAnalyzer;
        this.requiredPower = requiredPower;
        this.requiredPrice = requiredPrice;
        this.jadeTopic = jadeTopic;
    }

    @Override
    public void onStart() {
//        log.info("{} started", getBehaviourName());
        addSubBehaviour(new CollectBids(bidsAnalyzer, jadeTopic, requiredPower, requiredPrice));
        addSubBehaviour(new WakerBehaviour(myAgent, 5000) {
            @Override
            protected void onWake() {
            }
        });
//        log.info("Beh CollectBids initialized in {}", this.getBehaviourName());
    }

    @Override
    public int onEnd() {
        cycleCounter += 1;
        if (bidsAnalyzer.getBestSeller().isPresent() && bidsAnalyzer.getBestPrice() <= requiredPrice) {
            log.info("EVENT 1. PRODUCER FOUND, TRYING TO MAKE A DEAL...");
            return 1;
        } else if (bidsAnalyzer.getBestPrice() > requiredPrice) {
            log.info("EVENT 6. PRODUCER FOUND, BUT PRICE IS TOO HIGH. REQUESTING MORE MONEY FROM CONSUMER...");
            return 6;
        } else if (bidsAnalyzer.getBestSeller().isEmpty() && cycleCounter != 2) {
            log.info("EVENT 2. THERE IS NO PRODUCER WITH ENOUGH POWER. DEVIDING OUR CONTRACT INTO 2...");
            return 2;
        }/* else if (bidsAnalyzer.getBestSeller().isPresent() && cycleCounter == 2) {
            log.info("EVENT 3");
            return 3;
        } else if (bidsAnalyzer.getBestSeller().isEmpty() && cycleCounter == 2) {
            log.info("EVENT 4");
            return 4;
        }*/
        return 0;
    }
}
