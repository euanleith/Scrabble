package sample;

import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import static sample.TileUtils.IMG_SIZE;

//todo could probably merge two subclasses back into one again...
class Tile {

    ImageView img;

    Tile() {
        img = new ImageView();
        img.setFitWidth(IMG_SIZE);
        img.setFitHeight(IMG_SIZE);
    }

    ImageView getImg() {
        return img;
    }

    void setEvent(EventHandler<? super MouseEvent> event) {
        img.setOnMouseClicked(event);
    }

    double getX() {
        return GridPane.getColumnIndex(img);
    }

    double getY() {
        return GridPane.getRowIndex(img);
    }

    @Override
    public String toString() {
        return "[] (" + getX() + ", " + getY() + ")";
    }
}
