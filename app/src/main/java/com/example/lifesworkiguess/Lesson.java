package com.example.lifesworkiguess;

public class Lesson {

    private String LessonName;
    private String LessonRecipeName;
    private String LogoUri;
    private int Number;

    public Lesson(){
        this.LessonName = "ERROR";
        this.LessonRecipeName = null;
        this.LogoUri = null;
    }

    public Lesson(String lessonName, String lessonRecipeName, String logoUri, int number) {
        this.LessonName = lessonName;
        this.LessonRecipeName = lessonRecipeName;
        this.LogoUri = logoUri;
        this.Number = number;
    }


    public String getLessonName() {
        return LessonName;
    }

    public String getLessonRecipeName() {
        return LessonRecipeName;
    }

    public String getLogoUri() {
        return LogoUri;
    }

    public int getNumber() {
        return Number;
    }

    public void setLessonName(String lessonName) {
        this.LessonName = lessonName;
    }

    public void setLessonRecipeName(String lessonRecipeName) {
        this.LessonRecipeName = lessonRecipeName;
    }

    public void setLogoUri(String logoUri) {
        this.LogoUri = logoUri;
    }

    public void setNumber(int number) {
        Number = number;
    }
}
