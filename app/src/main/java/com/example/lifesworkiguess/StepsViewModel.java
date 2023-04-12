package com.example.lifesworkiguess;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class StepsViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Step>> stepsList = new MutableLiveData<>();

    public void setStepsList(ArrayList<Step> stepsList){
        this.stepsList.setValue(stepsList);
    }

    public LiveData<ArrayList<Step>> getStepsList(){

        if (stepsList.getValue() == null)
        {
           stepsList.setValue(new ArrayList<Step>());
        }
        return stepsList;
    }
}
