package sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import static sample.TileUtils.*;
import static sample.FileUtils.read;
import static sample.FileUtils.readToMap;
import static sample.Main.*;
import static sample.MathsUtils.sortFromAngle;
import static sample.MathsUtils.thereExists;

class Board {
    private Tile[][] board;
    private CircularArray<Player> players;
    private ArrayList<UserTile> bag;

    private Logger logger;

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
        logger = Logger.getLogger(Board.class.getName());
        initBoard(width, height);

        this.players = players;

        initBag();

        giveInitTiles();
    }

    /**
     * Initialises the board with various BoardTiles,
     * as described by 'boardTiles'
     * @param width width of the board
     * @param height height of the board
     */
    private void initBoard(int width, int height) {
        board = new Tile[width][height];

        initBoardTiles();

        for (int i = 0; i < boardTiles.length; i++) {
            for (int j  = 0; j < boardTiles[i].length; j++) {
                board[i][j] = new BoardTile(boardTiles[i][j]);
            }
        }
    }

    /**
     * Initialises each tile on the board with the relevant type
     * as determined by boardTiles
     */
    private static void initBoardTiles() {
        ArrayList<String> boardTilesList = read("src/sample/board_tiles.txt");
        if (boardTilesList == null) {
            System.out.println("no board tiles..?");
            return;
        }
        String[] boardTilesLines = ArrUtils.toArr(boardTilesList);

        for (int i = 0; i < boardTilesLines.length; i++) {
            String[] temp = boardTilesLines[i].split(" ");
            int iMul = (boardTilesLines.length*2)-2-i;
            for (int j = 0; j < temp.length; j++) {
                int jMul = (boardTilesLines.length*2)-2-j;
                boardTiles[i][j] = temp[j];
                boardTiles[iMul][j] = temp[j];
                boardTiles[i][jMul] = temp[j];
                boardTiles[iMul][jMul] = temp[j];
            }
        }
    }

    /**
     * Initialises the bag with the standard list of tiles
     */
    private void initBag() {
        bag = new ArrayList<>(NUM_PIECES);

        HashMap<String, Integer> alphabet = readToMap("src/sample/alphabet_freq.txt", " ");
        if (alphabet == null) {
            System.out.println("no alphabet..?");
            return;
        }

        for (HashMap.Entry<String, Integer> letter : alphabet.entrySet()) {
            for (int j = 0; j < letter.getValue(); j++) {
                UserTile tile = new UserTile(letter.getKey());
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
    private UserTile getFromBag() {
        Random rand = new Random();
        int n = rand.nextInt(bag.size());
        return bag.remove(n);
    }

    /**
     * Checks if the list of tiles placed form a valid move,
     * when combined with any surrounding connected tiles, i.e.;
     * are in a connected line and
     * the strings made from those connections form valid words
     * @param newTiles list of new tiles placed this turn by a player
     * @return true if the list of tiles forms a valid move, false otherwise.
     */
    int move(ArrayList<Tile> newTiles) {
        System.out.println("\n" + currentPlayer() + " moving");
//        logger.log(Level.ALL, "\nMoving");

        if (newTiles.isEmpty()) return 0;

        // get list of tile lists made from surrounding tiles
        ArrayList<ArrayList<Tile>> tileLists = getConnections(newTiles, board, UserTile.class);

        System.out.println("Potential connections (" + tileLists.size() + ");");
        for (ArrayList<Tile> connection : tileLists) {
            for (Tile t : connection) {
                System.out.print(t.toString() + " ");
            }
            System.out.println();
        }

        // if not connected
        if (tileLists.isEmpty()) {
            // if first move
            if (thereExists(newTiles,TileUtils::isInCentre)) {
                System.out.println("First move - added to connections");
                tileLists.add(newTiles);
            }
            else return -1;
        }

        // sort tileLists by angles
        for (ArrayList<Tile> connection : tileLists) {
            double angle = getAngle(connection);
            sortFromAngle(connection, angle, Tile::getX, Tile::getY);
        }

        System.out.println("Actual sorted connections (" + tileLists.size() + ");");
        for (ArrayList<Tile> connection : tileLists) {
            for (Tile t : connection) {
                System.out.print(t.toString() + " ");
            }
            System.out.println();
        }

        // if any tileLists aren't in a line
        if (thereExists(tileLists, c -> !isLine(c))) return -1;

        // if strings made by tileLists are valid
        ArrayList<ArrayList<UserTile>> userConnections = toUserTile(tileLists);
        return Dictionary.getValues(userConnections, boardTiles);
    }

    /**
     * todo Refills the rack of the player who just went,
     * then moves to the next turn,
     */
    void nextTurn(int score) {
        Player prevPlayer = players.next();
        prevPlayer.add(score);
        refillRack(prevPlayer);

        if (players.peek().getClass().equals(PlayerAI.class)) {
            score = ((PlayerAI) players.peek()).move(board);
            nextTurn(score);
        }
    }

    /**
     * Finds any empty indices, and grabs a new button from the bag
     */
    private void refillRack(Player player) {
        while (player.getRack().size() < RACK_SIZE) {
            player.getRack().add(getFromBag());
        }
    }

    /**
     * Returns the matrix
     * @return the matrix
     */
    Tile[][] getBoard() {
        return board;
    }

    /**
     * Returns the CircularArray of players
     * @return the CircularArray of players
     */
    CircularArray<Player> getPlayers() {
        return players;
    }

    /**
     * Returns the player who's turn it currently is
     * @return the player who's turn it currently is
     */
    Player currentPlayer() {
        return players.peek();
    }

    /**
     * Returns a string representation of the scores of the players
     * @return a string representation of the scores of the players
     */
    String getScores() {
        String scores = "Scores:\n";
        for (Player player : players) {
            scores += player.getName() + ": " + player.getScore() + "\n";
        }
        return scores;
    }
}