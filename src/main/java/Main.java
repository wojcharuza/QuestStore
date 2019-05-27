


import Dao.*;
import Model.Admin;
import Model.Card;
import Service.MentorService;

import java.util.List;

public class Main {



    public static void main(String[] args) {
        CardDao cardDao = new CardDaoImpl();
        MentorService mentorService = new MentorService();




        try{
        List<Card> cards = cardDao.getArtifacts();
        Card firstCard = cards.get(2);
            System.out.println("Card title: " + firstCard.getTitle() + "\nCard description: " + firstCard.getDescription());


        }
        catch (DaoException e){
            e.printStackTrace();
        }
        mentorService.addNewCard("artifact_basic");


    }
}
