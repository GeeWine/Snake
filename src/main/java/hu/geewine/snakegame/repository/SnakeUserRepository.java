package hu.geewine.snakegame.repository;

import hu.geewine.snakegame.model.SnakeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SnakeUserRepository extends JpaRepository<SnakeUser, Long> {
    List<SnakeUser> findBestTen();
}
