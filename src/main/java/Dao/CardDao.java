package Dao;

import Model.Card;
import java.util.List;

public interface CardDao {

    List<Card> getQuests() throws DaoException;
    List<Card> getArtifacts() throws DaoException;
    void addCard(String title, String description, String image_path, String cardType, int coolcoinValue) throws DaoException;
    void editCard(String oldTitle, String newTitle,
                  String description, String imagePath,
                  String cardType, int coolcoinValue) throws DaoException;

}
