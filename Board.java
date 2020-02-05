package sample;

import java.util.ArrayList;
import java.util.Random;

import static sample.ButtonUtils.areLines;
import static sample.ButtonUtils.getConnections;
import static sample.FileUtils.read;
import static sample.Main.NUM_PIECES;
import static sample.Main.RACK_SIZE;
import static sample.MathsUtils.thereExists;

public class Board {
    private Tile[][] board;
    private CircularArray<Player> players;
    private ArrayList<Tile> bag;

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
    Board(int width, int height, CircularArray<Player> players) {
//        initBoard(width, height);
        board = new Tile[width][height];

        this.players = players;

        initBag();

        giveInitTiles();
    }

    //todo
    /**
     * Initialises the board with various empty tiles
     * @param width width of the board
     * @param height height of the board
     */
    private void initBoard(int width, int height) {
        board = new Tile[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
//                board[i][j] = new Tile("", -1);
            }
        }
    }

    /**
     * Initialises the bag with the standard list of tiles
     */
    private void initBag() {
        bag = new ArrayList<>(NUM_PIECES);

        ArrayList<String> tileInfos = read("src/sample/tiles.txt");
        for (String tileInfo : tileInfos) {
            String[] s = tileInfo.split(" "); // str num val
            String str = s[0];
            int num = Integer.parseInt(s[1]);

            for (int j = 0; j < num; j++) {
                Tile tile = new Tile(str);
                tile.setEvent(e -> Main.currentTile = tile);
                bag.add(tile);
            }
        }
    }

    /**
     * Randomly gives initial tiles to players, and removes them from the bag
     */
    private void giveInitTiles() {
        for (Player player : players) {
            for (int i = 0; i < RACK_SIZE; i++) {
                player.getRack().add(getFromBag());
            }
        }
    }

    /**
     * Gets and removes random button from the bag
     * @return random button from the bag
     */
    private Tile getFromBag() {
        Random rand = new Random();
        int n = rand.nextInt(bag.size());
        return bag.remove(n);
    }

    /**
     * Returns if the list of tiles form a valid move, i.e.;
     * Are in a line, are connected,
     * and those connections form valid words
     * @param tiles list of tiles
     * @return true if the list of tiles forms a valid move, false otherwise.
     */
    public int move(ArrayList<Tile> tiles) {
        if (tiles.isEmpty()) return 0;
        ArrayList<ArrayList<Tile>> connections = getConnections(tiles, board);
        if (connections.size() == 0) {
            if (!thereExists(tiles,ButtonUtils::isInCentre)) return -1;
            else connections.add(tiles);
        }
        if (!areLines(connections)) return -1;
        return Dictionary.areValidWords(ButtonUtils.toString(connections));
    }

    CircularArray<Player> getPlayers() {
        return players;
    }

    /**
     * Refills the rack of the player who just went,
     * then moves to the next turn,
     */
    public void nextTurn() {
        refillRack();
    }

    /**
     * Finds any empty indices, and grabs a new button from the bag
     */
    private void refillRack() {
        Player currentPlayer = players.next();
        while (currentPlayer.getRack().size() < RACK_SIZE) {
            currentPlayer.getRack().add(getFromBag());
        }
    }

    /**
     * Returns the player who's turn it currently is
     * @return the player who's turn it currently is
     */
    Player currentPlayer() {
        return players.peek();
    }

    Tile setTile(int i, int j, String txt) {
        board[i][j] = new Tile(txt);
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