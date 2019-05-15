package main.java.Model;

public class Student {
    private int id;
    private String email;
    private int classID;
    private String imaGePath;

    public Student (Builder builder){
        this.id = builder.id;
        this.email = builder.email;
        this.classID = builder.classID;
        this.imaGePath = builder.imaGePath;


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

    public static class Builder {

        private int id;
        private String email;
        private int classID;
        private String imaGePath;

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
        public Student build(){
            return new Student (this);
        }
    }

}
