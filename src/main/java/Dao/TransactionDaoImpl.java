package Dao;

import Model.Card;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addTransaction(int studentID, String cardTitle) throws DaoException{

        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT  INTO  \"Transactions\"(student_id, card_title)VALUES "+
                    "(?, ?)");
            stmt.setInt(1,studentID);
            stmt.setString(2,cardTitle);
            stmt.executeQuery();


            } catch (SQLException e){
                e.printStackTrace();
                throw new DaoException();
        }


    }
    public void addGroupTransaction(int studentID, String cardTitle, int donation) throws DaoException{

        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT  INTO  \"group_transactions\"(student_id, card_title, donation)VALUES "+
                    "(?, ?, ?)");
            stmt.setInt(1,studentID);
            stmt.setString(2,cardTitle);
            stmt.setInt(3, donation);
            stmt.executeQuery();


        } catch (SQLException e){
            e.printStackTrace();
        }
    }
    public Map<String, Integer> getGroupTransactions () throws  DaoException{
        Map<String, Integer> transactionMap = new HashMap<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT card_title, donation " +
                    "FROM \"group_transactions\" ;");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String title = rs.getString("card_title");
                int donateValue = rs.getInt("donation");
                if (transactionMap.containsKey(title)){
                    transactionMap.put(title, transactionMap.get(title)+donateValue);
                }else {
                    transactionMap.put(title, donateValue);
                }
            }
            //System.out.println(transactionMap+"mapa w dao transa");
            return transactionMap;



        }catch (SQLException e){
            e.printStackTrace();
            throw new DaoException();
        }



    }






    public List<Card> questsComplitedByStudent(int studentID) throws DaoException{
        List<Card> usedCards = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT title, description, image_path, card_type, " +
                    "coolcoin_value FROM \"Transactions\" LEFT JOIN \"Cards\" ON " +
                    "\"Transactions\".card_title = \"Cards\".title WHERE student_id = ? AND " +
                    "card_type::text LIKE '%quest%' ;");
            stmt.setInt(1, studentID);
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
