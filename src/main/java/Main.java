


import Dao.*;
import Model.Admin;

public class Main {



    public static void main(String[] args) {
        CardDao cardDao = new CardDaoImpl();
//        cardDao.addCard("lol", "lol2", "lol3", "artifact_basic", 5);
        cardDao.editCard("lol", "nowy", "opis nowego", "ss", "artifact_basic", -100);


        LoginDaoImpl loginDaoImpl = new LoginDaoImpl();

        //loginDaoImpl.getUser("halo@halo","halo");
//        Mentor mentro = loginDaoImpl.getMentor("halo@halo","halo");
//        System.out.println(mentro.getFirstName() + mentro.getLastName());
//

    }
}
