package com.dimitri.ebrnn.evolution;

import com.dimitri.ebrnn.Trading.TickerDataParser;
import com.dimitri.ebrnn.Trading.TraderNet;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Evolution {

    private int TraderNetCount = 0;

    private ArrayList<TraderNet> traderNets;

    private Random random;

    public Evolution(int inputAmount, int outputAmount){
        traderNets = new ArrayList<>();
        random = new Random();
        initRandomTraderNets(8, 2, 100);
    }

    public void update(){
        feed();
        System.out.println(getAverageProfit());
        traderNets = makeOffspring();
    }

    public void render(Graphics g){

    }

    public double getAverageProfit(){
        double sum = 0;
        for (int i = 0; i < traderNets.size(); i++) {
            sum += traderNets.get(i).getCurrentProfit();
        }
        return sum/traderNets.size();
    }

    public ArrayList<TraderNet> makeOffspring(){
        ArrayList<TraderNet> top10 = getBestTraderNets();
        ArrayList<TraderNet> offSpring = new ArrayList<>();
        for (TraderNet top: top10) {
            for (int i = 0; i < 10; i++) {
                offSpring.add(new TraderNet(top.mutate()));
            }
        }
        return offSpring;
    }
    
    public ArrayList<TraderNet> getBestTraderNets(){
        ArrayList<TraderNet> sorted = sort();
        ArrayList<TraderNet> top10 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            top10.add(sorted.get(i));
        }
        return top10;
    }

    public ArrayList<TraderNet> sort(){
        ArrayList<TraderNet> sorted = new ArrayList<>();
        boolean first = true;
        for (TraderNet net: traderNets) {
            if(first){
                sorted.add(net);
                first = false;
            }
            for (int sortedNet = 0; sortedNet < sorted.size(); sortedNet++) {
                if(net.getCurrentProfit() >= sorted.get(sortedNet).getCurrentProfit()){
                    sorted.add(sortedNet, net);
                }else if(sortedNet+1 == sorted.size()){
                    sorted.add(sortedNet+1, net);
                }
            }
        }
        return sorted;
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
        for (TraderNet net: traderNets) {
            net.feed(array);
        }
    }

    public void initRandomTraderNets(int inputAmount, int outputAmount, int TraderNetAmount){
        for (int i = 0; i < TraderNetAmount; i++) {
            int hiddenLayers = random.nextInt(6);
            int[] topology = new int[hiddenLayers+2];
            topology[0] = inputAmount;
            for (int j = 0; j < hiddenLayers; j++) {
                topology[j+1] = random.nextInt(200)+1;
            }
            topology[topology.length-1] = outputAmount;
            traderNets.add(new TraderNet(topology));
            TraderNetCount++;
        }
    }

    public void initOldTraderNets(String filePath){

    }

}
