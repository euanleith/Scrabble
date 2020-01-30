package sample;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

class Player {
    private Button[] rack;
    private int score;
    private String name;

    Player(String name) {
        rack = new Button[Main.RACK_SIZE];//todo move RACK_SIZE to main?
        //todo set random? (need to coordinate with bag)
        score = 0;
        this.name = name;
    }

    String getName() {
        return name;
    }

    int getScore() {
        return score;
    }

    Button getButton(int index) {
        return rack[index];
    }
//
//    Tile getTile(Button button) {
//        for (Tile tile : rack) {
//            if (tile.getButton() == button) {
//                return tile;
//            }
//        }
//        return null;
//    }

    int getRackLength() {
        int cnt = 0;
        for (Button button : rack) {
            if (button != null) {
                cnt++;
            }
        }
        return cnt;
    }

    //todo yucky
    /**
     * Returns all of the non-null elements of the rack
     * @return all of the non-null elements of the rack
     */
    Button[] getRack() {
        int length = 0;
        for (Button button : rack) {
            if (button != null) {
                length++;
            }
        }
        Button[] rackWithoutNulls = new Button[length];
        int index = 0;
        for (Button button : rack) {
            if (button != null) {
                rackWithoutNulls[index++] = button;
            }
        }
        return rackWithoutNulls;
    }

    void remove(Button button) {
        rack[GridPane.getColumnIndex(button)] = null;
    }

    void add(Button button) {
        for (int i = 0; i < rack.length; i++) {
            if (rack[i] == null) {
                rack[i] = button;
                return;
            }
        }
    }

    void add(int index, Button button) {
        rack[index] = button;
    }

    void add(int score) {
        this.score += score;
    }
}