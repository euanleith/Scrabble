package sample;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static sample.ArrUtils.combinations;
import static sample.ArrUtils.getSurrounding;
import static sample.TileUtils.getConnections;
import static sample.TileUtils.toUserTile;

class PlayerAI extends Player {

    PlayerAI(String name) {
        super(name);
    }

    /*
    todo
    find all possible words that can be made
    -look for each combination of various rack tiles with each board tile
    choose one depending on difficulty

    for (tile : board) {
        if (tile.class = usertile) {
            for (surrounding board tiles) {
                for each (free direction (NSEW)) {
                    for each (combination of rack tiles) {
                        if ((rack_combination + tile).isValidWord()) {
                            list.add(rack_combination + tile)
                        }
                    }
                }
            }
        }
    }
    todo maybe end up by updating finish turn button??
     */
//    int move(Tile[][] board) {
//        HashMap<Integer, ArrayList<UserTile>> connections = new HashMap<>();//todo note only keeping one of each score right now...
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board.length; j++) {
//                if (board[i][j].getClass().equals(UserTile.class)) {
//                    ArrayList<Tile> freeTiles = getSurrounding(i, j, board, t -> t.getClass().equals(BoardTile.class));
//                    for (Tile freeTile : freeTiles) {
//                        ArrayList<ArrayList<UserTile>> combinations = combinations(getRack());
//                        for (ArrayList<UserTile> userTiles : combinations) {
//                            //todo for direction
//                            //todo temporarily try each set of userTiles on the board, then getConnections
//                            //todo have to add them to gridpane too?
//                            Tile[][] tempBoard = new Tile[board.length][];
//                            for (int k = 0; k < tempBoard.length; k++) {
//                                tempBoard[i] = board[i].clone();
//                            }
//
//                            //todo temp; only doing south direction right now
//                            for (int k = 1; k <= userTiles.size(); k++) {
//                                if (tempBoard[i][j+k].getClass().equals(UserTile.class)) break;
//                                tempBoard[i][j+k] = userTiles.get(k);
//                            }
//
//                            int value = getValue(userTiles, Main.boardTiles);
//                            if (value != -1) connections.put(value, userTiles);//todo not right way to store it; need tiles and indices
//                        }
//                    }
//                }
//            }
//        }
//        return -1;
//    }

//    int move(Tile[][] board) {
//        HashMap<Integer, ArrayList<UserTile>> connections = new HashMap<>();//todo note only keeping one of each score right now...
//        ArrayList<ArrayList<UserTile>> combinations = combinations(getRack());
//        for (int i = 0; i < board.length; i++) {
//            for (int j = 0; j < board.length; j++) {
//                if (board[i][j].getClass().equals(UserTile.class)) {
//                    ArrayList<Tile> freeTiles = getSurrounding(i, j, board, t -> t.getClass().equals(BoardTile.class));
//                    for (Tile freeTile : freeTiles) {
////                        int angle = (int)getAngle(board[i][j].getX(),board[i][j].getY(),freeTile.getX(),freeTile.getY());
//                        for (ArrayList<UserTile> userTiles : combinations) {
////                            switch (angle) {
////                                case 0://todo maybe wrong way around; I'm saying 0 = to right, 90 = above, etc
////                                case 270:
////                                    userTiles.add(0, (UserTile)board[i][j]);
////                                    break;
////                                case 90:
////                                case 180:
////                                    userTiles.add(userTiles.size()-1, (UserTile)board[i][j]);
////                                    break;
////                            }
////                            Tile[][] tempBoard = new Tile[board.length][];
////                            for (int k = 0; k < tempBoard.length; k++) {
////                                tempBoard[i] = board[i].clone();
////                            }
//                        }
//                    }
//                }
//            }
//        }
//        return -1;
//    }

    //todo combination tiles dont have positions
    int move(Tile[][] board) {
        HashMap<Integer, ArrayList<Tile>> out = new HashMap<>();//todo note only keeping one of each score right now...
        ArrayList<ArrayList<Tile>> combinations = combinations(getTileRack());
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                if (board[i][j].getClass().equals(UserTile.class)) {
                    ArrayList<Tile> freeTiles = getSurrounding(i, j, board, t -> t.getClass().equals(BoardTile.class));
                    for (Tile freeTile : freeTiles) {
                        for (ArrayList<Tile> userTiles : combinations) {
                            //todo place both ways(vertically and horizontally) and getConnections etc for them
                            Tile[][] tempBoard = clone(board);
//                            //todo temp; only doing below right now
                            boolean cont = true;
                            for (int k = 0; k <= userTiles.size()-1; k++) {//todo wrong; only doing below, want to do above/below & left/right depending on angle of freeTile and board[i][j]
                                if (tempBoard[i][j+k+1].getClass().equals(UserTile.class) || outside((int)freeTile.getX(), (int)freeTile.getY()+k+1, tempBoard)) {
                                    cont = false;
                                    break;
                                    //todo give up on this check
                                }
                                userTiles.get(k).getImg().setX(freeTile.getX());
                                userTiles.get(k).getImg().setY(freeTile.getY()+k+1);
                                tempBoard[(int)freeTile.getX()][(int)freeTile.getY()+k+1] = userTiles.get(k);

                            }
                            if (cont) {
                                ArrayList<ArrayList<Tile>> connections = getConnections(userTiles, tempBoard, UserTile.class);
                                ArrayList<ArrayList<UserTile>> userConnections = toUserTile(connections);//todo temp; converting back and forth between usertile and tile
                                int value = Dictionary.getValues(userConnections, Main.boardTiles);
                                out.put(value, userTiles);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("AI connections; " + out.toString());
        int maxValue = Collections.max(out.keySet());
        ArrayList<Tile> tiles = out.get(maxValue);
        for (Tile tile : tiles) {
            board[(int)tile.getX()][(int)tile.getY()] = tile;
        }
        return maxValue;
    }

    static Tile[][] clone(Tile[][] arr) {
        Tile[][] out = new Tile[arr.length][];
        for (int i = 0; i < arr.length; i++) {
            out[i] = arr[i].clone();
        }
        return out;
    }

    static boolean outside(int x, int y, Object[][] arr) {
        return (x < 0 ||
                x >= arr.length ||
                y < 0 ||
                y >= arr[x].length
        );
    }
}
