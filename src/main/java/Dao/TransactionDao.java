package Dao;

import Model.Card;
import java.util.List;

public interface TransactionDao {
    List<Card> getCardsUsedByStudent(int studentId) throws DaoException;
    void markQuestCompletedByStudent() throws DaoException;
    void addTransaction(int studentID, String title) throws DaoException;
}
