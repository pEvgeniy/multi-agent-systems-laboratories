package LR4.TradingFSMBeh;

import LR4.TradingFSMBeh.FirstIteration.BidsAnalyzer;
import LR4.TradingFSMBeh.FirstIteration.ConcludeContract;
import LR4.TradingFSMBeh.FirstIteration.SendRequestsAndCollectBids;
import LR4.TradingFSMBeh.SecondIteration.BidsAnalyzerSecondTry;
import LR4.TradingFSMBeh.SecondIteration.ConcludeContractWithTwoWinners;
import LR4.TradingFSMBeh.SecondIteration.SendRequestsAndCollectBidsSecondTry;
import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TradingFSM extends FSMBehaviour {
    private static final String COLLECT_BIDS="collect_bids", COLLECT_BIDS_SECOND_TRY="collect_bids_second_try",
            DEAL_WITH_WINNER="deal_with_winner", DEAL_WITH_TWO_WINNERS="deal_with_two_winners", REQ_TO_MAKE_PRICE_HIGHER="req_to_make_price_higher",
            END="end", BAD_ENDING="bad_ending", COLLECT_BIDS_AGAIN ="collect_bids_again", DEAL_WITH_WINNER_AGAIN="deal_with_winner_again";
    private final double requiredPower;
    private final double requiredPrice;
    private final AID jadeTopic;
    private final AID consumerAID;

    public TradingFSM(AID jadeTopic, AID consumerAID, double requiredPower, double requiredPrice) {
        this.requiredPower = requiredPower;
        this.requiredPrice = requiredPrice;
        this.jadeTopic = jadeTopic;
        this.consumerAID = consumerAID;
    }

    @Override
    public void onStart() {
        BidsAnalyzer bidsAnalyzer = new BidsAnalyzer();
        BidsAnalyzerSecondTry bidsAnalyzerSecondTry = new BidsAnalyzerSecondTry(requiredPower/2);

        this.registerFirstState( new SendRequestsAndCollectBids(bidsAnalyzer, jadeTopic, requiredPower, requiredPrice), COLLECT_BIDS);

        this.registerState( new ConcludeContract(bidsAnalyzer, consumerAID, requiredPower, requiredPrice), DEAL_WITH_WINNER);

        this.registerState( new SendRequestsAndCollectBidsSecondTry(bidsAnalyzerSecondTry, jadeTopic, requiredPower/2, requiredPrice), COLLECT_BIDS_SECOND_TRY);

        this.registerState( new ConcludeContractWithTwoWinners(bidsAnalyzerSecondTry, consumerAID, requiredPower/2, requiredPrice), DEAL_WITH_TWO_WINNERS);

        this.registerState( new RequestToMakePriceHigher(consumerAID, requiredPower, requiredPrice), REQ_TO_MAKE_PRICE_HIGHER);

        this.registerState( new SendBackToConsumer(consumerAID, -1, requiredPrice), BAD_ENDING);

        this.registerState( new SendRequestsAndCollectBids(bidsAnalyzer, jadeTopic, requiredPower, requiredPrice), COLLECT_BIDS_AGAIN);

        this.registerState( new ConcludeContract(bidsAnalyzer, consumerAID, requiredPower, requiredPrice), DEAL_WITH_WINNER_AGAIN);

        this.registerLastState(new OneShotBehaviour() {
            @Override
            public void action() {
                log.info("FSM is OVER");
            }
        }, END);


        this.registerTransition(COLLECT_BIDS, DEAL_WITH_WINNER, 1);
        this.registerTransition(COLLECT_BIDS, COLLECT_BIDS_SECOND_TRY, 2);
        this.registerTransition(DEAL_WITH_WINNER, COLLECT_BIDS_AGAIN, 5);

        this.registerTransition(COLLECT_BIDS_AGAIN, DEAL_WITH_WINNER_AGAIN, 1);
        this.registerTransition(COLLECT_BIDS_AGAIN, COLLECT_BIDS_SECOND_TRY, 2);
        this.registerTransition(DEAL_WITH_WINNER_AGAIN, COLLECT_BIDS_AGAIN, 2);

        this.registerTransition(COLLECT_BIDS_SECOND_TRY, DEAL_WITH_TWO_WINNERS, 3);
        this.registerTransition(COLLECT_BIDS_SECOND_TRY, BAD_ENDING, 4);

        this.registerTransition(COLLECT_BIDS, REQ_TO_MAKE_PRICE_HIGHER, 6);
        this.registerTransition(COLLECT_BIDS_AGAIN, REQ_TO_MAKE_PRICE_HIGHER, 6);
        this.registerTransition(COLLECT_BIDS_SECOND_TRY, REQ_TO_MAKE_PRICE_HIGHER, 7);

        this.registerDefaultTransition(DEAL_WITH_WINNER, END);
        this.registerDefaultTransition(DEAL_WITH_WINNER_AGAIN, END);
        this.registerDefaultTransition(DEAL_WITH_TWO_WINNERS, END);
        this.registerDefaultTransition(REQ_TO_MAKE_PRICE_HIGHER, END);
        this.registerDefaultTransition(BAD_ENDING, END);

//        this.registerDefaultTransition(GOOD_ENDING, END);
//        this.registerDefaultTransition(BAD_ENDING, END);
    }
}
