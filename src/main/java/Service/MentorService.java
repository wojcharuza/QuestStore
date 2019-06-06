package Service;

import Dao.CardDaoImpl;
import Dao.DaoException;
import Dao.StudentDaoImpl;
import Model.Card;
import Model.Student;
import View.View;

import java.util.ArrayList;
import java.util.List;

public class MentorService {

    View view = new View();
    StudentDaoImpl studentDao = new StudentDaoImpl();
    CardDaoImpl cardDao = new CardDaoImpl();

    public void addNewQuest (){
        String cardType = new String();
        view.showMessage("enter card type:"+
        "\n 1. quest_basic"+
        "\n 2. quest_rare");
        int questChoose = view.getIntegerInput();
        if (questChoose == 1){
            cardType = "quest_basic";

        } else if(questChoose == 2) {
            cardType = "quest_rare";

        } else {
            view.showMessage("type correct answer");
            addNewQuest();
        }
        addNewCard(cardType);

    }

    public void addNewArtifact (){
        String cardType = new String();
        view.showMessage("enter card type:"+
                "\n 1. artifact_basic"+
                "\n 2. artifact_rare");
        int questChoose = view.getIntegerInput();
        if (questChoose == 1){
            cardType = "artifact_basic";

        } else if(questChoose == 2) {
            cardType = "artifact_rare";

        } else {
            view.showMessage("type correct answer");
            addNewQuest();
        }


    }
    public void addNewCard(String cardType){



        view.showMessage("Enter Quest title");
        String title = view.getStringInput();

        view.showMessage("Enter Quest description");
        String description = view.getStringInput();

        view.showMessage("Enter image path");
        String imagePath = view.getStringInput();

        view.showMessage("Enter coolcoin value");
        int coolcoinValue = view.getIntegerInput();
        try{
            cardDao.addCard(title,description,imagePath,cardType,coolcoinValue);
        } catch (DaoException e){
            e.printStackTrace();
        }
    }
    public void editCard(){
        CardDaoImpl cardDao = new CardDaoImpl();

        view.showMessage("Enter title of card you want to edit");
        String oldTitle = view.getStringInput();

        view.showMessage("Enter new  title");
        String title = view.getStringInput();

        view.showMessage("Enter quest type");
        String cardType = view.getStringInput();

        view.showMessage("Enter Quest description");
        String description = view.getStringInput();

        view.showMessage("Enter image path");
        String imagePath = view.getStringInput();

        view.showMessage("Enter coolcoin value");
        int coolcoinValue = view.getIntegerInput();
        try{
            cardDao.editCard(oldTitle,title,description,imagePath,cardType,coolcoinValue);
        } catch (DaoException e){
            e.printStackTrace();
        }
    }
    public List<Student> getStudents(){

        List<Student> students = new ArrayList<>();

        try {
            List<Student> studentsFromDao = studentDao.getAllStudents();
            for (Student s: studentsFromDao){
                students.add(s);
            }

        }catch (DaoException e) {
            e.printStackTrace();
        }
        return students;

    }
    public void editStudent (){
        view.showMessage("enter id of student which you want to edit");
        int idToEdit = view.getIntegerInput();

        view.showMessage("Enter new  first name");
        String firstName = view.getStringInput();

        view.showMessage("Enter new last name");
        String lastName = view.getStringInput();

        view.showMessage("Enter new email");
        String email = view.getStringInput();

        view.showMessage("Enter new password");
        String password = view.getStringInput();

    }
    public List <Card> getAllArtifacts(){
        List<Card> artfiacts = new ArrayList<>();
        try{
            artfiacts = cardDao.getArtifacts();

        }catch (DaoException e){
            e.printStackTrace();
        }

        return artfiacts;
    }

    public List<Card> grtAllQuests (){
        List<Card> quests = new ArrayList<>();
        try{
            quests = cardDao.getQuests();

        }catch (DaoException e){
            e.printStackTrace();
        }
        return quests;

    }

}
