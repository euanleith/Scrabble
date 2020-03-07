package sample;

import java.util.ArrayList;
import java.util.Collections;

class Player {
    private ArrayList<UserTile> rack;
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

    ArrayList<UserTile> getRack() {
        return rack;
    }

    ArrayList<Tile> getTileRack() {
        ArrayList<Tile> out = new ArrayList<>(rack.size());
        for (Tile tile : rack) {
            out.add(tile);
        }
        return out;
    }
}