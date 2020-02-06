package sample;

import javafx.scene.image.Image;

import java.io.FileInputStream;

class BoardTile extends Tile {
    BoardTile(String type) {
        try {
            Image image = new Image(new FileInputStream("src/sample/imgs/board tile "+type+".jpg"));
            img.setImage(image);
        } catch (Exception e) {e.printStackTrace();}
    }
}
