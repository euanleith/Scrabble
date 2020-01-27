package sample;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

class Player {
    private Tile[] rack;
    private int points;
    private String name;

    Player(String name) {
        rack = new Tile[Main.RACK_SIZE];//todo move RACK_SIZE to main?
        //todo set random? (need to coordinate with bag)
        points = 0;
        this.name = name;
    }

    String getName() {
        return name;
    }

    Tile getTile(int index) {
        return rack[index];
    }

    Tile getTile(Button button) {
        for (Tile tile : rack) {
            if (tile.getButton() == button) {
                return tile;
            }
        }
        return null;
    }

    int getRackLength() {
        int cnt = 0;
        for (Tile tile : rack) {
            if (tile != null) {
                cnt++;
            }
        }
        return cnt;
    }

    void remove(Tile tile) {
        for (int i = 0; i < rack.length; i++) {
            if (rack[i] == tile) {
                rack[i] = null;
                return;
            }
        }
    }

    void remove(Button button) {
        rack[GridPane.getColumnIndex(button)] = null;
    }

    void add(Tile tile) {
        for (int i = 0; i < rack.length; i++) {
            if (rack[i] == null) {
                rack[i] = tile;
                return;
            }
        }
    }

    void add(Button button) {
        for (int i = 0; i < rack.length; i++) {
            if (rack[i] == null) {
                rack[i] = new Tile(button);
                return;
            }
        }
    }

    Button getEmptyButton() {
        for (Tile tile : rack) {
            if (tile.getButton().getText().equals("")) {
                return tile.getButton();
            }
        }
        return null;
    }
}