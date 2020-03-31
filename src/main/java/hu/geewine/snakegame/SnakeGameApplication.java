package hu.geewine.snakegame;

import hu.geewine.snakegame.controller.PlayingGroundController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Root class of Snake game.
 *
 * @author GeeWine
 */
@SpringBootApplication
public class SnakeGameApplication extends Application {

    private final Integer DEFAULT_WIDTH = 800;
    private final Integer DEFAULT_HEIGHT = 600;

    private ConfigurableApplicationContext context;

    private Parent rootNode;
    private double playGroundWidth;
    private double playGroundHeight;

    private static PropertiesFactoryBean mainProperties;

    @Override
    public void init() throws Exception {

        SpringApplicationBuilder builder = new SpringApplicationBuilder(SnakeGameApplication.class);

        context = builder.run(getParameters().getRaw().toArray(new String[0]));

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));

        loader.setResources(ResourceBundle.getBundle("main", Locale.getDefault()));

        loader.setControllerFactory(context::getBean);

        rootNode = loader.load();
    }

    @Override
    public void start(Stage primaryStage) {
        playGroundWidth = DEFAULT_WIDTH;
        playGroundHeight = DEFAULT_HEIGHT;

        primaryStage.setScene(new Scene(rootNode, playGroundWidth, playGroundHeight));

        primaryStage.centerOnScreen();

        primaryStage.show();

        PlayingGroundController playingGroundController = (PlayingGroundController) context.getBean("playingGroundController");
        playingGroundController.initGame();
    }

    /**
     * Properties file access bean to the main controller's Properties file.
     *
     * @return PropertiesFactoryBean
     */
    @Bean
    public PropertiesFactoryBean mainProperties() {
        PropertiesFactoryBean res = new PropertiesFactoryBean();
        res.setFileEncoding("UTF-8");
        res.setLocation(new ClassPathResource("/main.properties"));
        mainProperties = res;
        return res;
    }

    /**
     * Access by key for the main controller's Properties file.
     *
     * @return PropertiesFactoryBean
     */
    public static String getMainProperty(String key) {
        try {
            return mainProperties.getObject().getProperty(key);
        } catch (IOException e) {
            System.out.println("no such a key: " + e);
            return "no such a key";
        }
    }

}
