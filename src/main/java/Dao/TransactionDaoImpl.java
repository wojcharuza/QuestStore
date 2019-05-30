package Dao;

import Model.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDaoImpl implements TransactionDao {


    public List<Card> getCardsUsedByStudent(int studentId) throws DaoException {
        List<Card> usedCards = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT title, description, image_path, card_type, " +
                                            "coolcoin_value FROM \"Transactions\" LEFT JOIN \"Cards\" ON " +
                                            "\"Transactions\".card_title = \"Cards\".title WHERE student_id = ? AND " +
                                            "card_type::text LIKE 'artifact%' ;");
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()){
                String title = rs.getString("title");
                String description = rs.getString("description");
                String imagePath = rs.getString("image_path");
                String card_type = rs.getString("card_type");
                int coolcoinValue = rs.getInt("coolcoin_value");
                Card card = new Card.Builder().withTitle(title).withDescription(description)
                        .withImagePath(imagePath).withCardType(card_type).withCoolcoinValue(coolcoinValue).build();
                usedCards.add(card);
            }

            return usedCards;

        } catch (SQLException e) {
            throw new DaoException();
        }
    }




    public void markQuestCompletedByStudent(String questTitle, int studentId) throws DaoException {
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT INTO \"Transactions\" (student_id, card_title) VALUES (?, ?)");
            stmt.setInt(1, studentId);
            stmt.setString(2, questTitle);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
