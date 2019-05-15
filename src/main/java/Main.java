package main.java;

import main.java.Dao.MentorDaoImpl;

public class Main {



    public static void main(String[] args) {
        MentorDaoImpl mentorDao = new MentorDaoImpl();
        mentorDao.editMentor(1, "Halo", "Halo?", "halo@halo", "halo");
    }

}
