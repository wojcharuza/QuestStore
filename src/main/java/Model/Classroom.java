package Model;

import java.time.LocalDate;

public class Classroom {
    private int id;
    private String startDate;
    private int mentorId;
    private String mentorName;

    public Classroom (Builder builder){
        this.id = builder.id;
        this.startDate = builder.startDate;
        this.mentorId = builder.mentorId;
        this.mentorName = builder.mentorName;


    }

    public int getId() {
        return id;
    }

    public int getMentorId() {
        return mentorId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getMentorName() { return mentorName; }




    public static class Builder {
        private int id;
        private String startDate;
        private int mentorId;
        private String mentorName;


        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withStartDate(String startDate) {
            this.startDate = startDate;
            return this;

        }

        public Builder withMentorId(int mentorId) {
            this.mentorId = mentorId;
            return this;
        }

        public Builder withName(String name) {
            this.mentorName = name;
            return this;
        }

        public Classroom build(){
            return new Classroom(this);
        }
    }
}
