package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

/**
 * Created by yonatan on 12/23/16.
 */
public class NextPrimeHammer implements Tool {
    @Override
    public String getType() {
        return "np-hammer";
    }

    @Override
    public long useOn(Product p) {
        long n = p.getFinalId();
        boolean isPrime = false;
        long v =p.getFinalId() + 1;
        while (!isPrime(v)) {
            v++;
        }

        return v;
//        while (!isPrime) {
//            n++;
//            isPrime = true;
//            for (int i = 2; i * i <= n; i++) {
//                if (n % i == 0) {
//                    isPrime = false;
//                    break;
//                }
//            }
//        }
//        return n;
    }

    private boolean isPrime(long value) {
        long sq = (long) Math.sqrt(value);
        for (long i = 2; i < sq; i++) {
            if (value % i == 0) {
                return false;
            }
        }

        return true;
    }

}
