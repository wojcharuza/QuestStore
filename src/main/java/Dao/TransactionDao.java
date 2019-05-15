package main.java.Dao;

import main.java.Model.Card;

import java.util.List;

public interface TransactionDao {
    int getCoolcoinBalance();
    List<Card> getCardsUsedByStudent();
    void markQuestCompletedByStudent();
}
