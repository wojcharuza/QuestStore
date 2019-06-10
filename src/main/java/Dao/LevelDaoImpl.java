package Dao;

import Model.Level;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LevelDaoImpl implements LevelDao {



    public List<Level> getLevels() throws DaoException {
        List<Level> levels = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"Levels\"")){

            while (rs.next()) {
                int levelNumber = rs.getInt("id");
                int experienceNeeded = rs.getInt("experience");
                Level level = new Level.Builder().withLevelNumber(levelNumber).withExperienceNeeded(experienceNeeded).build();
                levels.add(level);
            }
            System.out.println(levels.get(1).getLevelNumber() + "level number in dao");
            return levels;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }



    public void editLevels(List<Level> levels) throws DaoException {
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            for(Level level: levels){
                int levelNumber = level.getLevelNumber();
                int experienceNeeded = level.getExperienceNeeded();
                PreparedStatement stmt = null;
                stmt = con.prepareStatement("UPDATE \"Levels\" SET experience = ? WHERE id = ?");
                stmt.setInt(1, experienceNeeded);
                stmt.setInt(2, levelNumber);
                stmt.executeUpdate();
                stmt.close();
            }
        }
        catch (SQLException e){
            throw new DaoException();
        }
    }




}
