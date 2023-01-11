package LR4.TradingFSMBeh.SecondIteration;

import jade.core.AID;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import lombok.extern.slf4j.Slf4j;

/**
 * sends request and waits for responses. If timeout is over, then behaviour will be ended.
 */
@Slf4j
public class SendRequestsAndCollectBidsSecondTry extends ParallelBehaviour {

    private final double requiredPower;
    private final double requiredPrice;
    private final AID jadeTopic;
    private final BidsAnalyzerSecondTry bidsAnalyzer;

    public SendRequestsAndCollectBidsSecondTry(BidsAnalyzerSecondTry bidsAnalyzer, AID jadeTopic, double requiredPower, double requiredPrice) {
        super(ParallelBehaviour.WHEN_ANY);
        this.bidsAnalyzer = bidsAnalyzer;
        this.requiredPower = requiredPower;
        this.requiredPrice = requiredPrice;
        this.jadeTopic = jadeTopic;
    }

    @Override
    public void onStart() {
        addSubBehaviour(new CollectBidsWithTwoWinners(bidsAnalyzer, jadeTopic, requiredPower, requiredPrice));
        addSubBehaviour(new WakerBehaviour(myAgent, 5000) {
            @Override
            protected void onWake() {
            }
        });
        log.info("\nBeh CollectBids initialized in {}\n", this.getBehaviourName());
    }

    @Override
    public int onEnd() {
        log.info("ENDING BEH {}", this.getBehaviourName());
        if (bidsAnalyzer.getBestSeller() == null) {
            log.info("EVENT 4. THERE IS NO PRODUCER WITH ENOUGH POWER...");
            return 4;
        } else if (bidsAnalyzer.getBestSeller().size() > 1) {
            log.info("EVENT 3. PRODUCERS FOUND, TRYING TO MAKE A DEAL WITH THEM...");
            return 3;
        } else {
            log.info("EVENT 4. THERE IS NO PRODUCER WITH ENOUGH POWER...");
            return 4;
        }
    }
}
