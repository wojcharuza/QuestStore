package Dao;

import Model.Classroom;
import com.mchange.v2.sql.SqlUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClassroomDaoImpl implements ClassroomDao {

    public List<Classroom> getClassrooms() {
        List<Classroom> classrooms = new ArrayList<>();
        try (Connection con = C3P0DataSource.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM \"Classes\"");) {
            while (rs.next()) {
                int id = rs.getInt("id");
                LocalDate startDate = (rs.getDate("start_date")).toLocalDate();
                int mentorId = rs.getInt("mentor_id");
                Classroom classroom = new Classroom.Builder().withId(id).withMentorId(mentorId).withStartDate(startDate).build();
                classrooms.add(classroom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return classrooms;
    }


    public void deleteClassRoom(int id) {
        try (Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement statement = null;
            statement = con.prepareStatement("DELETE FROM \"Classes\" WHERE id = ?");
            statement.setInt(1,id);
            statement.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

}




