package LR4.Time;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimeSimulation {

    private long startMillis;
    private int time;
    private ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
    private final int oneHourInMillis = 10000;
    private long currHourInMillis;

    public TimeSimulation(long startMillis) {
        this.startMillis = startMillis;
        ses.scheduleAtFixedRate(() -> time++, oneHourInMillis, oneHourInMillis, TimeUnit.MILLISECONDS);
    }

    public int getCurrentHour() {
        if (time >= 24) {
            time = 0;
        }
        currHourInMillis = System.currentTimeMillis();
        return time;
    }

    public long getMillisBeforeNextHour() {
        return currHourInMillis + 5000 - System.currentTimeMillis();
    }


    //    private int currHour;
//    private long startMillis;
//
//    public TimeSimulation(long startMillis, int currHour) {
//        this.startMillis = startMillis;
//        this.currHour = currHour;
//    }
//
//    public int getCurrentHour() {
//        if (System.currentTimeMillis() > startMillis + 3000) {
//            startMillis = System.currentTimeMillis();
//            currHour += 1;
//        }
//        return currHour;
//    }
//
//    public long getMillisBeforeNextHour(long millisPrev) {
//        return millisPrev + 3000 - System.currentTimeMillis();
//    }

}
