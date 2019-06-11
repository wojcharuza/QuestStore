package Dao;

import Model.Classroom;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Comparator.comparing;

public class ClassroomDaoImpl implements ClassroomDao {

    private MentorDao mentorDao = new MentorDaoImpl();

    public List<Classroom> getClassrooms() throws DaoException {
        List<Classroom> classrooms = new ArrayList<>();
        try (Connection con = C3P0DataSource.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"Classes\"");) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String startDate = rs.getString("start_date");
                int mentorId = rs.getInt("mentor_id");
                Classroom classroom = new Classroom.Builder().withId(id).withMentorId(mentorId).withStartDate(startDate).withName(mentorDao.getMentorNameById(mentorId)).build();
                classrooms.add(classroom);
            }
            stmt.close();
            classrooms.sort(comparing(Classroom::getId));
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

    public void setMentorIdAsNull(int id) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("UPDATE \"Classes\" SET mentor_id = null WHERE mentor_id = ?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignNewMentor(int classId, int mentorId) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("UPDATE \"Classes\" SET mentor_id = ? WHERE id = ?");
            stmt.setInt(1, mentorId);
            stmt.setInt(2, classId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}




