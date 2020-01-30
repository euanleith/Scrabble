package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.*;
import java.util.function.Predicate;

/*
Major
Change setup scene to be like civ
General error checks and responses(e.g. invalid move)

Medium
Organise into classes;
    how to structure buttons and their events?
    Screen class??
    basically idk, but it'll be good practice to figure out
        basic structure is that I have a number of screens, each with a list of buttons, each with events. I also have the functionality of the game (i.e. backend), which should be connected to the ui part at few and specific places. e.g. isValidMove is backend, and only needs to be supplied the most recent move and the board. How would this be integrated? There's plenty of options, idk which is the best.
        So there should be an object Board, but what exactly should it have? It could just be an 2d array of Buttons and players, and the functions could be abstracted.
            You have a 2d array, and an action which alters the contents of the array, which is checked if valid. Note that elements are added like a buffer, then the buffer is flushed when 'finish turn' is pressed.
Add ability to draw new rack
Might be able to do a more efficient dictionary.contains() since I know its alphabetical
Error checking

Peripherals
Variable rules: plurals, names, verbs, places, etc.
Variable dictionaries: add and remove words, remember custom dictionaries as well as default
 */

public class Main extends Application {

    private final static int SCREEN_WIDTH = 700;
    private final static int SCREEN_HEIGHT = 600;
    private final static int BOARD_WIDTH = 15;
    private final static int BOARD_HEIGHT = 15;
    final static int NUM_PIECES = 100;
    final static int RACK_SIZE = 7;
    private final static int MAX_PLAYERS = 4;

    private Scene menuScene, gameScene, setupScene;

    private static ArrayList<String> dictionary;
    static HashMap<String, Integer> letters;

    private int numPlayers;
    private Player[] players;
    private int playerIndex;

    private boolean gameActive;

    private Board board;

    static Button currentTile; // tile last clicked on
    private GridPane currentRack;
    private ArrayList<Button> currentTurnTiles; // tiles that have been placed this turn

    @Override
    public void start(Stage stage) throws Exception {

        dictionary = FileUtils.read("src/sample/dict.txt");
        letters = new HashMap<>(27);

        menuScene = initMenuScene(stage);

        stage.setScene(menuScene);

        stage.show();
    }

    /**
     * Initialises menu scene with nodes;
     * Resume button: sets scene to game scene if a game is active
     * New Game Button: creates a new game and sets scene to game scene
     * Options button: todo
     * Quit button: exits the program
     * Called on start up, and when 'Menu' button is clicked.
     * @param stage Primary stage
     * @return Menu Scene
     */
    private Scene initMenuScene(Stage stage) {
        gameActive = false;

        ListView<Button> menu = new ListView<>();

        Button resumeGame = new Button("Resume Game");
        resumeGame.setOnAction(e -> {
            if (gameActive) stage.setScene(gameScene);
        });

        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> {
            setupScene = initSetupScene(stage);
            stage.setScene(setupScene);
        });

        Button options = new Button("Options");

        Button quit = new Button("Quit");
        quit.setOnAction(e -> System.exit(0));

        menu.getItems().addAll(resumeGame, newGame, options, quit);

        return new Scene(menu, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    //todo clean up
    /**
     * Initialises game scene with nodes;
     * Racks: 1 seen rack for currentPlayer which can be interacted with,
     * and 1-3 unseen racks which are static.
     * Main Board: GridPane of buttons containing empty tiles, some of which have attributes.
     * These interact with the buttons from the racks.
     * Side buttons: menu and quit buttons, and a next turn button.
     * Called when todo
     * @param stage Primary stage
     * @return Game Scene
     */
    private Scene initGameScene(Stage stage) {
        BorderPane border = new BorderPane();

        // init board
        board = new Board(BOARD_WIDTH, BOARD_HEIGHT, players);

        // draw racks
        setBlankRacks(border);
        currentRack = drawNextRack(border);

        // draw main board
        GridPane gridBoard = new GridPane();
        gridBoard.setPadding(new Insets(10, 10, 10, 10));
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                Button button = board.setButton(i,  j, "");
                button.setMinSize(30, 30);
                button.setMaxSize(30, 30);
                button.setOnAction(e -> emptyBoardTileEvent(button));
                gridBoard.add(button, i, j);
            }
        }
        border.setCenter(gridBoard);

        // draw side buttons
        GridPane side = new GridPane();
        Button menu = new Button("Menu");
        menu.setOnAction(e -> stage.setScene(menuScene));
        Button quit = new Button("Quit");
        quit.setOnAction(e -> System.exit(0));
        Button scores = new Button(board.getScores());
        Button finishTurn = new Button("Finish turn");
        finishTurn.setOnAction(e -> {
            int score = board.move(currentTurnTiles);
            if (score != -1) {
                board.currentPlayer().add(score);
                scores.setText(board.getScores());
                board.nextTurn();
                currentRack = drawNextRack(border);
            }//todo else say 'invalid move' and something for no tiles being placed
        });
        side.addRow(BOARD_WIDTH+1, finishTurn, menu, quit);
        side.addRow(BOARD_WIDTH+2, scores);
        border.setRight(side);

        return new Scene(border, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    //-------------------todo move all of these

    //todo returns a list of strings which are the strings created by the new connections on the board from this turn
    //check each tile, and for each connection, create a new string from the string of buttons it creates
    //note: assumes list is a line
    static ArrayList<String> getConnections(ArrayList<Button> tiles, Button[][] board) {
        ArrayList<String> connections = new ArrayList<>();
        for (Button button : tiles) {
            ArrayList<Button> surrounding = getSurroundingButtons(button, tiles, board);
            for (Button connected : surrounding) {
                connections.add(ButtonUtils.toString(connected, tiles));
            }
        }
        return connections;
    }

    /**
     * Returns the list of non-empty buttons surrounding a given button
     * that are within the confines of the board
     * @param button button
     * @param board board of buttons
     * @return list of non-empty buttons surrounding a given button
     * that are within the confines of the board
     */
    static ArrayList<Button> getSurroundingButtons(Button button, ArrayList<Button> tiles, Button[][] board) {
        ArrayList<Button> buttons = new ArrayList<>(8);
        ArrayList<Pair<Integer, Integer>> positions = new ArrayList<>();
        positions.add(new Pair<>(0,-1));
        positions.add(new Pair<>(-1,0));
        positions.add(new Pair<>(1,0));
        positions.add(new Pair<>(0,1));
        for (Pair<Integer, Integer> pos : positions) {
            int x = GridPane.getColumnIndex(button) + pos.getKey();
            int y = GridPane.getRowIndex(button) + pos.getValue();
            if (x >= 0 && x < board.length &&
                    y >= 0 && y < board[x].length &&
                    !board[x][y].getText().equals("") &&//todo this will need to be changed when what denotes a used board tile changes
                    !tiles.contains(board[x][y])) {
                buttons.add(board[x][y]);
            }
        }
        return buttons;
    }

    //todo move
    //todo rename; thereExists should return a boolean
    /**
     * Returns the first element of a list for which the predicate is true
     * @param list ArrayList
     * @param pred Predicate
     * @param <T> Type of list and pred
     * @return the first element of a list for which the predicate is true, null if this doesn't exist
     */
    static <T>T find(List<T> list, Predicate<T> pred) {
        for (T obj : list) {
            if (pred.test(obj)) {
                return obj;
            }
        }
        return null;
    }

    //todo name contains()?
    /**
     * Returns true if there is an element in the list for which the predicate is true
     * @param list ArrayList
     * @param pred Predicate
     * @param <T> Type of list and pred
     * @return true if there is an element in the list for which the predicate is true, false otherwise
     */
    static <T> boolean thereExists(List<T> list, Predicate<T> pred) {
        for (T obj : list) {
            if (pred.test(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the list of words made are all valid
     * @param connections words formed by connections made with existing tiles on the board
     * @param tiles list of new tiles placed
     * @return if all words are valid, the score earned from these words, -1 otherwise
     */
    static int areValidWords(ArrayList<String> connections, ArrayList<Button> tiles) {

        // if not connected, valid if its the first
        // move of the game, and that word is valid
        if (connections.isEmpty()) { // if not connected
            if (thereExists(tiles, ButtonUtils::isInCentre)) {
                double angle = ButtonUtils.getAngle(tiles);
                MathsUtils.sortFromAngle(tiles, angle, Button::getLayoutX, Button::getLayoutY);
                if (isValidWord(tiles)) { // if first move
                    return getScore(ButtonUtils.toString(tiles));
                }
            }
            return -1;
        }

        // if connected, valid if each of the words formed by the
        // connections made with existing tiles on the board are valid
        int score = 0;
        for (String connection : connections) {
            if (!isValidWord(connection)) return -1;
            score += getScore(connection);
        }
        return score;
    }

    /**
     * Returns true if a valid word can be formed
     * from the given list of buttons,
     * i.e. by the string made from the combination
     * of each button's text in order
     * @param buttons list of buttons
     * @return true if a valid word can be formed from the buttons, false otherwise
     */
    static boolean isValidWord(ArrayList<Button> buttons) {
        return isValidWord(ButtonUtils.toString(buttons));
    }

    /**
     * Returns true if a valid word can be formed
     * from the given string.
     * if it contains blank character(s),
     * by replacing each blank with each letter
     * @param str string
     * @return true if a valid word can be formed from the given string, false otherwise
     */
    static boolean isValidWord(String str) {
        String[] arr = str.split("((?<=Blank)|(?=Blank))");
        for (String letter : letters.keySet()) {
            String test = "";
            for (String s : arr) {
                if (s.equals("Blank")) {
                    test += letter;
                    return isValidWord(test + str.substring((ArrUtils.indexOf(arr, s))+5));
                }
                else test += s;
            }
            if (dictionary.contains(test)) return true;
        }
        return false;
    }

    //todo move
    static int getScore(String str) {
        int score = 0;
        String[] arr = str.split("((?<=Blank)|(?=Blank))");
        for (String letter : arr) {
            if (letter.equals("Blank")) score += 2;
            else {
                for (int i = 0; i < letter.length(); i++) {
                    score += letters.get(letter.substring(i, i+1));
                }
            }
        }
        return score;
    }

    //-------------------

    /**
     * Event for when empty board tile is clicked
     * Sets board tile to currentTile,
     * removes currentTile from currentPlayer's rack,
     * resets currentTile
     * @param button board tile
     */
    private void emptyBoardTileEvent(Button button) {
        if (currentTile == null) return;
        button.setText(currentTile.getText());
        board.currentPlayer().remove(currentTile);
        ButtonUtils.remove(currentRack, currentTile);
        currentTurnTiles.add(button); // add to tiles that can be taken back this turn
        button.setOnAction(e -> nonEmptyBoardTileEvent(button)); // when clicked, return to rack
        currentTile = null;
    }

    /**
     * Event for when non-empty board tile is clicked
     * Creates a new button replicating the clicked tile,
     * adds this button to currentPlayer's rack,
     * resets the board tile to empty and removes it from currentTurnTiles
     * @param button board tile
     */
    private void nonEmptyBoardTileEvent(Button button) {
        Button empty = new Button(button.getText());
        empty.setMinSize(30, 30);
        empty.setMaxSize(30, 30);
        empty.setOnAction(e -> currentTile = empty);
        currentRack.add(empty, board.currentPlayer().getRackLength(), 0);
        board.currentPlayer().add(empty);
        currentTurnTiles.remove(button);
        currentTile = null;
        button.setText("");
        button.setOnAction(e2 -> emptyBoardTileEvent(button));
    }

    /**
     * Create buttons for blank racks at top, left, and right borders respectively
     * Called when a new game is started.
     * @param border BorderPane of the Scene
     */
    private void setBlankRacks(BorderPane border) {
        for (int j = 1; j < board.getPlayers().length; j++) {
            GridPane rack = new GridPane();

            // create buttons
            for (int i = 0; i < RACK_SIZE; i++) {
                Button button = new Button("");//todo inc val
                button.setMinSize(30, 30);
                button.setMaxSize(30, 30);
                if (j < 2) rack.add(button, i, 0);
                else rack.add(button, 0, i);
            }

            // add rack to pane
            switch (j) {
                case 1:
                    border.setTop(rack);
                    break;
                case 2:
                    border.setLeft(rack);
                    break;
                case 3:
                    border.setRight(rack);
                    break;
                default:
                    // invalid
                    break;
            }
        }
    }

    /**
     * Draw currentPlayer's rack
     * Called at the start of each player's turn.
     * @param border BorderPane of the Scene
     * @return currentPlayer's rack as a GridPane
     */
    private GridPane drawNextRack(BorderPane border) {

        // reset current turn tiles
        if (currentTurnTiles != null) {
            for (Button b : currentTurnTiles) {
                b.setOnAction(null);
            }
        }
        currentTurnTiles = new ArrayList<>(RACK_SIZE);

        // set visible rack to new current player's rack
        GridPane rack = new GridPane();
        rack.addRow(0, board.currentPlayer().getRack());
        border.setBottom(rack);
        return rack;
    }

    //todo change to be like civ
    /**
     * Initialises setup scene with buttons;
     * todo
     * Called when 'New Game' button is clicked
     * @param stage Primary stage
     * @return Setup Scene
     */
    private Scene initSetupScene(Stage stage) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        Label label = new Label("Num players:");
        grid.add(label,0,0);
        TextField text = new TextField();
        grid.add(text, 0, 1, 2, 1);
        Button submit = new Button("Submit");
        grid.add(submit, 2, 1, 2, 1);

        //todo error cases
        submit.setOnAction(e -> getPlayerNum(text, label, submit, stage));
        text.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                getPlayerNum(text, label, submit, stage);
            }
        });

        return new Scene(grid, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    /**
     * todo
     * @param text
     * @param label
     * @param submit
     * @param stage
     */
    private void getPlayerNum(TextField text, Label label, Button submit, Stage stage) {

        // read numPlayers
        numPlayers = Integer.parseInt(text.getText());
        text.setText("");
        if (numPlayers < 0 || numPlayers > MAX_PLAYERS) return;

        // read names of players
        label.setText("Names");
        players = new Player[numPlayers];
        playerIndex = 0;

        submit.setOnAction(ev -> getPlayerNames(text, stage));
        text.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                getPlayerNames(text, stage);
            }
        });
    }

    /**
     * todo
     * @param text
     * @param stage
     */
    private void getPlayerNames(TextField text, Stage stage) {
        players[playerIndex++] = new Player(text.getText());
        text.setText("");
        if (playerIndex >= numPlayers) {
            gameActive = true;
            gameScene = initGameScene(stage);
            stage.setScene(gameScene);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
