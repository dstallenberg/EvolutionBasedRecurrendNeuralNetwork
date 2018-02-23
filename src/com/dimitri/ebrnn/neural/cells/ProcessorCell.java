package com.dimitri.ebrnn.neural.cells;

import java.util.Random;

public class ProcessorCell {

    public static double tanh(double input){
        return Math.tanh(input);
    }

    public static double derrivativeTanh(double input){
        double x = tanh(input);
        return 1.0 - x*x;
    }

    public static double sigmoid(double input) {
        return 1/(1+Math.exp(-input));
    }

    public static double derrivativeSigmoid(double input) {
        double x = Math.exp(-input);
        return (x/((1+x)*(1+x)));
    }


    public static double getRandomWeight(){
        return (new Random().nextDouble()*2)-1;
    }

}
