package main.java.Model;

public class Mentor {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private  int id;



    public Mentor (Builder builder){
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.password = builder.password;
        this.id = builder.id;
    }

    public String getEmail() {
        return email;
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


    public int getId() {
        return id;
    }


    public static class Builder{
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private  int id;

        public Builder withEmail(String email){
            this.email = email;
            return this;
        }
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

        public Builder withID (int id){
            this.id = id;
            return this;
        }


        public Mentor build(){
            return new Mentor (this);
        }


    }

}
