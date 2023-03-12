package com.example.lifesworkiguess;

public class PermanentLesson extends Lesson {


    private int Number;

    public PermanentLesson(){
        super();
    }

    public PermanentLesson(String lessonName, String lessonRecipeName, String logoUri, int number) {
        super(lessonName, lessonRecipeName, logoUri);
        this.Number = number;
    }

    public PermanentLesson(String lessonName, String lessonRecipeName, String logoUri, int serveCount, String time, String difficulty, boolean kosher, int number) {
        super(lessonName, lessonRecipeName, logoUri, serveCount, time, difficulty, kosher);
        Number = number;
    }

    public int getNumber() {
        return Number;
    }

    public void setNumber(int number) {
        Number = number;
    }
}
