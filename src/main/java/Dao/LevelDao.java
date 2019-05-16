package Dao;

import Model.Level;

import java.util.List;

public interface LevelDao {

    List<Level> getLevels();
    void editLevels(List<Level> levels);
}
