package sample;

import javafx.scene.image.Image;

import java.io.FileInputStream;

public class UserTile extends Tile {

    private String text;

    UserTile(String text) {
        setData(text);
    }

    /**
     * Sets the text and image based on the given string
     * @param text string which maps to some image and value
     */
    private void setData(String text) {
        setImg(text);
        this.text = text;
    }

    /**
     * Sets the image to that which corresponds to the given string
     * @param text string which maps to some image
     */
    private void setImg(String text) {
        try {
            Image image = new Image(new FileInputStream("src/sample/imgs/" + text + ".jpg"));
            img.setImage(image);
        } catch (Exception e) {e.printStackTrace();}
    }

    String getText() {
        return text;
    }
}
