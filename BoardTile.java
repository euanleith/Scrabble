package sample;

import javafx.scene.image.Image;

import java.io.FileInputStream;

class BoardTile extends Tile {
    private String type;

    BoardTile(String type) {
        this.type = type;
        try {
            Image image = new Image(new FileInputStream("src/sample/imgs/board tile "+type+".jpg"));
            img.setImage(image);
        } catch (Exception e) {e.printStackTrace();}
    }

    @Override
    public String toString() {
        return type + " (" + getX() + ", " + getY() + ")";
    }
}
