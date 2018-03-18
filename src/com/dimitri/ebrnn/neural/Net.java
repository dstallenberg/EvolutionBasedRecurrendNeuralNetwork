package com.dimitri.ebrnn.neural;

import com.dimitri.ebrnn.neural.cells.HiddenCell;
import com.dimitri.ebrnn.neural.cells.lstm.LSTMCell;
import com.dimitri.ebrnn.neural.layers.HiddenLayer;
import com.dimitri.ebrnn.neural.layers.InputLayer;
import com.dimitri.ebrnn.neural.layers.LSTMLayer;
import com.dimitri.ebrnn.neural.layers.Layer;

import java.io.IOException;
import java.util.Random;

/**
 * Neural net Head Class API
 * @author Dimitri Stallenberg
 * @version 1.0
 */
public class Net {

    /**
     * Topology of the net
     */
    private int[] topology;
    /**
     * Input Output Object
     */
    private IO io;
    /**
     * FilePath used by the IO object.
     */
    private String filePath = "StandardFilePath.txt";
    /**
     * Array consisting of the layers contained in this net.
     */
    private Layer[] layer;

    /**
     * Array consisting of current input.
     */
    private double[] input;
    /**
     * Array consisting of current output.
     */
    private double[] output;
    /**
     * Array consisting of current targetOutput.
     */
    private double[] targetOutput;

    /**
     * The current error rate.
     */
    private double error = 0;
    /**
     * The recent average error rate.
     */
    private double recentAverageError = 0;
    /**
     * The smoothing factor of which the average error rate is calculated. This lies between 0 and 1.
     */
    private double recentAverageSmoothingFactor = 0.9;

    /**
     * Standard constructor for a fresh Neural net
     * @param topology The layout of the net. <br>
     *                 Each int represents the amount of neurons in that particular layer.
     */
    public Net(int[] topology){
        this.topology = topology;
        io = new IO(filePath);
        if(topology.length >= 2){
            input = new double[topology[0]];
            output = new double[topology[topology.length-1]];
            targetOutput = new double[topology[topology.length-1]];
            layer = new Layer[topology.length];
            layer[0] = new InputLayer(this, topology[0]);
            for (int i = 1; i < topology.length; i++) {
                layer[i] = new LSTMLayer(this, i, topology[i]);
            }
        }else{
            throw new IllegalArgumentException("Nets need at least 2 layers");
        }
    }

    /**
     * Constructor which takes in a String as a filePath. <br>
     * This constructor uses the file as a database for the weights.
     * @param filePath The String of the path to the file to be used.
     * @throws IOException For when the file doesn't exist or has a wrong format.
     */
    public Net(String filePath) throws IOException {
        io = new IO(filePath);
        io.Read();
        int[] topology = io.getTopology();

        if(topology.length >= 2){
            input = new double[topology[0]];
            output = new double[topology[topology.length-1]];
            targetOutput = new double[topology[topology.length-1]];
            layer = new Layer[topology.length];
            layer[0] = new InputLayer(this, topology[0]);
            for (int i = 1; i < topology.length; i++) {
                layer[i] = new LSTMLayer(this, i, io.getLayerWeights(i));
            }
        }else{
            throw new IllegalArgumentException("Nets need at least 2 layers");
        }
    }

    /**
     *
     * @param weight [layer][cell][gate][weight]
     */
    public Net(double[][][][] weight){
        int[] topology = new int[weight.length];
        for (int i = 0; i < topology.length; i++) {
            topology[i] = weight[i].length;
        }
        if(topology.length >= 2){
            input = new double[topology[0]];
            output = new double[topology[topology.length-1]];
            targetOutput = new double[topology[topology.length-1]];
            layer = new Layer[topology.length];
            layer[0] = new InputLayer(this, topology[0]);
            for (int i = 1; i < topology.length; i++) {
                layer[i] = new LSTMLayer(this, i, weight[i]);
            }
        }else{
            throw new IllegalArgumentException("Nets need at least 2 layers");
        }
    }

    public Net mutate(int oneInEvery_Neuron, int oneInEvery_Layer, int oneInEvery_Topo) {
        double[][][][] weights = getWeights();
        Random random = new Random();
        for (int i = 0; i < weights.length; i++) {
            for (int j = 0; j < weights[i].length; j++) {
                for (int k = 0; k < weights[i][j].length; k++) {

                    for (int l = 0; l < weights[i][j][k].length; l++) {
                        int mutate = random.nextInt(oneInEvery_Neuron);
                        if(mutate == 0){
                            double delta = (random.nextDouble()*2-1)*0.01;
                            weights[i][j][k][l] = weights[i][j][k][l]+delta;
                        }
                    }
                }
            }
        }

        int[] topology = getTopology();
        /**
        topology = mutateLayers(topology, oneInEvery_Topo);   // chance 0.002 for change
        topology = mutateNeurons(topology, oneInEvery_Layer); // chance 0.001 per layer

        //Set new neuron weights!!>!!>
        double[][][][] newWeights = new double[topology.length][][][];
        for (int layer = 0; layer < topology.length; layer++) {
            newWeights[layer] = new double[topology[layer]][][];
            for (int neuron = 0; neuron < topology[layer]; neuron++) {
                newWeights[layer][neuron] = new double[8][];
                for (int gate = 0; gate < 8; gate++) {
                    if(gate%2 == 0){
                        if(layer > 0){
                            newWeights[layer][neuron][gate] = new double[topology[layer-1]];
                            for (int weight = 0; weight < topology[layer-1]; weight++) {
                                if(newWeights.length == weights.length){
                                    if(newWeights[layer].length <= weights[layer].length){
                                        newWeights[layer][neuron][gate][weight] = weights[layer][neuron][gate][weight];
                                    }else{
                                        if(neuron >= weights[layer].length){
                                            newWeights[layer][neuron][gate][weight] = -100;
                                        }else{
                                            newWeights[layer][neuron][gate][weight] = weights[layer][neuron][gate][weight];
                                        }
                                    }
                                }else if(newWeights.length < weights.length){
                                    if(newWeights[layer].length == weights[layer].length){
                                        newWeights[layer][neuron][gate][weight] = weights[layer][neuron][gate][weight];
                                    }else{
                                        if(newWeights[layer].length == weights[layer+1].length){
                                            newWeights[layer][neuron][gate][weight] = weights[layer+1][neuron][gate][weight];
                                        }else{
                                            if(newWeights[layer].length < weights[layer].length){
                                                newWeights[layer][neuron][gate][weight] = weights[layer][neuron][gate][weight];
                                            }else{
                                                if(neuron >= weights[layer].length){
                                                    newWeights[layer][neuron][gate][weight] = -100;
                                                }else{
                                                    newWeights[layer][neuron][gate][weight] = weights[layer][neuron][gate][weight];
                                                }
                                            }
                                        }
                                    }
                                }else{
                                    if(newWeights[layer].length == weights[layer].length){
                                        newWeights[layer][neuron][gate][weight] = weights[layer][neuron][gate][weight];
                                    }else{
                                        newWeights[layer][neuron][gate][weight] = -100;
                                    }
                                }
                            }
                        }else{
                            newWeights[layer][neuron][gate] = new double[0];
                        }
                    }else{
                        if(layer > 0){
                            newWeights[layer][neuron][gate] = new double[topology[layer]];
                            for (int recurrentWeights = 0; recurrentWeights < topology[layer]; recurrentWeights++) {

                            }
                        }else{
                            newWeights[layer][neuron][gate] = new double[0];
                        }
                    }
                }
            }
        }
*/
        return new Net(weights);
    }

    public int[] mutateNeurons(int[] topology, int oneInEvery_Layer){
        //Chance to get more neurons per layer = 0.001
        Random random = new Random();
        int[] newTopology = new int[topology.length];
        for (int i = 1; i < topology.length; i++) {
            int choice = random.nextInt(oneInEvery_Layer);
            if(choice == 0){
                int delta = random.nextInt(10)+1;
                if(delta > 7){
                    delta = 2;
                }else{
                    delta = 1;
                }
                int bool = random.nextInt(2);
                if(bool == 1){
                    newTopology[i] = topology[i]+delta;
                }else{
                    newTopology[i] = topology[i]-delta;
                    if(newTopology[i] <= 0){
                        newTopology[i] = 1;
                    }
                }
            }else{
                newTopology[i] = topology[i];
            }
        }
        return newTopology;
    }

    /**
     * This method mutates the amount of Layers the network has.
     * @param topology the current Topology.
     * @param oneInEvery_Topo the chance that a layer gets removed or added.
     * @return an int array containing the new Topology.
     */
    public int[] mutateLayers(int[] topology, int oneInEvery_Topo){
        //Chance to get more layers = 0.001
        //Chance to get less layers = 0.001
        Random random = new Random();
        int choice = random.nextInt(oneInEvery_Topo);
        int[] newTopology;
        if(choice == 0){
            newTopology = new int[topology.length+1];
            int place = random.nextInt(topology.length);
            int newLayerLength = random.nextInt(topology[place]+2)+1;
            for (int i = 0; i < newTopology.length; i++) {
                if(place < i){
                    newTopology[i] = topology[i-1];
                }else if(place == i){
                    newTopology[i] = newLayerLength;
                }else{
                    newTopology[i] = topology[i];
                }
            }
        }else if(choice == 1){
            if(topology.length-1 > 1){
                newTopology = new int[topology.length-1];
                int place = random.nextInt(topology.length);
                for (int i = 0; i < newTopology.length; i++) {
                    if(place <= i){
                        newTopology[i] = topology[i+1];
                    }else{
                        newTopology[i] = topology[i];
                    }
                }
            }else{
                newTopology = topology;
            }
        }else{
            newTopology = topology;
        }

        return newTopology;
    }

    /**
     * This method gathers all the weights for the current network.
     * @return Multi-Dimension array containing all Weights.
     */
    public double[][][][] getWeights(){
        double[][][][] weights = new double[getLayer().length][][][];
        weights[0] = new double[getLayer(0).getCell().length][][];
        for (int inputNeuron = 0; inputNeuron < weights[0].length; inputNeuron++) {
            weights[0][inputNeuron] = new double[0][0];
        }
        for (int layer = 1; layer < getLayer().length; layer++) {
            weights[layer] = new double[getLayer(layer).getCell().length][][];
            for (int neuron = 0; neuron < getLayer(layer).getCell().length; neuron++) {
                weights[layer][neuron] = new double[8][];
                for (int gate = 0; gate < 8; gate++) {
                    if(gate%2 == 0){
                        weights[layer][neuron][gate] = new double[((LSTMCell)getLayer(layer).getCell(neuron)).getConnection().length];
                        for (int weight = 0; weight < ((LSTMCell)getLayer(layer).getCell(neuron)).getConnection().length; weight++) {
                            if(gate == 0){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getConnection(weight).getWeight();
                            }else if(gate == 2){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getInputGate().getConnection(weight).getWeight();
                            }else if(gate == 4){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getForgetGate().getConnection(weight).getWeight();
                            }else if(gate == 6){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getOutputGate().getConnection(weight).getWeight();
                            }
                        }
                    }else{
                        weights[layer][neuron][gate] = new double[((LSTMCell)getLayer(layer).getCell(neuron)).getRecurrentConnection().length];
                        for (int weight = 0; weight < ((LSTMCell)getLayer(layer).getCell(neuron)).getRecurrentConnection().length; weight++) {
                            if(gate == 1){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getRecurrentConnection(weight).getWeight();
                            }else if(gate == 3){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getInputGate().getRecurrentConnection(weight).getWeight();
                            }else if(gate == 5){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getForgetGate().getRecurrentConnection(weight).getWeight();
                            }else if(gate == 7){
                                weights[layer][neuron][gate][weight] = ((LSTMCell)getLayer(layer).getCell(neuron)).getOutputGate().getRecurrentConnection(weight).getWeight();
                            }
                        }
                    }
                }
            }
        }
        return weights;
    }

    /**
     * This method feeds the input array into the input cells. <br>
     * It then feeds this forward through the network.
     * @param input The new input array.
     */
    public void feedForward(double[] input){
        this.input = input.clone();
        if(input.length == layer[0].getCell().length){
            for (int i = 0; i < input.length; i++) {
                layer[0].getCell(i).setOutput(input[i]);
            }
            for (int i = 1; i < layer.length; i++) {
                for (int j = 0; j < layer[i].getCell().length; j++) {
                    ((LSTMCell)layer[i].getCell(j)).feedForward();

                }
            }
            for (int i = 0; i < layer[layer.length-1].getCell().length; i++) {
                output[i] = layer[layer.length-1].getCell(i).getOutput();
            }
        }else{
            throw new IllegalArgumentException("The length of the input array does't match the amount of input cells.");
        }
    }

    /**
     * Gets the input array.
     * @return Input array.
     */
    public double[] getInput() {
        return input;
    }

    /**
     * Gets the output array.
     * @return Output array.
     */
    public double[] getOutput(){
        return output;
    }

    /**
     * Gets the target output array.
     * @return Target output array.
     */
    public double[] getTargetOutput() {
        return targetOutput;
    }

    /**
     * Gets the last error rate.
     * @return Last error rate.
     */
    public double getError() {
        return error;
    }

    /**
     * Gets the RecentAverageError.
     * @return RecentAverageError
     */
    public double getRecentAverageError() {
        return recentAverageError;
    }

    /**
     * Gets the recentAverageError Smoothing factor.
     * @return RecentAverageError Smoothing factor.
     */
    public double getRecentAverageSmoothingFactor() {
        return recentAverageSmoothingFactor;
    }

    /**
     * Sets the recentAverageError Smoothing factor.
     * @param recentAverageSmoothingFactor The new Factor between 0 and 1.
     */
    public void setRecentAverageSmoothingFactor(double recentAverageSmoothingFactor) {
        this.recentAverageSmoothingFactor = recentAverageSmoothingFactor;
    }

    /**
     * Gets the IO object of this net.
     * @return IO object.
     */
    public IO getIo() {
        return io;
    }

    /**
     * Gets an array of layers.
     * @return All layers.
     */
    public Layer[] getLayer() {
        return layer;
    }

    /**
     * Gets a certain layer.
     * @param index Index of the wanted layer.
     * @return Wanted layer.
     */
    public Layer getLayer(int index) {
        return layer[index];
    }

    /**
     * Method to set a filePath to save the weights on.
     * @param filePath String of the path of the file.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return Eta: The overall learning rate.
     */
    public double getEta() {
        return ((HiddenLayer)layer[1]).getCell(0).getEta();
    }

    /**
     * @return Alpha: The fraction of deltaWeight to be added to the new Weight when training.
     */
    public double getAlpha() {
        return ((HiddenLayer)layer[1]).getCell(0).getAlpha();
    }

    /**
     * Eta is the overall learning rate which is applied to the gradient.
     * @param eta Fraction between 0 and 1.
     */
    public void setEta(double eta) {
        for (int i = 1; i < layer.length; i++) {
            for (int j = 0; j < ((HiddenLayer)layer[i]).getCell().length; j++) {
                HiddenCell cell = ((HiddenLayer)layer[i]).getCell(j);
                cell.setEta(eta);
            }
        }
    }

    /**
     * Alpha is the fraction of the deltaWeight that is added to the new Weight when training.
     * @param alpha Fraction between 0 and 1.
     */
    public void setAlpha(double alpha) {
        for (int i = 1; i < layer.length; i++) {
            for (int j = 0; j < ((HiddenLayer)layer[i]).getCell().length; j++) {
                HiddenCell cell = ((HiddenLayer)layer[i]).getCell(j);
                cell.setAlpha(alpha);
            }
        }
    }

    /**
     * Getter
     * @return the toplogy of the net.
     */
    public int[] getTopology() {
        return topology;
    }
}
