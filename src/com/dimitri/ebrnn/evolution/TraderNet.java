package com.dimitri.ebrnn.evolution;

import com.dimitri.ebrnn.neural.Net;
import com.dimitri.ebrnn.neural.cells.Connection;
import com.dimitri.ebrnn.neural.cells.lstm.Gate;
import com.dimitri.ebrnn.neural.cells.lstm.LSTMCell;

import java.util.Random;

public class TraderNet {

    private Net net;

    private double currentProfit;

    private Random random;

    public TraderNet(int[] topology){
        net = new Net(topology);
        currentProfit = 0;
        random = new Random();
    }

    public TraderNet mutate(){
        Net child = net;
        //all weights are slightly changed working with standard deviation to decrease chances of big changes

        for (int layer = 0; layer < child.getLayer().length; layer++) {
            for (int cell = 0; cell < child.getLayer(layer).getCell().length; cell++) {
                LSTMCell currentCell = ((LSTMCell)child.getLayer(layer).getCell(cell));
                Gate input = ((LSTMCell)child.getLayer(layer).getCell(cell)).getInputGate();
                Gate forget = ((LSTMCell)child.getLayer(layer).getCell(cell)).getForgetGate();
                Gate output = ((LSTMCell)child.getLayer(layer).getCell(cell)).getOutputGate();

                for (int connection = 0; connection < currentCell.getConnection().length; connection++) {
                    Connection weight = currentCell.getConnection(connection);
                    double delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);

                    weight = input.getConnection(connection);
                    delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);
                    weight = forget.getConnection(connection);
                    delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);
                    weight = output.getConnection(connection);
                    delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);

                }

                for (int recurrent = 0; recurrent < currentCell.getRecurrentConnection().length; recurrent++) {
                    Connection weight = currentCell.getRecurrentConnection(recurrent);
                    double delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);

                    weight = input.getRecurrentConnection(recurrent);
                    delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);
                    weight = forget.getRecurrentConnection(recurrent);
                    delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);
                    weight = output.getRecurrentConnection(recurrent);
                    delta = (random.nextDouble()*2-1)*0.01;
                    weight.setWeight(weight.getWeight()+delta);
                }
            }
        }

        //Chance to get more layers = 0.001
        int choice = random.nextInt(1000);

        if(choice == 0){
            int newLayerLenght = random.nextInt(5)+1;
            int[] current = child.getTopology();

        }

        //Chance to get more cells = 0.005 (chance per layer)


    }

}
