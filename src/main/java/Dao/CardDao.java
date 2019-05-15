package main.java.Dao;

import main.java.Model.Card;

import java.util.List;

public interface CardDao {

    List<Card> getQuests();
    List<Card> getArtifacts();
    void addQuest();
    void addArtifact();
    void editQuest();
    void editArtifact();
}
