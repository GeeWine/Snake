package hu.geewine.snakegame.graphics;

import hu.geewine.snakegame.graphics.model.Segment;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.List;

/**
 * Graphics manager of Snake game.
 *
 * @author GeeWine
 */
public class PlayGraphics {

    private final Integer size;
    private final Group drawRoot;

    public PlayGraphics(Group drawRoot, Integer size) {
        this.size = size;
        this.drawRoot = drawRoot;
    }

    public void drawFieldBounderies(double x, double y, double canvasWidth, double canvasHeight) {
        Rectangle rectT = new Rectangle(x - 15, y - 15,  canvasWidth + 30, 15);
        rectT.setFill(Color.BLACK);
        rectT.setStrokeWidth(0);
        this.drawRoot.getChildren().add(rectT);
        Rectangle rectR = new Rectangle(x + canvasWidth, y - 14,  15, canvasHeight + 28);
        rectR.setFill(Color.BLACK);
        rectR.setStrokeWidth(0);
        this.drawRoot.getChildren().add(rectR);
        Rectangle rectB = new Rectangle(x - 15, y + canvasHeight,  canvasWidth + 30, 15);
        rectB.setFill(Color.BLACK);
        rectB.setStrokeWidth(0);
        this.drawRoot.getChildren().add(rectB);
        Rectangle rectL = new Rectangle(x - 15, y - 14,  15, canvasHeight + 28);
        rectL.setFill(Color.BLACK);
        rectL.setStrokeWidth(0);
        this.drawRoot.getChildren().add(rectL);
    }

    public void drawField(double x, double y, double canvasWidth, double canvasHeight) {
        Rectangle rect = new Rectangle(x, y,  canvasWidth, canvasHeight);
        rect.setFill(Color.YELLOW);
        rect.setStrokeWidth(0);
        this.drawRoot.getChildren().add(rect);
    }

    public boolean wallOrOwnCollisionDetected(List<Segment> segments) {
        Segment head = segments.get(0);
        if (head.getBoundsInParent().intersects(drawRoot.getChildren().get(0).getBoundsInParent())
                || head.getBoundsInParent().intersects(drawRoot.getChildren().get(1).getBoundsInParent())
                || head.getBoundsInParent().intersects(drawRoot.getChildren().get(2).getBoundsInParent())
                || head.getBoundsInParent().intersects(drawRoot.getChildren().get(3).getBoundsInParent())) {
            return true;
        }
        for (int i = segments.size() -1; i > 0; i--) {
            if (intersects(head.getBoundsInParent(), segments.get(i).getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    public boolean appleCollisionDetected(Segment apple, Segment head) {
        return intersects(apple.getBoundsInParent(), head.getBoundsInParent());
    }

    public boolean intersects(Bounds s1, Bounds s2) {
        if (s1.isEmpty() || s2.isEmpty()
                || s1.getWidth() < 0 || s1.getHeight() < 0 || s1.getDepth() < 0
                || s2.getWidth() < 0 || s2.getHeight() < 0 || s2.getDepth() < 0) return false;
        return (s1.getMinX() + s1.getWidth() > s2.getMinX() &&
                s1.getMinY() + s1.getHeight() > s2.getMinY() &&
                s1.getMinZ() + s1.getDepth() >= s2.getMinZ() &&
                s1.getMinX() < s2.getMaxX() &&
                s1.getMinY() < s2.getMaxY() &&
                s1.getMinZ() <= s2.getMaxZ());
    }

}
