package Dao;

import Model.Level;
import java.util.List;

public interface LevelDao {

    List<Level> getLevels() throws DaoException;
    void editLevels(List<Level> levels) throws DaoException;
}
