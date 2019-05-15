package Model;

import java.util.List;

public class Student {
    private String firstName;
    private String lastName;
    private String password;
    private int id;
    private String email;
    private int classID;
    private String imaGePath;
    private int coolcoins;
    private List<Card> usedArtifacts;

    public Student (Builder builder){
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.password = builder.password;
        this.id = builder.id;
        this.email = builder.email;
        this.classID = builder.classID;
        this.imaGePath = builder.imaGePath;
        this.coolcoins = builder.coolcoins;
        this.usedArtifacts = builder.usedArtifacts;
    }


    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public int getClassID() {
        return classID;
    }

    public String getImaGePath() {
        return imaGePath;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }


    public static class Builder {

        private String firstName;
        private String lastName;
        private String password;
        private int id;
        private String email;
        private int classID;
        private String imaGePath;
        private int coolcoins;
        private List<Card> usedArtifacts;

        public Builder withFirstName(String firstName){
            this.firstName = firstName;
            return this;
        }
        public Builder withLastName (String lastName){
            this.lastName = lastName;
            return this;
        }
        public Builder withPassword (String password ){
            this.password = password;
            return this;
        }

        public Builder wirhId (int id){
            this.id = id;
            return this;
        }

        public Builder withEmail(String email){
            this.email = email;
            return this;

        }
        public Builder withClassId ( int classID){
            this.classID = classID;
            return this;
        }

        public Builder withImagePath (String imagePath){
            this.imaGePath = imagePath;
            return this;
        }

        public Builder withCoolcoins (int coolcoins){
            this.coolcoins = coolcoins;
            return this;
        }

        public Builder withUsedArtifacts (List<Card> artifacts) {
            this.usedArtifacts = artifacts;
            return this;
        }
        public Student build(){
            return new Student (this);
        }
    }

}
