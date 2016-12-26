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
        Random random = new Random(p.getStartId());
        long total = 0;
        long amount = p.getStartId() % 10000;
        for (int i = 0; i < amount; i++) {
           total += random.nextInt();
        }
        return total;
    }
}
