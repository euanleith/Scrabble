package sample;

import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.FileInputStream;

import static sample.Dictionary.getValue;
import static sample.TileUtils.IMG_SIZE;


class Tile {
    private ImageView img;
    private String text;
    private int val;

    Tile(String text) {
        img = new ImageView();
        img.setFitWidth(IMG_SIZE);
        img.setFitHeight(IMG_SIZE);

        set(text);
        this.val = getValue(text);
    }

    ImageView getImg() {
        return img;
    }

    String getText() {
        return text;
    }

    /**
     * Sets the text, image, and value
     * @param text todo
     */
    void set(String text) {
        setImg(text);
        this.text = text;
        val = getValue(text);
    }

    private void setImg(String text) {
        try {
            Image image = new Image(new FileInputStream("src/sample/imgs/" + text + ".jpg"));
            img.setImage(image);
        } catch (Exception e) {e.printStackTrace();}
    }

    void setEvent(EventHandler<? super MouseEvent> event) {
        img.setOnMouseClicked(event);
    }

    double getX() {
        return img.getLayoutX();
    }

    double getY() {
        return img.getLayoutY();
    }

    @Override
    public String toString() {
        return "text: " + text + " val: " + val;
    }
}
