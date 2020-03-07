package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static sample.TileUtils.remove;
import static sample.FileUtils.readToCircularArray;
import static sample.TileUtils.getImgs;



/*
Major
General error checks and responses(e.g. invalid move)
ai
todo bug; see screenshots
todo maybe tile shouldn't inc. image, and image should be found when tile is made
todo can remove tile, and replace with string and a map from those strings to images and functions
todo add response when clicked

Medium
Add ability to draw new rack
Error checking

Peripherals
Variable rules: plurals, names, verbs, places, etc.
Variable dictionaries: add and remove words, remember custom dictionaries as well as default
trie for dictionary
 */

public class Main extends Application {

    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 700;
    private static final int BOARD_WIDTH = 15;
    private static final int BOARD_HEIGHT = 15;
    final static int NUM_PIECES = 100;
    final static int RACK_SIZE = 7;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;
    static final String[][] boardTiles = new String[BOARD_WIDTH][BOARD_HEIGHT];

    private static final int MAX_DICT_BUTTONS = 20;

    private Scene menuScene, setupScene, gameScene, nextTurnScene, optionsScene, dictScene;

    private Board board;

    //todo could be parameters to funcs?
    static UserTile currentTile; // tile last clicked on
    private GridPane currentRack;
    private ArrayList<Tile> currentTurnTiles; // tiles that have been placed this turn

    @Override
    public void start(Stage stage) {
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
        VBox menu = new VBox();
        menu.setPadding(new Insets(15, 12, 15, 12));
        menu.setSpacing(10);
//        menu.setAlignment(Pos.CENTER);

        Button resumeGame = new Button("Resume Game");
        resumeGame.setOnAction(e -> {
            if (board != null) stage.setScene(gameScene);
        });

        Button newGame = new Button("New Game");
        newGame.setOnAction(e -> {
            setupScene = initSetupScene(stage);
            stage.setScene(setupScene);
        });

        Button options = new Button("Options");
        options.setOnAction(e -> {
            optionsScene = initOptionsScene(stage);
            stage.setScene(optionsScene);
        });

        Button quit = new Button("Quit");
        quit.setOnAction(e -> System.exit(0));

        menu.getChildren().addAll(resumeGame, newGame, options, quit);

        return new Scene(menu, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private Scene initSetupScene(Stage stage) {
        HBox scene = new HBox();
        scene.setSpacing(37);

        VBox players = new VBox();
        players.setPadding(new Insets(15, 12, 15, 12));
        players.setSpacing(10);

        for (int i = 0; i < MIN_PLAYERS; i++) {
            players.getChildren().add(newPlayerHBox(i+1));
        }

        VBox side = new VBox();
        side.setPadding(new Insets(15, 12, 15, 12));
        side.setSpacing(10);

        Button addPlayer = new Button("Add Player");
        addPlayer.setOnAction(e -> {
            if (players.getChildren().size() < MAX_PLAYERS) {
                HBox hbox = newPlayerHBox(players.getChildren().size()+1);
                Button removePlayer = new Button("X");
                scene.setSpacing(0);//todo temp; to stop addPlayer from moving
                removePlayer.setOnAction(e2 -> {
                    players.getChildren().remove(hbox);
                    if (players.getChildren().size() <= 2) {
                        scene.setSpacing(37);//todo temp; to stop addPlayer from moving
                    }
                });
                hbox.getChildren().add(removePlayer);
                players.getChildren().add(hbox);
            }
        });

        Button submit = new Button("Start game");
        submit.setOnAction(e -> {
            gameScene = initGameScene(stage, parsePlayers(players));
//            nextTurnScene = initNextTurnScene(stage);
            stage.setScene(gameScene);
        });

        Button menu = new Button("Menu");
        menu.setOnAction(e -> stage.setScene(menuScene));

        side.getChildren().addAll(addPlayer, submit, menu);

        scene.getChildren().addAll(players, side);

        return new Scene(scene, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private HBox newPlayerHBox(int playerNum) {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);

        TextField name = new TextField("Player " + playerNum);

        ToggleGroup playerType = new ToggleGroup();
        ToggleButton human = new ToggleButton("Human");
        human.setToggleGroup(playerType);
        ToggleButton ai = new ToggleButton("AI");
        ai.setToggleGroup(playerType);

        if (playerNum == 1) playerType.selectToggle(human);
        else playerType.selectToggle(ai);

        hbox.getChildren().addAll(name, human, ai);
        return hbox;
    }

    /**
     * Parse from the setup menu the list of players
     * @param vbox setup menu
     * @return list of players
     */
    private CircularArray<Player> parsePlayers(VBox vbox) {
        int size = vbox.getChildren().size();
        CircularArray<Player> players = new CircularArray<>(size);
        for (int i = 0; i < size; i++) {
            HBox hbox = (HBox) vbox.getChildren().get(i);
            TextField text = (TextField) hbox.getChildren().get(0);
            ToggleButton human = (ToggleButton) hbox.getChildren().get(1);
            if (human.isSelected()) players.add(new Player(text.getText()));
            else players.add(new PlayerAI(text.getText()));
        }
        return players;
    }

    /**
     * Initialises game scene with nodes;
     * Racks: 1 seen rack for currentPlayer which can be interacted with,
     * and 1-3 unseen racks which are static.
     * Main Board: GridPane of empty tiles, some of which have attributes.
     * These interact with the tiles from the racks.
     * Side buttons: menu and quit buttons, and a next turn button.
     * Called when 'Resume Game' or 'Start Game' are clicked
     * @param stage Primary stage
     * @return Game Scene
     */
    private Scene initGameScene(Stage stage, CircularArray<Player> players) {

        // init board
        board = new Board(BOARD_WIDTH, BOARD_HEIGHT, players);

        // draw racks, board, and menu
        BorderPane border = new BorderPane();

        drawBlankRacks(border);
        currentRack = drawNextRack(border);

        border.setCenter(drawBoard());

        border.setRight(drawBoardMenu(stage, border));

        return new Scene(border, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    /**
     * Event for when empty board tile is clicked
     * Sets board tile to currentTile,
     * removes currentTile from currentPlayer's rack,
     * resets currentTile
     * @param boardTile board tile
     */
    private void setToCurrentTile(BoardTile boardTile, GridPane grid) {
        if (currentTile == null) return;

        // add current tile to board
        UserTile newTile = new UserTile(currentTile.getText());
        newTile.setEvent(e -> returnToCurrentRack(newTile, grid));
        int row = GridPane.getRowIndex(boardTile.getImg());
        int col = GridPane.getColumnIndex(boardTile.getImg());
        board.getBoard()[row][col] = newTile;
        grid.add(newTile.getImg(), col, row);
        currentTurnTiles.add(newTile);

        // remove current tile from rack
        board.currentPlayer().getRack().remove(currentTile);
        remove(currentRack, currentTile);
        currentTile = null;
    }

    /**
     * Event for when non-empty board tile is clicked
     * Creates a new tile replicating the clicked tile,
     * adds this tile to currentPlayer's rack,
     * resets the board tile to empty and removes it from currentTurnTiles
     * @param boardTile board tile
     */
    private void returnToCurrentRack(UserTile boardTile, GridPane grid) {
        // add tile to rack
        UserTile rackTile = new UserTile(boardTile.getText());
        rackTile.setEvent(e -> currentTile = rackTile);
        board.currentPlayer().getRack().add(rackTile);
        currentRack.add(rackTile.getImg(), board.currentPlayer().getRack().size(), 0);

        // reset old tile
        int row = GridPane.getRowIndex(boardTile.getImg());
        int col = GridPane.getColumnIndex(boardTile.getImg());
        BoardTile newTile = new BoardTile(boardTiles[row][col]);
        newTile.setEvent(e2 -> setToCurrentTile(newTile, grid));
        board.getBoard()[row][col] = newTile;
        grid.add(newTile.getImg(), col, row);
        currentTurnTiles.remove(boardTile);
        currentTile = null;
    }

    /**
     * Create tiles for blank racks at top, left, and right borders respectively
     * Called when a new game is started.
     * @param border BorderPane of the Scene
     */
    private void drawBlankRacks(BorderPane border) {
        for (int j = 1; j < board.getPlayers().size(); j++) {
            GridPane rack = new GridPane();

            // create tiles
            for (int i = 0; i < RACK_SIZE; i++) {
                Tile tile = new UserTile(Dictionary.getBlank());
                if (j < 2) rack.add(tile.getImg(), i, 0);
                else rack.add(tile.getImg(), 0, i);
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
            for (Tile tile : currentTurnTiles) {
                tile.setEvent(null);
            }
        }
        currentTurnTiles = new ArrayList<>(RACK_SIZE);

        // set visible rack to new current player's rack
        GridPane rack = new GridPane();
        rack.addRow(0, getImgs(board.currentPlayer().getRack()));
        border.setBottom(rack);
        return rack;
    }

    private GridPane drawBoard() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                BoardTile tile = (BoardTile) board.getBoard()[i][j];
                tile.setEvent(e -> setToCurrentTile(tile, grid));
                grid.add(tile.getImg(), i, j);
            }
        }
        return grid;
    }

    private GridPane drawBoardMenu(Stage stage, BorderPane border) {

        GridPane side = new GridPane();

        Button menu = new Button("Menu");
        menu.setOnAction(e -> stage.setScene(menuScene));

        Button quit = new Button("Quit");
        quit.setOnAction(e -> System.exit(0));

        Button scores = new Button(board.getScores());

        Text currentTurn = new Text(board.currentPlayer().getName() + "'s turn");

        Button finishTurn = new Button("Finish turn");
        finishTurn.setOnAction(e -> {
            int score = board.move(currentTurnTiles);
            if (score != -1) {
                board.nextTurn(score);
                scores.setText(board.getScores());
                currentRack = drawNextRack(border);
                stage.setScene(initNextTurnScene(stage));
            } else { // if invalid move
                Text text = new Text("Invalid move!");
                side.addRow(BOARD_WIDTH+4, text);
                stage.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<>() {
                    @Override
                    public void handle(javafx.scene.input.MouseEvent event){
                        side.getChildren().remove(text);
                        stage.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
                    }
                });
            }
        });

        side.addRow(BOARD_WIDTH+1, finishTurn, menu, quit);
        side.addRow(BOARD_WIDTH+2, scores);
        side.addRow(BOARD_WIDTH+3, currentTurn);

        return side;
    }

    private Scene initNextTurnScene(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setAlignment(Pos.CENTER);

        Text playersTurn = new Text(board.currentPlayer().getName() + "'s turn");
        Button startTurn = new Button("Start turn");
        startTurn.setOnAction(e -> {
            // change current turn text box
            BorderPane pane = (BorderPane) gameScene.getRoot();
            GridPane grid = (GridPane) pane.getRight();
            Text currentPlayer = (Text) grid.getChildren().get(4);
            currentPlayer.setText(board.currentPlayer().getName() + "'s turn");

            stage.setScene(gameScene);
        });

        vbox.getChildren().addAll(playersTurn, startTurn);

        return new Scene(vbox, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    //------------------
    /*
    todo options;
        dictionary
            various dicts, with a ToggleGroup for the current dictionary, std dict being static; or have rulesets..?
                list of all words in the dict, with a tick beside / different colour for chosen ones
                option to add new words
                reset button
     */
    private Scene initOptionsScene(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        Button dicts = new Button("Dictionaries");
        dicts.setOnAction(e -> {
            dictScene = initDictsScene(stage);
            stage.setScene(dictScene);
        });

        vbox.getChildren().addAll(dicts);

        return new Scene(vbox, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private Scene initDictsScene(Stage stage) {
        VBox vbox = new VBox();
        vbox.setSpacing(10);

        //todo replace with vbox.getChildren.addAll(readDicts()) ?
        File dir = new File("src/sample/dicts");
        List<File> dicts = Arrays.asList(dir.listFiles());
        for (File dict : dicts) {
            Button b = readDict(dict, stage);
            vbox.getChildren().add(b);
        }

        return new Scene(vbox, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    private Button readDict(File file, Stage stage) {
        Button out = new Button(file.getName());
        out.setOnAction(e -> {
            Scene dictScene = initDictScene(stage, file);
            stage.setScene(dictScene);
        });
        return out;
    }

    private Scene initDictScene(Stage stage, File file) {
        HBox hbox = new HBox();

        CircularArray<String> dict = readToCircularArray(file);
//        ArrayList<String> dict = read(file);

        VBox vbox = getNext(dict);
        int pageIndex = 0;
//        int first = (pageIndex++)*MAX_DICT_BUTTONS;
//        VBox vbox = new VBox();
//        vbox.getChildren().addAll(dict.subList(first, first+MAX_DICT_BUTTONS));


        Button addWord = new Button("+");
        addWord.setOnAction(e -> addWord(vbox));

        Button nextPage = new Button(">");
        nextPage.setOnAction(e -> {
            hbox.getChildren().remove(0);
            VBox vbox2 = getNext(dict);
            hbox.getChildren().add(0, vbox2);
        });

        hbox.getChildren().addAll(vbox, addWord, nextPage);

        return new Scene(hbox, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    //todo yucky
    //todo generalise; get next n elements of a circular array (or something else that remembers the current index)
    //todo or just use subList()
    private VBox getNext(CircularArray<String> list) {
        VBox vbox = new VBox();
        for (int i = 0; i < MAX_DICT_BUTTONS; i++) {// && (list.getOutIndex() != list.size())
            Button b = new Button(list.next());
            vbox.getChildren().add(b);
        }
        return vbox;
    }

    private void addWord(VBox vbox) {
        TextField textField = new TextField();
        textField.setPromptText("new word");
        textField.setOnAction(e -> {
            //todo write to file
            Button word = new Button(textField.getText());
            vbox.getChildren().add(word);
            vbox.getChildren().sort(Comparator.comparing(b -> b.getAccessibleText()));
        });
    }

    //------------

    /**
     * Get the time taken in nanoseconds for a function to run
     * @param func function
     * @param obj object parameter to the function
     * @param <T> function input type
     * @param <U> function output type
     * @return time taken for function to run
     */
    static <T, U>long getTimeTaken(Function<T, U> func, T obj) {
        final long start = System.nanoTime();
        func.apply(obj);
        final long end = System.nanoTime();
        return end - start;
    }

    static <T, U, V>long getTimeTaken(BiFunction<T, U, V> func, T obj1, U obj2) {
        final long start = System.nanoTime();
        func.apply(obj1, obj2);
        final long end = System.nanoTime();
        return end - start;
    }
}
