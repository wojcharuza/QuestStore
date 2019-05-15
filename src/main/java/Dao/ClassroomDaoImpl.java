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
                Date date = rs.getDate("start_date");
                LocalDate startDate = ((java.sql.Date) date).toLocalDate();
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
        try (Connection con = C3P0DataSource.getInstance().getConnection();
            PreparedStatement ps = createPrepStmntForDeletion(con, id);
            ps.executeUpdate()){
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    private PreparedStatement createPrepStmntForDeletion(Connection con, int id) throws SQLException {
        String sql = "DELETE FROM \"Classes\" WHERE id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        return ps;
    }
}




