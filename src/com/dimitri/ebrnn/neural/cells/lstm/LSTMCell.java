package com.dimitri.ebrnn.neural.cells.lstm;

import com.dimitri.ebrnn.neural.cells.Cell;
import com.dimitri.ebrnn.neural.cells.Connection;
import com.dimitri.ebrnn.neural.cells.ProcessorCell;
import com.dimitri.ebrnn.neural.layers.Layer;

public class LSTMCell extends Cell {

    private Connection[] connection;
    private Connection[] recurrentConnection;

    private Gate inputGate;
    private Gate forgetGate;
    private Gate outputGate;

    private double forgetGateMemory;


    public LSTMCell(Layer layer, int cellIndex, int inputAmount) {
        super(layer, cellIndex);

        this.connection = new Connection[inputAmount];
        this.recurrentConnection = new Connection[layer.getCell().length];
        for (int i = 0; i < connection.length; i++) {
            connection[i] = new Connection(ProcessorCell.getRandomWeight());
        }
        for (int i = 0; i < recurrentConnection.length; i++) {
            recurrentConnection[i] = new Connection(ProcessorCell.getRandomWeight());
        }

        this.inputGate = new Gate(inputAmount, layer.getCell().length);
        this.forgetGate = new Gate(inputAmount, layer.getCell().length);
        this.outputGate = new Gate(inputAmount, layer.getCell().length);
        this.forgetGateMemory = 0;
    }

    public LSTMCell(Layer layer, int cellIndex, int inputAmount, double[][] weights){
        super(layer, cellIndex);
        this.connection = new Connection[inputAmount];
        this.recurrentConnection = new Connection[layer.getCell().length];
        for (int i = 0; i < connection.length; i++) {
            if(weights[0][i] == -100){
                connection[i] = new Connection(ProcessorCell.getRandomWeight());
            }else{
                connection[i] = new Connection(weights[0][i]);
            }
        }
        for (int i = 0; i < recurrentConnection.length; i++) {
            if(weights[1][i] == -100){
                recurrentConnection[i] = new Connection(ProcessorCell.getRandomWeight());
            }else{
                recurrentConnection[i] = new Connection(weights[1][i]);
            }
        }
        this.inputGate = new Gate(inputAmount, layer.getCell().length, weights[2], weights[3]);
        this.forgetGate = new Gate(inputAmount, layer.getCell().length, weights[4], weights[5]);
        this.outputGate = new Gate(inputAmount, layer.getCell().length, weights[6], weights[7]);
        this.forgetGateMemory = 0;
    }

    public void feedForward(){
        double solution = 0;
        double sum = 0;
        //new Data
        Layer previousLayer = getLayer().getNet().getLayer(getLayer().getLayerIndex()-1);

        for (int i = 0; i < previousLayer.getCell().length; i++) {
            sum += previousLayer.getCell(i).getOutput() * connection[i].getWeight();
        }

        //recurrent Data
        for (int i = 0; i < getLayer().getCell().length; i++) {
            sum += getLayer().getCell(i).getOutput() * recurrentConnection[i].getWeight();
        }

        sum += 1*connection[connection.length-1].getWeight();

        //Normalize
        solution = ProcessorCell.tanh(sum);

        //inputGate
        double inputGateKeeper = inputGate.feedForward(previousLayer, getLayer());

        //multiply
        solution = solution * inputGateKeeper;

        //forgetGate
        double forgetGateKeeper = forgetGate.feedForward(previousLayer, getLayer());

        //multiply with previous timestep
        forgetGateKeeper = forgetGateMemory * forgetGateKeeper;

        //sum
        solution += forgetGateKeeper;

        //set new memory
        forgetGateMemory = solution;

        //normalize
        solution = ProcessorCell.tanh(solution);

        //outputGate
        double outputGateKeeper = outputGate.feedForward(previousLayer, getLayer());

        //multiply
        solution = solution * outputGateKeeper;

        //setOutput
        setOutput(solution);
    }


    public Connection[] getConnection() {
        return connection;
    }

    public Connection[] getRecurrentConnection() {
        return recurrentConnection;
    }

    public Connection getConnection(int index) {
        return connection[index];
    }

    public Connection getRecurrentConnection(int index) {
        return recurrentConnection[index];
    }

    public Gate getInputGate() {
        return inputGate;
    }

    public Gate getForgetGate() {
        return forgetGate;
    }

    public Gate getOutputGate() {
        return outputGate;
    }
}
