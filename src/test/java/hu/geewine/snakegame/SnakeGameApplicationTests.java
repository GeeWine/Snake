package hu.geewine.snakegame;

import hu.geewine.snakegame.controller.PlayingGroundController;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
class SnakeGameApplicationTests {

    static  PlayingGroundController pgc;


    @BeforeAll
    static void init() {
//        pgc = new PlayingGroundController();
    }

    @Test
    void testAppleMove() {
//        while (true) {
////            pgc.newApple();
////            pgc.getApple().getBoundsInParent();
//            double randomX = new Random().nextInt((int) 400);
//            double randomY = new Random().nextInt((int) 400);
//            randomX = Math.floor(randomX / 20) * 20;
//            randomY = Math.floor(randomY / 20) * 20;
//            System.out.println();
//        }
    }

}
