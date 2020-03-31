package hu.geewine.snakegame.graphics.util;

/**
 * Enum connects the snake segment's relative direction to each other to the proper image file.
 */
public enum SegmentState {
    HEAD_UP("/SnakeGraphics/headUp.png"),
    HEAD_RIGHT("/SnakeGraphics/headRight.png"),
    HEAD_DOWN("/SnakeGraphics/headDown.png"),
    HEAD_LEFT("/SnakeGraphics/headLeft.png"),
    BODY_VERTICAL("/SnakeGraphics/bodyV.png"),
    BODY_HORIZONTAL("/SnakeGraphics/bodyH.png"),
    BODY_ARC_UL("/SnakeGraphics/bodyArcUL.png"),
    BODY_ARC_UR("/SnakeGraphics/bodyArcUR.png"),
    BODY_ARC_BR("/SnakeGraphics/bodyArcBR.png"),
    BODY_ARC_BL("/SnakeGraphics/bodyArcBL.png"),
    TAIL_UP("/SnakeGraphics/tailUp.png"),
    TAIL_RIGHT("/SnakeGraphics/tailRight.png"),
    TAIL_DOWN("/SnakeGraphics/tailDown.png"),
    TAIL_LEFT("/SnakeGraphics/tailLeft.png"),
    DEAD_SNAKE_UP("/SnakeGraphics/deadSnakeUp.png"),
    DEAD_SNAKE_RIGHT("/SnakeGraphics/deadSnakeRight.png"),
    DEAD_SNAKE_DOWN("/SnakeGraphics/deadSnakeDown.png"),
    DEAD_SNAKE_LEFT("/SnakeGraphics/deadSnakeLeft.png"),
    APPLE("/SnakeGraphics/apple.png");

    private String value;

    SegmentState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
