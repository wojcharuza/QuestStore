


import Dao.*;
import Model.Admin;
import Model.Card;
import Service.MentorService;

import java.util.List;

public class Main {



    public static void main(String[] args) {
        CardDao cardDao = new CardDaoImpl();
        try{
        List<Card> cards = cardDao.getArtifacts();
        for (Card card: cards){
            System.out.println(card.getTitle());
        }

        }
        catch (DaoException e){
            e.printStackTrace();
        }

    }
}
