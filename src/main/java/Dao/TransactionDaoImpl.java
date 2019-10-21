package Dao;

import Model.Card;
import Model.GroupTransaction;
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
            stmt.executeUpdate();

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
            stmt.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    public void archivedGroupTransaction(int studentID, String cardTitle, int donation) throws DaoException{
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("INSERT  INTO  \"archived_group_transactions\"(student_id, card_title, donation)VALUES "+
                    "(?, ?, ?)");
            stmt.setInt(1,studentID);
            stmt.setString(2,cardTitle);
            stmt.setInt(3, donation);
            stmt.executeUpdate();

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
            return transactionMap;

        }catch (SQLException e){
            e.printStackTrace();
            throw new DaoException();
        }

    }


    public List<GroupTransaction> getGroupTransactionsByIdAndTitle (int studentID, String title) throws  DaoException{
        List<GroupTransaction> groupTransactionsList = new ArrayList<>();
        GroupTransaction groupTransaction = new GroupTransaction(title,0);;
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT * " +
                    "FROM \"group_transactions\" WHERE card_title = ? AND student_id = ?");
            stmt.setString(1,title);
            stmt.setInt(2,studentID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                String cardTitle = rs.getString("card_title");
                int donateValue = rs.getInt("donation");
                int sumOfDonateValue = groupTransaction.getDonationValue() + donateValue;
                groupTransaction.setDonationValue(sumOfDonateValue);
            }
            groupTransactionsList.add(groupTransaction);
            System.out.println(groupTransactionsList.size()+"list size in dao");
            return groupTransactionsList;

        }catch (SQLException e){
            e.printStackTrace();
            throw new DaoException();
        }
    }


    public List<Integer> getDonatorsId (String title) throws DaoException{
        List<Integer> donatorsId = new ArrayList<>();
        try(Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("SELECT DISTINCT student_id " +
                    "FROM \"group_transactions\" WHERE card_title = ?;");
            stmt.setString(1,title);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()){

                int donator = rs.getInt("student_id");
                donatorsId.add(donator);
            }
            return donatorsId;

        }catch (SQLException e){
            e.printStackTrace();
            throw new DaoException();
        }
    }


    public void deleteComplitedContribution(String title) throws DaoException{
        try (Connection con = C3P0DataSource.getInstance().getConnection()){
            PreparedStatement statement = null;
            statement = con.prepareStatement("DELETE FROM group_transactions WHERE card_title = ?");
            statement.setString(1,title);
            statement.executeUpdate();
            statement.close();
        }
        catch (SQLException e){
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


    public void deleteTransactionsByIds(List<Integer> studentIds){
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            Integer[] arrayOfIds = studentIds.toArray(new Integer[studentIds.size()]);
            Array sqlArray = con.createArrayOf("int4", arrayOfIds);
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("DELETE FROM \"Transactions\" WHERE student_id = ANY(?)");
            stmt.setArray(1, sqlArray);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void deleteTransactionsById(int studentId){
        try (Connection con = C3P0DataSource.getInstance().getConnection()) {
            PreparedStatement stmt = null;
            stmt = con.prepareStatement("DELETE FROM \"Transactions\" WHERE student_id = ?");
            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}



