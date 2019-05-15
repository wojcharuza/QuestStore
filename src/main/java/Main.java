package main.java;

import main.java.Dao.MentorDaoImpl;

public class Main {



    public static void main(String[] args) {
        MentorDaoImpl mentorDao = new MentorDaoImpl();
        mentorDao.addMentor("Zibon", "Nob", "zibon@nob.gg", "jestemnobem");
    }

}
