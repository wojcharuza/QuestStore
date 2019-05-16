


import Dao.*;
import Model.Level;

import java.util.ArrayList;
import java.util.List;

public class Main {



    public static void main(String[] args) {
        CardDao cardDao = new CardDaoImpl();
        cardDao.getArtifacts();
        System.out.println("\n\n\n");
        cardDao.getQuests();

    }

}
