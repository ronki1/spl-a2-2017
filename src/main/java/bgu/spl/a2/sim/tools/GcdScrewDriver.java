package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

import java.math.BigInteger;

/**
 * Created by yonatan on 12/23/16.
 */
public class GcdScrewDriver implements Tool {
    @Override
    public String getType() {
        return "gs-driver";
    }

    @Override
    public long useOn(Product p) {
        long id = p.getStartId();
        long reversed = 0;
        while (id != 0) {
            reversed = reversed * 10 + id % 10;
            id = id / 10;
        }

        // GCD alg
        long m = p.getStartId();
        long n = reversed;
        long r;
        while (n != 0) {
            r = m % n;
            m = n;
            n = r;
        }
        return n;

        // alt using java.math
        /*
        BigInteger b1 = BigInteger.valueOf(p.getStartId());
        BigInteger b2 = BigInteger.valueOf(reversed);
        BigInteger gcd = b1.gcd(b2);
        return gcd.longValue();
        */
    }
}
