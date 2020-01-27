package sample;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.HashSet;

/*
Variable rules: plurals, names, verbs, places, etc.
Variable dictionary(/ies): add and remove words, remember custom dictionaries as well as default
Change setup scene to be like civ
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

    //todo put in Board?
    private int numPlayers;
    private Player[] players;
    private int playerIndex;

    private boolean gameActive;

    private Board board;

    private Button currentTile; // tile last clicked on
    private GridPane currentRack;
    private HashSet<Button> currentTurnTiles; // tiles that have been placed this turn

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
                Button button = board.getBoard()[i][j].getButton();//todo note this might create a separate pointer
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
        Button finishTurn = new Button("Finish turn");
        finishTurn.setOnAction(e -> {
            board.nextTurn();
            currentRack = drawNextRack(border);
        }); //todo error check
        side.addRow(16, finishTurn, menu, quit);
        border.setRight(side);

        return new Scene(border, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

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
        remove(currentRack, currentTile);
        currentTurnTiles.add(button); // add to tiles that can be taken back this turn
        button.setOnAction(e -> nonEmptyBoardTileEvent(button)); // when clicked, return to rack
        currentTile = null;
    }

    //todo might still be wrong...
    /**
     * Removes button from a GridPane and shift other items left
     * gridpane.getChildren is an ObservableList, which shifts all values when removing elements
     * gridpane however has separate indices which don't change, so I have to change them manually
     * @param button button to be removed
     */
    private void remove(GridPane grid, Button button) {
        for (int i = 0; i < grid.getChildren().size(); i++) {
            if (grid.getChildren().get(i) == button) {
                for (int j = i; j < grid.getChildren().size()-1; j++) {
                    GridPane.setColumnIndex(grid.getChildren().get(j+1), j);
                }
                break;
            }
        }
        grid.getChildren().remove(button);
    }

    /**
     * Event for when non-empty board tile is clicked
     * Creates a new button replicating the clicked tile,
     * adds this button to currentPlayer's rack,
     * resets the board tile to empty
     * @param button board tile
     */
    private void nonEmptyBoardTileEvent(Button button) {
        Button empty = new Button(button.getText());
        empty.setMinSize(30, 30);
        empty.setMaxSize(30, 30);
        empty.setOnAction(e -> currentTile = empty);
        currentRack.add(empty, board.currentPlayer().getRackLength(), 0);
        board.currentPlayer().add(empty);
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

            // add rack to borderpane
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
        // reset current turn tiles todo make function
        if (currentTurnTiles != null) {
            for (Button b : currentTurnTiles) {
                b.setOnAction(null);
            }
        }
        currentTurnTiles = new HashSet<>(RACK_SIZE);

        Player player = board.currentPlayer();
        GridPane rack = new GridPane();
        for (int i = 0; i < RACK_SIZE; i++) {
            Button button = new Button(player.getTile(i).getStr());//todo inc val
            rack.add(button, i, 0);
            button.setMinSize(30, 30);
            button.setMaxSize(30, 30);
            button.setOnAction(e -> currentTile = button); // rack tile clicked
        }
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
