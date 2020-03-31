package hu.geewine.snakegame.model;

import javax.persistence.*;

/**
 * Entity class of table SNAKE_USER.
 *
 * @author GeeWine
 */
@Entity
@NamedQuery(name = "SnakeUser.findBestTen", query = "SELECT u FROM SnakeUser u ORDER BY u.record DESC")
public class SnakeUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Long record;

    public SnakeUser() {
    }

    public SnakeUser(String name) {
        this.name = name;
    }

    public SnakeUser(String name, Long record) {
        this.name = name;
        this.record = record;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getRecord() {
        return record;
    }

    public void setRecord(Long record) {
        this.record = record;
    }

}
