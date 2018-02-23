package com.dimitri.ebrnn.evolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Evolution {

    private int TraderNetCount = 0;

    private ArrayList<TraderNet> traderNets;

    private Random random;

    public Evolution(int inputAmount, int outputAmount){
        traderNets = new ArrayList<>();
        random = new Random();
        initRandomTraderNets(inputAmount, outputAmount, 100);

    }

    public void makeSelection(){
        
    }
    
    public void getBestTraderNets(){
        
    }

    public void initRandomTraderNets(int inputAmount, int outputAmount, int TraderNetAmount){
        for (int i = 0; i < TraderNetAmount; i++) {
            int hiddenLayers = random.nextInt(3);
            if(hiddenLayers == 2){
                int cellsFirstLayer = random.nextInt(5)+1;
                int cellsSecondLayer = random.nextInt(5)+1;
                traderNets.add(new TraderNet(new int[]{inputAmount, cellsFirstLayer, cellsSecondLayer, outputAmount}));
            }else if(hiddenLayers == 1){
                int cellsFirstLayer = random.nextInt(5)+1;
                traderNets.add(new TraderNet(new int[]{inputAmount, cellsFirstLayer, outputAmount}));
            } else {
                traderNets.add(new TraderNet(new int[]{inputAmount, outputAmount}));
            }
            TraderNetCount++;
        }
    }

    public void initOldTraderNets(String filePath){

    }

}
