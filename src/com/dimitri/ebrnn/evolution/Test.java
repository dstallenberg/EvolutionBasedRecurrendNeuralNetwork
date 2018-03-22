package com.dimitri.ebrnn.evolution;

public class Test {

    public static void main(String... args) {
        Evolution evolution = new Evolution(8, 2, 100);
        for (int i = 0; i < 100; i++) {
            evolution.update();
        }

    }


}
