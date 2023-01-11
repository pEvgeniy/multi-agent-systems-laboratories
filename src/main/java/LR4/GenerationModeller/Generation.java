package LR4.GenerationModeller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class Generation {

    public double generationWES() {
         Random random = new Random();
         double windPowerGeneration = BigDecimal.valueOf(random.nextGaussian() * 6.2 + 9.1).setScale(2, RoundingMode.HALF_UP).doubleValue();
         if (windPowerGeneration < 0) {
             return 0;
         }
         return windPowerGeneration;
     }

    public double generationSES(int t) {
         double sunPowerGeneration = -85.304*Math.pow(t, 1) + 21.938*Math.pow(t, 2) - 1.424*Math.pow(t, 3) + 0.027*Math.pow(t, 4);
         if ((0 <= t && t < 5) || (t > 19)) {
             return 0;
         }
         return sunPowerGeneration;
     }

     public double generatedPower(String agentName, int t) {
         if (agentName.contains("WES")) {
             return generationWES();
         }
         if (agentName.contains("SES")) {
             return generationSES(t);
         }
         if (agentName.contains("TEC")) {
             return 11.6;
         }
         return 0;
     }


}
