package com.example.lifesworkiguess;

public class Lesson {
    private String LessonName;
    private String LessonRecipeName;
    private String LogoUri;
    private int ServeCount;
    private String Time;
    private String Difficulty;
    private boolean Kosher;

    public Lesson(){
        this.LessonName = "ERROR";
        this.LessonRecipeName = null;
        this.LogoUri = null;
        this.Time = null;
        this.Difficulty = null;
    }

    public Lesson(String lessonName, String lessonRecipeName, String logoUri) {
        this.LessonName = lessonName;
        this.LessonRecipeName = lessonRecipeName;
        this.LogoUri = logoUri;
    }

    public Lesson(String lessonName, String lessonRecipeName, String logoUri, int serveCount, String time, String difficulty, boolean kosher) {
        this.LessonName = lessonName;
        this.LessonRecipeName = lessonRecipeName;
        this.LogoUri = logoUri;
        this.ServeCount = serveCount;
        this.Time = time;
        this.Difficulty = difficulty;
        this.Kosher = kosher;
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

    public void setLessonName(String lessonName) {
        this.LessonName = lessonName;
    }

    public void setLessonRecipeName(String lessonRecipeName) {
        this.LessonRecipeName = lessonRecipeName;
    }

    public void setLogoUri(String logoUri) {
        this.LogoUri = logoUri;
    }

    public int getServeCount() {
        return ServeCount;
    }

    public void setServeCount(int serveCount) {
        ServeCount = serveCount;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDifficulty() {
        return Difficulty;
    }

    public void setDifficulty(String difficulty) {
        Difficulty = difficulty;
    }

    public boolean isKosher() {
        return Kosher;
    }

    public void setKosher(boolean kosher) {
        Kosher = kosher;
    }
}
