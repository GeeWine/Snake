package hu.geewine.snakegame.controller;

import hu.geewine.snakegame.SnakeGameApplication;
import hu.geewine.snakegame.graphics.PlayGraphics;
import hu.geewine.snakegame.graphics.model.Segment;
import hu.geewine.snakegame.graphics.model.Snake;
import hu.geewine.snakegame.graphics.util.SegmentDirection;
import hu.geewine.snakegame.graphics.util.SegmentState;
import hu.geewine.snakegame.model.SnakeUser;
import hu.geewine.snakegame.repository.SnakeUserRepository;
import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Main controller of Snake game.
 *
 * @author GeeWine
 */
@Component
public class PlayingGroundController {

    private final Integer SIZE = 20;

    private SnakeUserRepository snakeUserRepository;

    @FXML
    public AnchorPane playingGround;

    @FXML
    public Spinner<Double> playGroundWidth;
    @FXML
    public Spinner<Double> playGroundHeight;

    @FXML
    public Label lifesLabel;
    @FXML
    public Label pointsLabel;
    @FXML
    public Label recordLabel;

    @FXML
    public TableView<SnakeUser> recordTable;

    public Canvas playingGrid;
    double canvasWidth;
    double canvasHeight;
    double canvasTopLeftX;
    double canvasTopLeftY;

    private GraphicsContext graphicsContext;
    private Group drawRoot;
    private Group snakeRoot;
    private PlayGraphics playingGraphics;
    private Scene scene;

    private AnimationTimer timer;

    private Snake snake;
    private Segment apple;

    private int lifes;
    private Long points;

    private boolean directonChangedInCurrentMove;

    private ObservableList<SnakeUser> bestTen;

    public PlayingGroundController(SnakeUserRepository snakeUserRepository) {
        this.snakeUserRepository = snakeUserRepository;
    }

    @FXML
    public void initialize() {
        initPlayGroundBoundsSpinners();

        this.playingGrid = new Canvas(this.playGroundWidth.getValueFactory().valueProperty().getValue(), this.playGroundHeight.getValueFactory().valueProperty().getValue());
        this.playingGrid.widthProperty().bind(this.playGroundWidth.getValueFactory().valueProperty());
        this.playingGrid.heightProperty().bind(this.playGroundHeight.getValueFactory().valueProperty());

        this.graphicsContext = playingGrid.getGraphicsContext2D();
        this.drawRoot = new Group();
        this.drawRoot.getChildren().add(playingGrid);
        this.playingGround.getChildren().add(drawRoot);
        this.snakeRoot = new Group();
        this.playingGround.getChildren().add(snakeRoot);
        this.playingGraphics = new PlayGraphics(drawRoot, SIZE);
    }

    private void initPlayGroundBoundsSpinners() {
        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactoryWidth = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 200);
        valueFactoryWidth.setValue(40d);
        SpinnerValueFactory.DoubleSpinnerValueFactory valueFactoryHeight = new SpinnerValueFactory.DoubleSpinnerValueFactory(0, 200);
        valueFactoryHeight.setValue(40d);
        this.playGroundWidth.setValueFactory(valueFactoryWidth);
        this.playGroundHeight.setValueFactory(valueFactoryHeight);
    }

    /**
     * Initializations after the components are instanciated.
     */
    public void initGame() {
        scene = playingGround.getScene();
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (directonChangedInCurrentMove) return;
                if (event.getCode().isArrowKey()) startGame();
                switch (event.getCode()) {
                    case UP:
                        snake.setDirection(0, -SIZE, SegmentDirection.UP);
                        break;
                    case DOWN:
                        snake.setDirection(0, SIZE, SegmentDirection.DOWN);
                        break;
                    case LEFT:
                        snake.setDirection(-SIZE, 0, SegmentDirection.LEFT);
                        break;
                    case RIGHT:
                        snake.setDirection(SIZE, 0, SegmentDirection.RIGHT);
                        break;
                }
                directonChangedInCurrentMove = true;
            }
        });

        initRecordsTable();
        setRecordLabelText(bestTen.size() > 0 ? bestTen.get(0).getRecord() : 0L);
    }

    private void setRecordLabelText(Long record) {
        recordLabel.setText(String.valueOf(record.intValue()));
    }

    private void initRecordsTable() {
        bestTen = FXCollections.observableArrayList();
        refreshRecords();

        TableColumn<SnakeUser, String> column1 = new TableColumn<>(SnakeGameApplication.getMainProperty("name"));
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SnakeUser, Long> column2 = new TableColumn<>(SnakeGameApplication.getMainProperty("points"));
        column2.setCellValueFactory(new PropertyValueFactory<>("record"));

        recordTable.getColumns().add(column1);
        recordTable.getColumns().add(column2);

        recordTable.setItems(bestTen);
    }

    private void refreshRecords() {
        bestTen.clear();
        bestTen.addAll(getTopTenRecords());
    }

    /**
     * Reading the users from DB.
     *
     * @return List<SnakeUser> Only ten records are sent back.
     */
    private List<SnakeUser> getTopTenRecords() {
        return snakeUserRepository.findBestTen().stream().limit(10).collect(Collectors.toList());
    }

    /**
     *Setting playing field and game parameters.
     *
     * @param actionEvent Start button pressed.
     */
    public void setGameOn(ActionEvent actionEvent) {
        preSetGame();

        if (!drawField()) {
            return;
        }

        newSnake();

        newApple();
    }

    /**
     * After the first arrow key press after start button pressed.
     * Main game engine.
     */
    private void startGame() {
        if (timer == null) {
            timer = new AnimationTimer() {
                private long lastUpdate = 0;
                boolean snakeAlive = true;

                @SneakyThrows
                @Override
                public void handle(long now) {
                    if (!snakeAlive && now - lastUpdate >= 2_000_000_000) {
                        lastUpdate = now;
                        snakeAlive = true;
                        newSnake();
                    }
                    if (snakeAlive && now - lastUpdate >= 1_000_000_000 / snake.getSpeed()) {
                        lastUpdate = now;

                        if (playingGraphics.appleCollisionDetected(apple, snake.getHead())) {
                            moveApple();
                            snake.moveWithSwallow();
                            points++;
                            if (points > 0 && points % 3 == 0) snake.setSpeed(snake.getSpeed() + 0.1);
                            pointsLabel.setText(String.valueOf(points));
                        } else {
                            if (playingGraphics.wallOrOwnCollisionDetected(snake.getSegments())) {
                                snake.moveToDeath();
                                lifes--;
                                lifesLabel.setText(String.valueOf(lifes));
                                snakeAlive = false;
                                if (lifes == 0) {
                                    timer.stop();
                                    manageUserPoints();
                                }
                            } else {
                                snake.move();
                            }
                        }

                        directonChangedInCurrentMove = false;
                    }
                }

            };
        }
        timer.start();
    }

    /**
     * At the end of the game.
     */
    private void manageUserPoints() {
        String yourNamePlease = SnakeGameApplication.getMainProperty("your.name.please");
        TextInputDialog td = new TextInputDialog(yourNamePlease);
        td.setTitle("");
        td.setHeaderText(SnakeGameApplication.getMainProperty("game.over"));
        td.setContentText(SnakeGameApplication.getMainProperty("your.points") + points);
        td.show();

        td.setOnHidden(t -> {
            String name = td.getEditor().getText();

            if (!(name == null || name.isEmpty() || name.equals(yourNamePlease))) {
                List<SnakeUser> users = snakeUserRepository.findAll();

                SnakeUser user = users.stream().filter(u -> u.getName().equals(name)).findAny().orElse(null);

                if (user == null) {
                    user = new SnakeUser(name, points);
                    if (bestTen.size() == 0) {
                        alertRecord(user);
                    }
                } else {
                    if (user.getRecord() < points) {
                        alertRecord(user);
                    }
                }
                snakeUserRepository.save(user);
                refreshRecords();
            }
        });
    }

    private void alertRecord(SnakeUser user) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(SnakeGameApplication.getMainProperty("it.is.your.record"));
        alert.setHeaderText(SnakeGameApplication.getMainProperty("congratulations"));

        if (bestTen.size() == 0 || bestTen.get(0).getRecord()  < points) {
            alert.setContentText(SnakeGameApplication.getMainProperty("it.is.overall.record"));
            setRecordLabelText(points);
        } else {
            alert.setContentText(SnakeGameApplication.getMainProperty("you.made.your.record"));
        }

        alert.showAndWait();

        user.setRecord(points);
    }

    private void preSetGame() {
        directonChangedInCurrentMove = false;
        lifes = 5;
        lifesLabel.setText(String.valueOf(lifes));
        points = 0L;
        pointsLabel.setText(String.valueOf(points));
        playingGround.setFocusTraversable(true);
    }

    private void newSnake() {
        snakeRoot.getChildren().clear();
        snake = new Snake(
                snakeRoot,
                SIZE,
                getRandomX(), getRandomY(),
                2, 0, -SIZE,
                SegmentDirection.UP
        );
//        snake.createTestSnakeBody();
    }

    public void newApple() {
        apple = new Segment(SegmentState.APPLE, SIZE,getRandomX(), getRandomY());
        drawRoot.getChildren().add(apple);
    }

    private void moveApple() {
        apple.setX(getRandomX());
        apple.setY(getRandomY());
    }

    /**
     * Getting new valid coordinate Y inside the playing ground.
     * @return double
     */
    private double getRandomY() {
        double randomY = new Random().nextInt((int) canvasHeight);
        randomY = Math.floor(randomY / SIZE) *SIZE;
        return canvasTopLeftY + randomY;
    }

    /**
     * Getting new valid coordinate X inside the playing ground.
     * @return double
     */
    private double getRandomX() {
        double randomX = new Random().nextInt((int) canvasWidth);
        randomX = Math.floor(randomX / SIZE) * SIZE;
        return canvasTopLeftX + randomX;
    }

    private boolean drawField() {
        this.drawRoot.getChildren().removeAll(this.drawRoot.getChildren());
        canvasWidth = Math.round(this.playingGrid.getWidth() * SIZE / 2);
        canvasHeight = Math.round(this.playingGrid.getHeight() * SIZE / 2);
        canvasTopLeftX = Math.round(this.playingGround.getWidth() - canvasWidth) / 2;
        canvasTopLeftY = Math.round(this.playingGround.getHeight() - canvasHeight) / 2;

        if (canvasHeight > playingGround.getHeight() || canvasWidth > playingGround.getWidth()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(SnakeGameApplication.getMainProperty("playground.error"));
            alert.setHeaderText(SnakeGameApplication.getMainProperty("playground.size.wrong"));
            alert.setContentText(SnakeGameApplication.getMainProperty("reconsider.width.height"));

            alert.showAndWait();

            return false;
        }

        playingGraphics.drawFieldBounderies(canvasTopLeftX - 2, canvasTopLeftY - 2, canvasWidth + 4, canvasHeight + 4);
        playingGraphics.drawField(canvasTopLeftX, canvasTopLeftY, canvasWidth, canvasHeight);

        return true;
    }

    public void endGame(ActionEvent actionEvent) {
        if (timer != null) {
            timer.stop();
        }
    }

}
