package Dao;

import Model.Card;

import java.util.List;

public interface TransactionDao {
    List<Card> getCardsUsedByStudent();
    void markQuestCompletedByStudent();
}
