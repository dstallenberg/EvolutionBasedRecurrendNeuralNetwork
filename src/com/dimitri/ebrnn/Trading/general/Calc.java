package com.dimitri.ebrnn.Trading.general;

public class Calc {

    private static final int BIG_ENOUGH_INT = 16*1024;
    private static final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    private static final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5;

    //Faster than Math.floor()
    public static double floor(double val){
        return (int) (val + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }
    //Faster than Math.round()
    public static double round(double val){
        return (int) (val + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }
    //Faster than Math.ceil()
    public static double ceil(double val){
        return BIG_ENOUGH_INT - (int)(BIG_ENOUGH_FLOOR-val);
    }

    // Math.pow is faster
//    public static double pow(double val, int exp){
//        double res = 1;
//        for (int i = 0; i < exp; i++) {
//            res *= val;
//        }
//        return res;
//    }

    // Math.abs is faster
//    public static double abs(double val){
//        return (val <= 0.0D) ? 0.0D - val : val;
//    }

    // Math.sqrt is more accurate but i did not test this well enough yet
//    public static double sqrt(double val){
//        return 0;
//    }

    public static int factorial(int val){
        int result = 1;
        for (int i = 1; i <= val; i++) {
            result *= i;
        }
        return result;
    }

}
