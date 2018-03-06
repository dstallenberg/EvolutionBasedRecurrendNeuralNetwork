package com.dimitri.ebrnn.neural;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class IO {

    private String filePath;
    private double[][][][] weights;

    public IO(String filePath){
        this.filePath = filePath;
    }

    public void Read() throws FileNotFoundException {
        File file = new File(filePath);
        weights = count(file);
        Scanner scanner = new Scanner(file);

        int layer = 0;
        int neuron = 0;
        int gate = 0;
        int weight = 0;

        if(scanner.hasNext()){
            String current = scanner.next();
            while(scanner.hasNext()){
                current = scanner.next();
                while(!current.equals("Layer:") && scanner.hasNext()){
                    current = scanner.next();
                    while(!current.equals("Neuron:") && !current.equals("Layer:")){
                        current = scanner.next();
                        while(!current.equals("Gate:") && !current.equals("Neuron:") && !current.equals("Layer:")){
                            weights[layer][neuron][gate][weight] = Double.parseDouble(current);
                            weight++;
                            if(scanner.hasNext()){
                                current = scanner.next();
                            }else{
                                break;
                            }
                        }
                        weight = 0;
                        gate++;
                    }
                    gate = 0;
                    neuron++;
                }
                neuron = 0;
                layer++;
            }
        }

        scanner.close();
    }

    public void Write(Net net) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(filePath);
        double[][][][] weights = getWeights();

        for (int i = 0; i < weights.length; i++) {
            printWriter.print("Layer:\n");
            for (int j = 0; j < weights[i].length; j++) {
                printWriter.print("\tNeuron:\n");
                for (int k = 0; k < weights[i][j].length; k++) {
                    printWriter.print("\tGate:\n");
                    for (int l = 0; l < weights[i][j][k].length; l++) {
                        printWriter.print("\t\t" + weights[i][j][k][l] + "\n");
                    }
                }
            }
        }



//        for (int layer = 0; layer < net.getLayer().length; layer++) {
//            printWriter.print("Layer:\n");
//            for (int neuron = 0; neuron < net.getLayer(layer).getCell().length; neuron++) {
//                printWriter.print("\tNeuron:\n");
//                for (int gate = 0; gate < 4; gate++) {
//                    printWriter.print("\tGate:\n");
//                    for (int weight = 0; weight < ((LSTMCell)net.getLayer(layer).getCell(neuron)).getConnection().length; weight++) {
//                        if(gate == 0){
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getConnection(weight).getWeight() + "\n");
//                        }else if(gate == 1){
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getInputGate().getConnection(weight).getWeight() + "\n");
//                        }else if(gate == 2){
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getForgetGate().getConnection(weight).getWeight() + "\n");
//                        }else{
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getOutputGate().getConnection(weight).getWeight() + "\n");
//                        }
//                    }
//                    for (int recurrentWeight = 0; recurrentWeight < ((LSTMCell)net.getLayer(layer).getCell(neuron)).getRecurrentConnection().length; recurrentWeight++) {
//                        if(gate == 0){
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getRecurrentConnection(recurrentWeight).getWeight() + "\n");
//                        }else if(gate == 1){
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getInputGate().getRecurrentConnection(recurrentWeight).getWeight() + "\n");
//                        }else if(gate == 2){
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getForgetGate().getRecurrentConnection(recurrentWeight).getWeight() + "\n");
//                        }else{
//                            printWriter.print("\t\t" + ((LSTMCell)net.getLayer(layer).getCell(neuron)).getOutputGate().getRecurrentConnection(recurrentWeight).getWeight() + "\n");
//                        }
//                    }
//                }
//            }
//        }

        printWriter.close();
    }

    public double[][][][] count(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String current;
        int layers = 0;
        while (scanner.hasNext()){
            current = scanner.next();
            if(current.equals("Layer:")){
                layers++;
            }
        }
        double[][][][] weights = new double[layers][][][];

        scanner = new Scanner(file);
        current = scanner.next();
        for (int i = 0; i < weights.length; i++) {

            int neurons = 0;
            current = scanner.next();
            while (!current.equals("Layer:")){
                if(current.equals("Neuron:")){
                    neurons++;
                }
                if(scanner.hasNext()){
                    current = scanner.next();
                }else{
                    break;
                }
            }

            weights[i] = new double[neurons][][];
        }

        scanner = new Scanner(file);
        current = scanner.next();
        for (int i = 0; i < weights.length;i++) {
            current = scanner.next();
            for (int j = 0; j < weights[i].length; j++) {
                current = scanner.next();
                weights[i][j] = new double[8][]; // (3 gates + 1 input)*2  (recurrent and normal)
                for (int k = 0; k < weights[i][j].length; k++) {
                    current = scanner.next();
                    int weight = 0;
                    while(!current.equals("Neuron:") && !current.equals("Layer:")) {
                        weight++;
                        if (scanner.hasNext()) {
                            current = scanner.next();
                        } else {
                            break;
                        }
                    }
                    weights[i][j][k] = new double[weight];
                }
            }
        }
        return weights;
    }

    public double[][][] getLayerWeights(int layerIndex) {
        return weights[layerIndex];
    }

    public double[][][][] getWeights() {
        return weights;
    }

    public int[] getTopology() throws IOException {
        if(!weights.equals(null)){
            int[] topology =  new int[weights.length];

            for (int i = 0; i < weights.length; i++) {
                topology[i] = weights[i].length;
            }

            return topology;
        }else{
            throw new IOException("No topology has been derived yet");
        }
    }

}
