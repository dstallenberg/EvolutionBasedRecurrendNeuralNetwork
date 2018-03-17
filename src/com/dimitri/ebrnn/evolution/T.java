package com.dimitri.ebrnn.evolution;

import com.dimitri.ebrnn.neural.cells.ProcessorCell;

import java.util.Random;

public class T {

    public static void main(String... args){
        Random r = new Random();
        long last;
        for (int i = 0; i < 1000; i++) {
            double input = r.nextDouble();
            ProcessorCell.tanh(input);
        }
        for (int j = 0; j < 10; j++) {
            last = System.nanoTime();
            for (int i = 0; i < 1000000; i++) {
                double input = r.nextDouble();
                ProcessorCell.tanh(input);
            }
            System.out.println("Math: " + ((System.nanoTime()-last))/1000);
            last = System.nanoTime();
            for (int i = 0; i < 1000000; i++) {
                double input = r.nextDouble();
                ProcessorCell.ownTanh(input);
            }
            System.out.println("Own: "+((System.nanoTime()-last))/1000);
        }


    }

}
