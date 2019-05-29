package Dao;

import Model.Classroom;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClassroomDaoImpl implements ClassroomDao {

    private MentorDao mentorDao = new MentorDaoImpl();

    public List<Classroom> getClassrooms() throws DaoException {
        List<Classroom> classrooms = new ArrayList<>();
        try (Connection con = C3P0DataSource.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"Classes\"");) {
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate startDate = (rs.getDate("start_date")).toLocalDate();
                int mentorId = rs.getInt("mentor_id");
                Classroom classroom = new Classroom.Builder().withId(id).withMentorId(mentorId).withStartDate(startDate).withName(mentorDao.getMentorNameById(mentorId)).build();
                classrooms.add(classroom);
            }
            stmt.close();
            return classrooms;
        } catch (SQLException e) {
            throw new DaoException();
        }
    }


    public void deleteClassRoom(int id) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement statement = null;
            statement = con.prepareStatement("DELETE FROM \"Classes\" WHERE id = ?");
            statement.setInt(1,id);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e){
            throw new DaoException();
        }
    }

    @Override
    public void addClassroom(String date, int mentorId) throws DaoException {
            try (Connection con = C3P0DataSource.getInstance().getConnection()) {
                PreparedStatement stmt = null;
                stmt = con.prepareStatement("INSERT INTO \"Classes\"(start_date, mentor_id) VALUES (?, ?)");
                stmt.setString(1, date);
                stmt.setInt(2, mentorId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }


    }
}




