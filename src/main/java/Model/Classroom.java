package Model;

import java.sql.Date;
import java.time.LocalDate;

public class Classroom {
    private int id;
    private LocalDate startDate;
    private int mentorId;

    public Classroom (Builder builder){
        this.id = builder.id;
        this.startDate = builder.startDate;
        this.mentorId = builder.mentorId;


    }

    public int getId() {
        return id;
    }

    public int getMentorId() {
        return mentorId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }




    public static class Builder {
        private int id;
        private LocalDate startDate;
        private int mentorId;


        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withStartDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;

        }

        public Builder withMentorId(int mentorId) {
            this.mentorId = mentorId;
            return this;
        }

        public Classroom build(){
            return new Classroom(this);
        }
    }
}
