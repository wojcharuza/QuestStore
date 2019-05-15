package main.java.Model;

import java.sql.Date;

public class Classroom {
    private int id;
    private Date startDate;
    private int classID;

    public Classroom (Builder builder){
        this.id = builder.id;
        this.startDate = builder.startDate;
        this.classID = builder.classID;


    }

    public int getId() {
        return id;
    }

    public int getClassID() {
        return classID;
    }

    public Date getStartDate() {
        return startDate;
    }




    public static class Builder {
        private int id;
        private Date startDate;
        private int classID;


        public Builder wirhId(int id) {
            this.id = id;
            return this;
        }

        public Builder withEmail(Date startDate) {
            this.startDate = startDate;
            return this;

        }

        public Builder withClassId(int classID) {
            this.classID = classID;
            return this;
        }

        public Classroom build(){
            return new Classroom(this);
        }
    }
}
