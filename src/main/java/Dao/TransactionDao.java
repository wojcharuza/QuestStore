package Dao;

import Model.Card;
import java.util.List;

public interface TransactionDao {
    List<Card> getCardsUsedByStudent(int studentId) throws DaoException;
    void markQuestCompletedByStudent(String questTitle, int studentId) throws DaoException;
}
