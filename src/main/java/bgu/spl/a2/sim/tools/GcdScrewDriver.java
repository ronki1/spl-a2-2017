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
        long id = p.getFinalId();
        BigInteger b1 = BigInteger.valueOf(id);
        BigInteger b2 = BigInteger.valueOf(reverse(id));
        long value= (b1.gcd(b2)).longValue();
        return value;
//        long reversed = 0;
//        while (id != 0) {
//            reversed = reversed * 10 + id % 10;
//            id = id / 10;
//        }
//
//        // GCD alg
//        long m = p.getFinalId();
//        long n = reversed;
//        long r;
//        while (n != 0) {
//            r = m % n;
//            m = n;
//            n = r;
//        }
//        return n;

        // alt using java.math
        /*
        BigInteger b1 = BigInteger.valueOf(p.getStartId());
        BigInteger b2 = BigInteger.valueOf(reversed);
        BigInteger gcd = b1.gcd(b2);
        return gcd.longValue();
        */
    }
    public long reverse(long n){
        long reverse=0;
        while( n != 0 ){
            reverse = reverse * 10;
            reverse = reverse + n%10;
            n = n/10;
        }
        return reverse;
    }
}
