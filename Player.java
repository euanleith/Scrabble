package sample;

import java.util.ArrayList;

class Player {
    private ArrayList<Tile> rack;
    private int score;
    private String name;

    Player(String name) {
        rack = new ArrayList<>(Main.RACK_SIZE);
        score = 0;
        this.name = name;
    }

    String getName() {
        return name;
    }

    void add(int score) {
        this.score += score;
    }

    int getScore() {
        return score;
    }

    ArrayList<Tile> getRack() {
        return rack;
    }
}