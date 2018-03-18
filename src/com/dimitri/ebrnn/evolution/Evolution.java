package com.dimitri.ebrnn.evolution;

import com.dimitri.ebrnn.Trading.TickerDataParser;
import com.dimitri.ebrnn.Trading.TraderNet;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Evolution {

    private int TraderNetCount = 0;
    private int inputAmount;
    private int outputAmount;

    private ArrayList<TraderNet> traderNets;
    private ArrayList<Double> profits;

    private Random random;

    public Evolution(int inputAmount, int outputAmount){
        this.inputAmount = inputAmount;
        this.outputAmount = outputAmount;
        traderNets = new ArrayList<>();
        profits = new ArrayList<>();
        random = new Random();
        initRandomTraderNets(8, 2, 100);
    }

    public void update(){
        for (int i = 0; i < 10; i++) {
            feed();
            System.out.println("Average Profit: " + getAverageProfit());
            makeOffspring();
            System.out.println("Mutated!");
        }
        feed();
        System.out.println("Average Profit: " + getAverageProfit());
        save();
        System.out.println("Saved!");
        makeOffspring();
        System.out.println("Mutated!");

    }

    public void render(Graphics g){

    }

    public void save(){
        ArrayList<TraderNet> top10 = getBestTraderNets();
        traderNets.removeAll(traderNets);
        for (int i = 0; i < top10.size(); i++) {
            try {
                top10.get(i).getNet().getIo().Write("weights/"+ i+".txt", top10.get(i).getNet());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public double getAverageProfit(){
        double sum = 0;
        for (int i = 0; i < traderNets.size(); i++) {
            sum += traderNets.get(i).getAverageProfit();
        }
        return sum/traderNets.size();
    }

    public void makeOffspring(){
        ArrayList<TraderNet> top10 = getBestTraderNets();
        traderNets.removeAll(traderNets);
        if(top10.size() == 0){
            initRandomTraderNets(8, 2, 100);
        }else{
            for (TraderNet top: top10) {
                for (int i = 0; i < Math.floor(100d/top10.size())-1; i++) {
                    traderNets.add(new TraderNet(top.mutate()));
                }
                traderNets.add(top);
            }
        }

    }
    
    public ArrayList<TraderNet> getBestTraderNets(){
        ArrayList<TraderNet> top10 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int index = 0;
            double highest = Integer.MIN_VALUE;
            for (int j = 0; j < traderNets.size(); j++) {
                if(traderNets.get(j).getAverageProfit() > highest){
                    highest = traderNets.get(j).getAverageProfit();
                    index = j;
                }
            }
            if(traderNets.get(index).getAverageProfit() > 0){
                top10.add(traderNets.get(index));
            }
            traderNets.remove(index);
        }
        return top10;
    }

    public ArrayList<TickerDataParser> fetchData(){
        ArrayList<TickerDataParser> array = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File("textFiles/DownloadsOneMin20.txt"));
            while(scanner.hasNext()){
                StringBuilder data = new StringBuilder();
                String current = scanner.next();
                while(!current.equals("~")){
                    data.append(current);
                    current = scanner.next();
                }
                array.add(new TickerDataParser(data.toString()));
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }

    public void feed(){
        ArrayList<TickerDataParser> array = fetchData();

        for (int i = 0; i < traderNets.size(); i++) {
            long last = System.nanoTime();
            System.out.println("NextNet: " + i);
            traderNets.get(i).feed(array);
            System.out.println("Net "+ i +" fed! Time: " + (System.nanoTime()-last)/1000000 + " ms");
            System.out.println("Profit: " + traderNets.get(i).getAverageProfit());
            profits.add(i, traderNets.get(i).getAverageProfit());
//            try {
//                traderNets.get(i).getNet().getIo().Write("weights/"+ i +".txt", traderNets.get(i).getNet());
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
        }
    }

    public void initRandomTraderNets(int inputAmount, int outputAmount, int TraderNetAmount){
        for (int i = 0; i < TraderNetAmount; i++) {
            int hiddenLayers = random.nextInt(6)+1;
            int[] topology = new int[hiddenLayers+2];
            topology[0] = inputAmount;
            for (int j = 0; j < hiddenLayers; j++) {
                topology[j+1] = random.nextInt(50)+1;
            }
            topology[topology.length-1] = outputAmount;
            traderNets.add(new TraderNet(topology));
            TraderNetCount++;
        }
    }

    public void initOldTraderNets(String filePath){

    }

}
