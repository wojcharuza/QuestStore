package Dao;

import Model.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CardDaoImpl implements CardDao{

    public List<Card> getQuests() throws DaoException {
        List<Card> cards = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"Cards\" WHERE card_type::text LIKE 'quest%'")){
            prepareCard(cards, rs);
            stmt.close();
            rs.close();
            return cards;

        } catch (SQLException e) {
            throw new DaoException();
        }
    }



    public List<Card> getArtifacts() throws DaoException {
        List<Card> cards = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM \"Cards\" WHERE card_type::text LIKE 'artifact%'")){
            prepareCard(cards, rs);
            stmt.close();
            rs.close();
            return cards;

        } catch (SQLException e) {
            throw new DaoException();
        }
    }


    private void prepareCard(List<Card> cards, ResultSet rs) throws SQLException {
        while (rs.next()) {
            String title = rs.getString("title");
            String description = rs.getString("description");
            String imagePath = rs.getString("image_path");
            String cardType = rs.getString("card_type");
            int coolcoinValue = rs.getInt("coolcoin_value");
            System.exit(0);

            Card card = new Card.Builder().withTitle(title).withDescription(description).
                    withImagePath(imagePath).withCardType(cardType).withCoolcoinValue(coolcoinValue).build();
            cards.add(card);
        }
    }



    public void addCard(String title, String description, String imagePath, String cardType, int coolcoinValue) throws DaoException {
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT INTO " +
                    "\"Cards\"(title, description, image_Path, card_type, coolcoin_value) " +
                    "VALUES (?, ?, ?, CAST(? AS card_type), ?)");

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, imagePath);
            stmt.setString(4, cardType);
            stmt.setInt(5, coolcoinValue);
            stmt.executeUpdate();
        }
        catch (SQLException e){
            throw new DaoException();
        }
    }



    public void editCard(String oldTitle, String newTitle,
                          String description, String imagePath,
                          String cardType, int coolcoinValue) throws DaoException {

        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt;
            stmt = con.prepareStatement("UPDATE \"Cards\" SET title = ?, " +
                    "description = ?, image_path = ?, card_type = CAST(? AS card_type), coolcoin_value = ? WHERE title = ?");
            stmt.setString(1, newTitle);
            stmt.setString(2, description);
            stmt.setString(3, imagePath);
            stmt.setString(4, cardType);
            stmt.setInt(5, coolcoinValue);
            stmt.setString(6, oldTitle);
            stmt.executeUpdate();
            stmt.close();
        }
        catch (SQLException e){
            throw new DaoException();
        }
    }


}
