package Dao;

import Model.Card;
import Model.GroupTransaction;

import java.util.List;
import java.util.Map;

public interface TransactionDao {
    List<Card> getCardsUsedByStudent(int studentId) throws DaoException;
    void markQuestCompletedByStudent(String questTitle, int studentId) throws DaoException;
    void addTransaction(int studentID, String title) throws DaoException;
    void addGroupTransaction(int studentID, String title, int donation) throws DaoException;
    Map getGroupTransactions() throws DaoException;
    List<Integer> getDonatorsId(String title) throws DaoException;
    List<Card> questsComplitedByStudent(int studentID) throws DaoException;
    List<GroupTransaction> getGroupTransactionsByIdAndTitle (int studentID, String title) throws  DaoException;
    void archivedGroupTransaction(int studentID, String cardTitle, int donation) throws DaoException;
    void deleteComplitedContribution(String title) throws DaoException;

}
