package com.example.lifesworkiguess;

import android.net.Uri;

import com.example.lifesworkiguess.Recipe;

import java.util.ArrayList;

public class Lesson {

    private String lessonName;
    private String lessonRecipeName;
    private ArrayList<String> extraInfoList;
    private String extraInfo;
    private String logoUri;

    public Lesson(){
        this.lessonName = "ERROR";
        this.lessonRecipeName = null;
        this.extraInfo = null;
        this.extraInfoList = null;
        this.logoUri = null;
    }

    public Lesson(String lessonName, String lessonRecipeName, String extraInfo, String logoUri) {
        this.lessonName = lessonName;
        this.lessonRecipeName = lessonRecipeName;
        this.extraInfo = extraInfo;
        this.logoUri = logoUri;
        this.extraInfoList = new ArrayList<>();

    }

    public void formatExtraInfo(){
        extraInfoList = new ArrayList<>();
        int count = 0;
        for (int i = 0; i<extraInfo.length(); i++){
            if (extraInfo.charAt(i) ==',')count = count+1;
        }
        for (int i = 0; i<count; i++){
            String currInfo = extraInfo.substring(0, extraInfo.indexOf(','));
            extraInfoList.add(currInfo);
            extraInfo =extraInfo.substring(extraInfo.indexOf(',')+1);

        }
        extraInfoList.add(extraInfo);
    }

    public String getLessonName() {
        return lessonName;
    }

    public String getLessonRecipeName() {
        return lessonRecipeName;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public ArrayList<String> getExtraInfoList() {
        return extraInfoList;
    }

    public String getLogoUri() {
        return logoUri;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public void setLessonRecipeName(String lessonRecipeName) {
        this.lessonRecipeName = lessonRecipeName;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public void setLogoUri(String logoUri) {
        this.logoUri = logoUri;
    }

    public void setExtraInfoList(ArrayList<String> extraInfoList) {
        this.extraInfoList = extraInfoList;
    }
}
