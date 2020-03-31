package hu.geewine.snakegame.graphics.model;

import hu.geewine.snakegame.graphics.util.SegmentState;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

/**
 * A segment of model Snake.
 *
 * @author GeeWine
 */
public class Segment extends Rectangle {

    public Segment(SegmentState state, double size, double x, double y) {
        super();
        this.setX(x);
        this.setY(y);
        this.setWidth(size);
        this.setHeight(size);
        setImage(state.getValue());
    }

    public void setImage(String imageUrl) {
        Image img = new Image(imageUrl);
        this.setFill(new ImagePattern(img));
    }

}
