package com.dimitri.ebrnn.neural.layers;

import com.dimitri.ebrnn.neural.Net;
import com.dimitri.ebrnn.neural.cells.InputCell;

public class InputLayer extends Layer{

    private InputCell[] cell;

    public InputLayer(Net net, int cellAmount) {
        super(net, 0, cellAmount);
        cell = new InputCell[cellAmount];
        for (int i = 0; i < cell.length; i++) {
            cell[i] = new InputCell(this, i);
        }
    }

    @Override
    public InputCell[] getCell() {
        return cell;
    }

    @Override
    public InputCell getCell(int index) {
        return cell[index];
    }
}
