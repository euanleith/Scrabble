package sample;

import javafx.scene.image.ImageView;

import java.util.ArrayList;

abstract class TileUtils {
    static final int IMG_SIZE = 35;

    static ImageView[] getImgs(ArrayList<Tile> tiles) {
        ImageView[] rackImgs = new ImageView[tiles.size()];
        for (int i = 0; i < tiles.size(); i++) {
            rackImgs[i] = tiles.get(i).getImg();
        }
        return rackImgs;
    }
}
