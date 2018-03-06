package com.dimitri.ebrnn.Trading.general;

public class Arrays {

    public static int rotate(double[] array, int current){
        if(current >= array.length-1){
            System.arraycopy(array, 1, array, 0, array.length - 1);
            return array.length-1;
        }else{
            return ++current;
        }
    }

    public static double sumLast(double[] array, int stepAmount, int current){
        double sum = 0;
        for (int i = current-stepAmount; i < current; i++) {
            sum += array[i];
        }
        return sum;
    }

    public static void calcEMA(int current, double[] array, int currentEMA, double[] arrayEMA, double length){
        arrayEMA[currentEMA] = array[current];
        arrayEMA[currentEMA] *= (2d/(length+1));
        double temp = arrayEMA[currentEMA-1];
        temp *= (1-(2/(length+1)));
        arrayEMA[currentEMA] += temp;
    }
}
