package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.util.Random;

/**
 * Created by yonatan on 12/23/16.
 */
public class RandomSumPliers implements Tool {
    @Override
    public String getType() {
        return "rs-pliers";
    }

    @Override
    public long useOn(Product p) {
        Random r = new Random(p.getFinalId());
        long  sum = 0;
        for (long i = 0; i < p.getFinalId() % 10000; i++) {
            sum += r.nextInt();
        }

        return sum;
//        Random random = new Random(p.getFinalId());
//        long total = 0;
//        long amount = p.getFinalId() % 10000;
//        for (int i = 0; i < amount; i++) {
//           total += random.nextInt();
//        }
//        return total;
    }
}
