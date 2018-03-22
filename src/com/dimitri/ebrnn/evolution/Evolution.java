package com.dimitri.ebrnn.evolution;

import com.dimitri.ebrnn.Trading.TickerDataParser;
import com.dimitri.ebrnn.Trading.TraderNet;
import com.dimitri.ebrnn.neural.IO;
import com.dimitri.ebrnn.neural.Net;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;


public class Evolution {

    private final int inputAmount;
    private final int outputAmount;
    private final int totalNets;

    private final ArrayList<TraderNet> traderNets;
    private final ArrayList<Double> profits;
    private final ArrayList<Integer> positives;
    private final ArrayList<Integer> zeros;
    private final ArrayList<Integer> negatives;

    private final Random random;

    public Evolution(int inputAmount, int outputAmount, int totalNets){
        this.inputAmount = inputAmount;
        this.outputAmount = outputAmount;
        this.totalNets = totalNets;
        traderNets = new ArrayList<>(100);
        profits = new ArrayList<>(100);
        positives = new ArrayList<>();
        zeros = new ArrayList<>();
        negatives = new ArrayList<>();
        random = new Random();
        initRandomTraderNets(inputAmount, outputAmount, totalNets);
//        try {
//            initOldTraderNets();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        makeOffspring(traderNets);
    }

    public void update(){
        feed();
        getAverageProfit();
        save();
        makeOffspring(getBestTraderNets());
    }

    public void render(Graphics g){

    }

    public void save(){
        ArrayList<TraderNet> top10 = getBestTraderNets();
        for (int i = 0; i < top10.size(); i++) {
            try {
                Net net = top10.get(i).getNet();
                IO io = new IO("weights/"+ i + ".txt");
                io.Write(net);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Saved!");
    }

    public double getAverageProfit(){
        double sum = 0;
        for (TraderNet traderNet : traderNets) {
            sum += traderNet.getAverageProfit();
        }
        double avg = sum/traderNets.size();
        System.out.println("Average Profit: " + avg);
        return avg;
    }

    public void makeOffspring(ArrayList<TraderNet> top){
        ArrayList<TraderNet> topNets = new ArrayList<>();
        topNets.addAll(top);
        traderNets.removeAll(traderNets);
        if(topNets.size() == 0){
            initRandomTraderNets(inputAmount, outputAmount, totalNets);
        }else{
            for (TraderNet t: topNets) {
                traderNets.add(t);
                for (int i = 0; i < Math.floor(totalNets/topNets.size())-1; i++) {
                    traderNets.add(new TraderNet(t.mutate()));
                }
            }
        }
        System.out.println("Mutated!");
    }
    
    public ArrayList<TraderNet> getBestTraderNets(){
        ArrayList<TraderNet> topNets = new ArrayList<>();

        // Point system
        HashMap<TraderNet, Integer> profitRanking = new HashMap<>();
        HashMap<TraderNet, Integer> positivesRanking = new HashMap<>();
        HashMap<TraderNet, Integer> totalDivNegativesRanking = new HashMap<>();
        HashMap<TraderNet, Integer> positiveTradesRanking = new HashMap<>();
        HashMap<TraderNet, Integer> positiveTradesDivTotalRanking = new HashMap<>();

        ArrayList<TraderNet> tempList = new ArrayList<>();

        // Profit Rankings
        int initialSize = traderNets.size();
        for (int i = 0; i < initialSize; i++) {
            int index = 0;
            double highest = Integer.MIN_VALUE;
            for (int j = 0; j < traderNets.size(); j++) {
                if(highest < traderNets.get(j).getAverageProfit()){
                    index = j;
                    highest = traderNets.get(j).getAverageProfit();
                }
            }
            profitRanking.put(traderNets.get(index), i);
            tempList.add(traderNets.remove(index));
        }

        // Positives Rankings
        for (int i = 0; i < initialSize; i++) {
            int index = 0;
            int mostPositives = Integer.MIN_VALUE;
            for (int j = 0; j < tempList.size(); j++) {
                if(mostPositives < tempList.get(j).getPositives()){
                    index = j;
                    mostPositives = tempList.get(j).getPositives();
                }
            }
            positivesRanking.put(tempList.get(index), i);
            traderNets.add(tempList.remove(index));
        }

        // Total divided by Negatives Rankings
        for (int i = 0; i < initialSize; i++) {
            int index = 0;
            double bestRating = Integer.MAX_VALUE;
            for (int j = 0; j < traderNets.size(); j++) {
                TraderNet t = traderNets.get(j);
                double rating = t.getNegatives()/(t.getNegatives()+t.getPositives()+t.getZero());
                if(bestRating > rating){
                    bestRating = rating;
                    index = j;
                }
            }
            totalDivNegativesRanking.put(traderNets.get(index), i);
            tempList.add(traderNets.remove(index));
        }

        for (int i = 0; i < initialSize; i++) {
            int index = 0;
            double mostPos = Integer.MIN_VALUE;
            for (int j = 0; j < tempList.size(); j++) {
                if(mostPos < tempList.get(j).getPositivesTrades()){
                    mostPos = tempList.get(j).getPositivesTrades();
                    index = j;
                }
            }
            positiveTradesRanking.put(tempList.get(index), i);
            traderNets.add(tempList.remove(index));
        }


        // Positive trades divided by Total trades
        for (int i = 0; i < initialSize; i++) {
            int index = 0;
            double bestRating = Integer.MIN_VALUE;
            for (int j = 0; j < traderNets.size(); j++) {
                TraderNet t = traderNets.get(j);
                if(t.getTotalTrades() != 0){
                    double rating = t.getPositivesTrades()/t.getTotalTrades();
                    if(bestRating < rating){
                        bestRating = rating;
                        index = j;
                    }
                }
            }
            positiveTradesDivTotalRanking.put(traderNets.get(index), i);
            tempList.add(traderNets.remove(index));
        }

        for (int i = 0; i < tempList.size(); i++) {
            traderNets.add(tempList.remove(i));
        }

        // Sum all the rankings with optional weight
        // then compare and select the best one's
        for (int i = 0; i < 10; i++) {
            TraderNet bestNet = traderNets.get(0);
            double best = Integer.MAX_VALUE-100000;
            for (TraderNet net: profitRanking.keySet()) {
                double total = profitRanking.get(net);
                total += positivesRanking.get(net)*4;
                total += totalDivNegativesRanking.get(net);
                total += positiveTradesRanking.get(net)*2;
                total += positiveTradesDivTotalRanking.get(net)*3;


                if(total < best){
                    best = total;
                    bestNet = net;
                }
            }
            profitRanking.remove(bestNet);
            positivesRanking.remove(bestNet);
            totalDivNegativesRanking.remove(bestNet);
            positiveTradesDivTotalRanking.remove(bestNet);
            topNets.add(bestNet);
        }

        for (int i = 0; i < topNets.size(); i++) {
            System.out.println("Top performer: " + i + "\n\tPositives: " + topNets.get(i).getPositives() + "\n\tZeros: " + topNets.get(i).getZero() + "\n\tNegatives: " + topNets.get(i).getNegatives() + "\n\tProfit: " + String.format("%.5f", topNets.get(i).getAverageProfit()) + " %" + "\n\tPositive Trades: " + topNets.get(i).getPositivesTrades() + "\n\tTotal Trades: " + topNets.get(i).getTotalTrades());
        }
        return topNets;
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
            System.out.println("Net: " + i + "\n\tTime: " + (System.nanoTime()-last)/1000000 + " ms" + "\n\tPositives: " + traderNets.get(i).getPositives() + "\n\tZeros: " + traderNets.get(i).getZero() + "\n\tNegatives: " + traderNets.get(i).getNegatives() + "\n\tProfit: " + String.format("%.5f", traderNets.get(i).getAverageProfit()) + " %"  + "\n\tPositive Trades: " + traderNets.get(i).getPositivesTrades() + "\n\tTotal Trades: " + traderNets.get(i).getTotalTrades());
            profits.add(i, traderNets.get(i).getAverageProfit());
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
        }
        System.out.println("Initialized: " + TraderNetAmount + " nets!");
    }

    public void initOldTraderNets() throws FileNotFoundException {
        for (int i = 0; i < 10; i++) {
            IO io = new IO("weights/1.0/" + i + ".txt");
            io.Read();
            traderNets.add(new TraderNet(new Net(io.getWeights())));
            System.out.println("Old Net: " + i + " initialized!");
        }
    }

}
