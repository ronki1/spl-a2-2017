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
