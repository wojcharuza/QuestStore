package Dao;

import Model.Card;
import Model.Level;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CardDaoImpl implements CardDao{





    public List<Card> getQuests() {
        List<Card> cards = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"Cards\" WHERE card_type::text LIKE 'quest%'")){
            prepareCard(cards, rs);
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }



    public List<Card> getArtifacts() {
        List<Card> cards = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"Cards\" WHERE card_type::text LIKE 'artifact%'")){
            prepareCard(cards, rs);
            stmt.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }



    private void prepareCard(List<Card> cards, ResultSet rs) throws SQLException {
        while (rs.next()) {
            String title = rs.getString("title");
            String description = rs.getString("description");
            String imagePath = rs.getString("image_path");
            String cardType = rs.getString("card_type");
            int coolcoinValue = rs.getInt("coolcoin_value");

            Card card = new Card.Builder().withTitle(title).withDescription(description).
                    withImagePath(imagePath).withCardType(cardType).withCoolcoinValue(coolcoinValue).build();

            cards.add(card);

        }
    }



    public void addCard(String title, String description, String imagePath, String cardType, int coolcoinValue) {
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT INTO " +
                    "Cards(title, description, imagePath, cardType, coolcoinVale) " +
                    "VALUES (?, ?, ?, ?, ?)");

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, imagePath);
            stmt.setString(4, cardType);
            stmt.setInt(5, coolcoinValue);
            stmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }






    public void editQuest() {

    }

    public void editArtifact() {

    }
}
