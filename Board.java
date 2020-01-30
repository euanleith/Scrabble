package sample;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Random;

public class Board {
    private Button[][] board;
    private Player[] players;
    private ArrayList<Button> bag;

    private int currentTurn; //todo Player?

    /**
     * Board constructor
     * Initialises the board and the bag of tiles
     * Assigns players which has a length and names, but no tiles
     * Gives these players their initial tiles and removes those from the bag
     * Called when a new game is started.
     * @param width width of the board
     * @param height height of the board
     * @param players array of players
     */
    Board(int width, int height, Player[] players) {
//        initBoard(width, height);
        board = new Button[width][height];

        this.players = players;
        currentTurn = 0;

        initBag();

        giveInitTiles();
    }

    /**
     * Initialises the board with various empty tiles
     * @param width width of the board
     * @param height height of the board
     */
    private void initBoard(int width, int height) {
        board = new Button[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //todo board probably shouldn't have buttons; just tiles
//                board[i][j] = new Tile("", -1);//todo str
            }
        }
    }

    /**
     * Initialises the bag with the standard list of tiles
     */
    private void initBag() {
        bag = new ArrayList<>(Main.NUM_PIECES);

        ArrayList<String> tileInfos = FileUtils.read("src/sample/tiles.txt");
        for (String tileInfo : tileInfos) {
            String[] s = tileInfo.split(" "); // str num val
            String str = s[0];
            int num = Integer.parseInt(s[1]);
            int val = Integer.parseInt(s[2]);

            Main.letters.put(str, val); //todo move?

            for (int j = 0; j < num; j++) {

                //todo move elsewhere?
                Button button = new Button(str);
                button.setMinSize(30, 30);
                button.setMaxSize(30, 30);
                button.setOnAction(e -> Main.currentTile = button); // rack tile clicked

                bag.add(button);
            }
        }
    }

    /**
     * Randomly gives initial tiles to players, and removes them from the bag
     */
    private void giveInitTiles() {
        for (Player player : players) {
            for (int i = 0; i < Main.RACK_SIZE; i++) {
                player.add(getFromBag());
            }
        }
    }

    /**
     * Gets and removes random button from the bag
     * @return random button from the bag
     */
    private Button getFromBag() {
        Random rand = new Random();
        int n = rand.nextInt(bag.size());
        return bag.remove(n);
    }

    //todo move to board?
    /**
     * Returns if the list of tiles form a valid move, i.e.;
     * Are in a line, are connected,
     * and those connections form valid words
     * @param tiles list of tiles
     * @return true if the list of tiles forms a valid move, false otherwise.
     */
    int move(ArrayList<Button> tiles) {
        if (tiles.isEmpty()) return 0;
        if (!ButtonUtils.isLine(tiles)) return -1;
        ArrayList<String> connections = Main.getConnections(tiles, board);
        return Main.areValidWords(connections, tiles);
    }

    Button[][] getBoard() {
        return board;
    }

    Player[] getPlayers() {
        return players;
    }

    /**
     * Refills the rack of the player who just went,
     * then moves to the next turn,
     */
    void nextTurn() {
        refillRack();

        if (++currentTurn >= players.length) {
            currentTurn = 0;
        }
    }

    /**
     * Finds any empty indices, and grabs a new button from the bag
     */
    private void refillRack() {
        for (int i = 0; i < Main.RACK_SIZE; i++) {
            if (players[currentTurn].getButton(i) == null) {
                players[currentTurn].add(i, getFromBag());
            }
        }
    }

    /**
     * Returns the player who's turn it currently is
     * @return the player who's turn it currently is
     */
    Player currentPlayer() {
        return players[currentTurn];
    }

    Button setButton(int i, int j, String txt) {
        board[i][j] = new Button(txt);
        return board[i][j];
    }

    String getScores() {
        String scores = "Scores:\n";
        for (Player player : players) {
            scores += player.getName() + ": " + player.getScore() + "\n";
        }
        return scores;
    }
}