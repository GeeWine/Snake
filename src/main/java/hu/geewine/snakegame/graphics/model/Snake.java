package hu.geewine.snakegame.graphics.model;

import hu.geewine.snakegame.graphics.util.SegmentDirection;
import hu.geewine.snakegame.graphics.util.SegmentState;
import javafx.scene.Group;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Main graphics model of Snake game.
 *
 * @author GeeWine
 */
public class Snake {

    private  Integer size;
    private  Group root;
    private List<Segment> segments;
    private  double speed;
    private  double directionX;
    private  double directionY;
    private SegmentDirection direction;

//    public Snake(Segment segment) {
//        this.segments = new LinkedList<>();
//        this.segments.add(segment);
//    }

//    public Snake(Group snakeRoot, Integer size, double x, double y) {
//        this.root = snakeRoot;
//        this.size = size;
//        Segment segment = new Segment(SegmentState.HEAD_UP, size, x, y);
//        this.segments = new LinkedList<>();
//        this.segments.add(segment);
//        this.root.getChildren().add(segment);
//    }

    public Snake(Group snakeRoot, Integer size, double x, double y, double speed, double dirX, double dirY, SegmentDirection direction) {
        this.root = snakeRoot;
        this.size = size;
        this.direction = direction;
        Segment segment = new Segment(SegmentState.HEAD_UP, size, x, y);
        this.segments = new LinkedList<>();
        this.segments.add(segment);
        this.root.getChildren().add(segment);

        this.speed = speed;
        this.directionX = dirX;
        this.directionY = dirY;
    }

    /**
     * Only for testing purposes, letting the game start with a Snake 5 segments long.
     */
    public void createTestSnakeBody() {
        Segment segment1 = new Segment(SegmentState.BODY_VERTICAL, size, getHead().getX(),getHead().getY() + size);
        Segment segment2 = new Segment(SegmentState.BODY_VERTICAL, size, segment1.getX(), segment1.getY() + size);
        Segment segment3 = new Segment(SegmentState.BODY_VERTICAL, size, segment2.getX(), segment2.getY() + size);
        Segment segment4 = new Segment(SegmentState.TAIL_DOWN, size, segment3.getX(), segment3.getY() + size);
        segments.addAll(Arrays.asList(segment1, segment2, segment3, segment4));
        root.getChildren().addAll(Arrays.asList(segment1, segment2, segment3, segment4));
    }

    /**
     * Sets the direction of the head depending on the pressed arrow key.
     *
     * @param dirX Coordinate X diff.
     * @param dirY Coordinate Y diff.
     * @param direction Direction value.
     */
    public void setDirection(int dirX, int dirY, SegmentDirection direction) {
        Segment head = getHead();
        switch (direction) {
            case UP:
                if (this.direction != SegmentDirection.DOWN) {
                    changeDirection(dirX, dirY, direction, head);
                }
                break;
            case DOWN:
                if (this.direction != SegmentDirection.UP) {
                    changeDirection(dirX, dirY, direction, head);
                }
                break;
            case LEFT:
                if (this.direction != SegmentDirection.RIGHT) {
                    changeDirection(dirX, dirY, direction, head);
                }
                break;
            case RIGHT:
                if (this.direction != SegmentDirection.LEFT) {
                    changeDirection(dirX, dirY, direction, head);
                }
                break;
        }
    }

    private void changeDirection(int dirX, int dirY, SegmentDirection direction, Segment head) {
        this.directionX = dirX;
        this.directionY = dirY;
        this.direction = direction;
    }

    /**
     * A normal step of the snake.
     */
    public void move() {
        for (int i = segments.size() -1; i > 0; i--) {
            Segment currentSegment = segments.get(i);
            Segment beforeSegment = segments.get(i - 1);
            currentSegment.setX(beforeSegment.getX());
            currentSegment.setY(beforeSegment.getY());
        }

        getHead().setX(getHead().getX() + directionX);
        getHead().setY(getHead().getY() + directionY);

        adjustSegmentGraphics();
    }

    /**
     * The last step of the snake.
     * Changing graphics without a real move.
     */
    public void moveToDeath() {
        Segment head = getHead();
        if (direction == SegmentDirection.UP) head.setImage(SegmentState.DEAD_SNAKE_UP.getValue());
        if (direction == SegmentDirection.RIGHT) head.setImage(SegmentState.DEAD_SNAKE_RIGHT.getValue());
        if (direction == SegmentDirection.DOWN) head.setImage(SegmentState.DEAD_SNAKE_DOWN.getValue());
        if (direction == SegmentDirection.LEFT) head.setImage(SegmentState.DEAD_SNAKE_LEFT.getValue());
        head.toFront();
    }

    /**
     * The head goes forward, the rest stays.
     */
    public void moveWithSwallow() {
        Segment newSegmentAfterHead = new Segment(SegmentState.APPLE, size, getHead().getX(), getHead().getY());
        segments.add(1, newSegmentAfterHead);
        root.getChildren().add(newSegmentAfterHead);

        getHead().setX(getHead().getX() + directionX);
        getHead().setY(getHead().getY() + directionY);

        adjustSegmentGraphics();
    }

    private void adjustSegmentGraphics() {
        adjustHeadGraphics();

        if (segments.size() > 2) {
            for (int i = 1; i < segments.size() - 1; i++) {
                adjustBodyGraphics(i);
            }
        }

        if (segments.size() > 1) {
            adjustTailGraphics();
        }
    }

    /**
     * Setting the graphics of the head.
     */
    private void adjustHeadGraphics() {
        Segment head = getHead();
        if (direction == SegmentDirection.UP) head.setImage(SegmentState.HEAD_UP.getValue());
        if (direction == SegmentDirection.RIGHT) head.setImage(SegmentState.HEAD_RIGHT.getValue());
        if (direction == SegmentDirection.DOWN) head.setImage(SegmentState.HEAD_DOWN.getValue());
        if (direction == SegmentDirection.LEFT) head.setImage(SegmentState.HEAD_LEFT.getValue());
    }

    /**
     * Setting the graphics of a body segments depending on the location of the segments before and after.
     */
    private void adjustBodyGraphics(int i) {
        Segment segmentBefore = segments.get(i - 1);
        double segmentBeforeX = segmentBefore.getX();
        double segmentBeforeY = segmentBefore.getY();
        Segment current = segments.get(i);
        double currentX = current.getX();
        double currentY = current.getY();
        Segment segmentAfter = segments.get(i + 1);
        double segmentAfterX = segmentAfter.getX();
        double segmentAfterY = segmentAfter.getY();

        if (segmentBeforeX == segmentAfterX) {
            current.setImage(SegmentState.BODY_VERTICAL.getValue());
        } else if (segmentBeforeY == segmentAfterY) {
            current.setImage(SegmentState.BODY_HORIZONTAL.getValue());
        } else {
            if ((segmentBeforeX < currentX && currentX == segmentAfterX && segmentBeforeY == currentY && currentY < segmentAfterY)
                    || (segmentAfterX < currentX && currentX == segmentBeforeX && segmentAfterY == currentY && currentY < segmentBeforeY)) {
                current.setImage(SegmentState.BODY_ARC_UR.getValue());
            } else if ((segmentBeforeX == currentX && currentX > segmentAfterX && segmentBeforeY < currentY && currentY == segmentAfterY)
                    || (segmentAfterX == currentX && currentX > segmentBeforeX && segmentAfterY < currentY && currentY == segmentBeforeY)) {
                current.setImage(SegmentState.BODY_ARC_BR.getValue());
            } else if ((segmentBeforeX == currentX && currentX < segmentAfterX && segmentBeforeY < currentY && currentY == segmentAfterY)
                    || (segmentAfterX == currentX && currentX < segmentBeforeX && segmentAfterY < currentY && currentY == segmentBeforeY)) {
                current.setImage(SegmentState.BODY_ARC_BL.getValue());
            } else if ((segmentBeforeX > currentX && currentX == segmentAfterX && segmentBeforeY == currentY && currentY < segmentAfterY)
                    || (segmentAfterX > currentX && currentX == segmentBeforeX && segmentAfterY == currentY && currentY < segmentBeforeY)) {
                current.setImage(SegmentState.BODY_ARC_UL.getValue());
            }
        }
    }

    /**
     * Setting the graphics of the tail depending on the location of the segment before.
     */
    private void adjustTailGraphics() {
        Segment segmentBeforeTail = segments.get(segments.size() - 2);
        double segmentBeforeTailX = segmentBeforeTail.getX();
        double segmentBeforeTailY = segmentBeforeTail.getY();
        Segment tail = segments.get(segments.size() - 1);
        double tailX = tail.getX();
        double tailY = tail.getY();
        double diffX = segmentBeforeTailX - tailX;
        double diffY = segmentBeforeTailY - tailY;

        if (diffY < 0) {
            tail.setImage(SegmentState.TAIL_DOWN.getValue());
        } else if (diffY > 0) {
            tail.setImage(SegmentState.TAIL_UP.getValue());
        } else {
            if (diffX < 0) {
                tail.setImage(SegmentState.TAIL_RIGHT.getValue());
            } else {
                tail.setImage(SegmentState.TAIL_LEFT.getValue());
            }
        }
    }


    public Group getRoot() {
        return root;
    }

    public void setRoot(Group root) {
        this.root = root;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDirectionX() {
        return directionX;
    }

    public void setDirectionX(double directionX) {
        this.directionX = directionX;
    }

    public double getDirectionY() {
        return directionY;
    }

    public void setDirectionY(double directionY) {
        this.directionY = directionY;
    }

    public Segment getHead() {
        return segments.get(0);
    }

}
