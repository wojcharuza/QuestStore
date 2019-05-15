package Model;

public class User {

    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String permission;
    private  int id;
    private int classID;


    public User(Builder builder){
        this.email = builder.email;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.password = builder.password;
        this.permission = builder.permission;
        this.id = builder.id;
        this.classID = builder.classID;
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

    public String getPermission() {
        return permission;
    }

    public int getId() {
        return id;
    }

    public int getClassID() {
        return classID;
    }

    public static class Builder{
        private String email;
        private String firstName;
        private String lastName;
        private String password;
        private String permission;
        private  int id;
        private int classID;

        public Builder withUserEmail(String email){
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
        public Builder withPermission (String permission){
            this.permission = permission;
            return this;
        }
        public Builder withID (int id){
            this.id = id;
            return this;
        }
        public Builder withClassID (int classID){
            this.classID = classID;
            return this;
        }

        public User build(){
            return new User(this);
        }


    }




}

