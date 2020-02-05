package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

import static sample.ButtonUtils.remove;
import static sample.TileUtils.getImgs;



/*
Major
Special board tiles
General error checks and responses(e.g. invalid move)
todo blanks not working

Medium
Add ability to draw new rack
Error checking

Peripherals
Variable rules: plurals, names, verbs, places, etc.
Variable dictionaries: add and remove words, remember custom dictionaries as well as default
trie for dictionary
 */

public class Main extends Application {

    private static final int SCREEN_WIDTH = 700;
    private static final int SCREEN_HEIGHT = 600;
    private static final int BOARD_WIDTH = 15;
    private static final int BOARD_HEIGHT = 15;
    final static int NUM_PIECES = 100;
    final static int RACK_SIZE = 7;
    private static final int MAX_PLAYERS = 4;
    private static final int MIN_PLAYERS = 2;

    static final String BLANK = "Empty";

    private Scene menuScene, gameScene, setupScene;

    private Board board;

    static Tile currentTile; // tile last clicked on
    private GridPane currentRack;//todo rack in Player could be GridPane?
    private ArrayList<Tile> currentTurnTiles; // tiles that have been placed this turn

    @Override
    public void start(Stage stage) throws Exception {
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

        Button quit = new Button("Quit");
        quit.setOnAction(e -> System.exit(0));

        menu.getChildren().addAll(resumeGame, newGame, options, quit);

        return new Scene(menu, SCREEN_WIDTH, SCREEN_HEIGHT);
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
    private void setToCurrentTile(Tile boardTile) {
        if (currentTile == null) return;

        // add current tile to board
        boardTile.set(currentTile.getText());
        boardTile.setEvent(e -> returnToCurrentRack(boardTile));
        currentTurnTiles.add(boardTile);

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
    private void returnToCurrentRack(Tile boardTile) {
        // add tile to rack
        Tile rackTile = new Tile(boardTile.getText());
        rackTile.setEvent(e -> currentTile = rackTile);
        board.currentPlayer().getRack().add(rackTile);
        currentRack.add(rackTile.getImg(), board.currentPlayer().getRack().size(), 0);

        // reset old tile
        boardTile.set(BLANK);
        boardTile.setEvent(e2 -> setToCurrentTile(boardTile));
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
                Tile tile = new Tile(BLANK);
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
//        rack.addRow(0, board.currentPlayer().getRackImgs());
        rack.addRow(0, getImgs(board.currentPlayer().getRack()));
        border.setBottom(rack);
        return rack;
    }

    private GridPane drawBoard() {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                Tile tile = board.setTile(i,  j, BLANK);
                tile.setEvent(e -> setToCurrentTile(tile));
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
        Button finishTurn = new Button("Finish turn");
        finishTurn.setOnAction(e -> {
            int score = board.move(currentTurnTiles);
            if (score != -1) {
                board.currentPlayer().add(score);
                scores.setText(board.getScores());
                board.nextTurn();
                currentRack = drawNextRack(border);
            } else { // if invalid move
                Text text = new Text("Invalid move!");
                side.addRow(BOARD_WIDTH+3, text);
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
        return side;
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
            stage.setScene(gameScene);
        });

        side.getChildren().addAll(addPlayer, submit);

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
        CircularArray<Player> players = new CircularArray<>(vbox.getChildren().size());
        for (int i = 0; i < players.size(); i++) {
            players.add(new Player(((TextField)((HBox)vbox.getChildren().get(i)).getChildren().get(0)).getText()));
        }
        return players;
    }
}
