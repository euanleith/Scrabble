package sample;

import java.util.ArrayList;
import java.util.Random;

public class Board {
    private Tile[][] board;
    private Player[] players;
    private Tile[] bag;

    // contains indices for all remaining tiles in the bag,
    // so that an index can be chosen cleanly at random
    private ArrayList<Integer> bagIndices;
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
        initBoard(width, height);

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
        board = new Tile[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //todo board probably shouldn't have buttons; just tiles
                board[i][j] = new Tile("", -1);//todo str
            }
        }
    }

    /**
     * Initialises the bag with the standard list of tiles
     */
    private void initBag() {
        bag = new Tile[Main.NUM_PIECES];
        bagIndices = new ArrayList<>(bag.length);
        int i = 0;

        String[] tileInfos = FileUtils.readFile("src/sample/tiles.txt");
        for (String tileInfo : tileInfos) {
            String[] s = tileInfo.split(" "); // str num val
            String str = s[0];
            int num = Integer.parseInt(s[1]);
            int val = Integer.parseInt(s[2]);
            for (int j = 0; j < num; j++) {
                bagIndices.add(i);
                bag[i++] = new Tile(str, val);
            }
        }
    }

    /**
     * Randomly gives initial tiles to players, and removes them from the bag
     */
    private void giveInitTiles() {
        for (Player player : players) {
            for (int i = 0; i < Main.RACK_SIZE; i++) {
                Random rand = new Random();
                int n = rand.nextInt(bagIndices.size());
                player.add(bag[n]);
                bagIndices.remove(n); // note removing at index n
            }
        }
    }

    Tile[][] getBoard() {
        return board;
    }

    Player[] getPlayers() {
        return players;
    }

    /**
     * Moves to the next turn
     */
    void nextTurn() {
        if (++currentTurn >= players.length) {
            currentTurn = 0;
        }
    }

    /**
     * Returns the player who's turn it currently is
     * @return the player who's turn it currently is
     */
    Player currentPlayer() {
        return players[currentTurn];
    }
}