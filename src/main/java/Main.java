


import Dao.*;
import Model.Level;

import java.util.ArrayList;
import java.util.List;

public class Main {



    public static void main(String[] args) {
        CardDao cardDao = new CardDaoImpl();
//        cardDao.addCard("lol", "lol2", "lol3", "artifact_basic", 5);
        cardDao.editCard("lol", "nowy", "opis nowego", "ss", "artifact_basic", -100);


    }

}
