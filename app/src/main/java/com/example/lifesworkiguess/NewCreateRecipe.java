package com.example.lifesworkiguess;

import static com.example.lifesworkiguess.myServices.recipeToXML;
import static com.example.lifesworkiguess.myServices.uploadXML;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class NewCreateRecipe extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
EditText titleET, ing1N, ing1A, ing1U,ing2N, ing2A, ing2U, ing3N, ing3A, ing3U,
        step1N, step1D, step2N, step2D, step3N, step3D;
EditText[] Ing1, Ing2, Ing3, Step1, Step2, Step3;
ArrayList<EditText[]> allIngredients, allSteps;
ArrayList<ArrayList<EditText[]>> combinedList;
Spinner spinIng, spinStep;

String title, ingName, ingAmount, ingUnits, stepName, stepDescription,stepTime, stepAction, lastUploaded;
int ingCount, stepCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_create_recipe);


        titleET = findViewById(R.id.Title);

        //Linking Ingredients and creating array

        ing1N = findViewById(R.id.ing1N);
        ing1A = findViewById(R.id.ing1A);
        ing1U = findViewById(R.id.ing1U);
        Ing1 = new EditText[]{ing1N, ing1A, ing1U};

        ing2N = findViewById(R.id.ing2N);
        ing2A = findViewById(R.id.ing2A);
        ing2U = findViewById(R.id.ing2U);
        Ing2 = new EditText[]{ing2N, ing2A, ing2U};

        ing3N = findViewById(R.id.ing3N);
        ing3A = findViewById(R.id.ing3A);
        ing3U = findViewById(R.id.ing3U);
        Ing3 = new EditText[]{ing3N, ing3A, ing3U};


        allIngredients = new ArrayList<>();
        allIngredients.add(Ing1); allIngredients.add(Ing2); allIngredients.add(Ing3);


        //Linking Steps and creating array

        step1N = findViewById(R.id.step1N);
        step1D = findViewById(R.id.step1D);
        Step1 = new EditText[]{step1N, step1D};

        step2N = findViewById(R.id.step2N);
        step2D = findViewById(R.id.step2D);
        Step2 = new EditText[]{step2N, step2D};

        step3N = findViewById(R.id.step3N);
        step3D = findViewById(R.id.step3D);
        Step3 = new EditText[]{step3N, step3D};

        allSteps = new ArrayList<>();
        allSteps.add(Step1); allSteps.add(Step2); allSteps.add(Step3);


        combinedList = new ArrayList<>();
        combinedList.add(allIngredients); combinedList.add(allSteps);


        spinIng = findViewById(R.id.ingredientSpinner);
        String[] ingNumberforSpinner = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adpIng = new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, ingNumberforSpinner);
        spinIng.setAdapter(adpIng);
        spinIng.setOnItemSelectedListener(this);

        spinStep = findViewById(R.id.stepSpinner);
        String[] stepNumberforSpinner = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adpStep = new ArrayAdapter<String>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, stepNumberforSpinner);
        spinStep.setAdapter(adpStep);
        spinStep.setOnItemSelectedListener(this);


    }







    public boolean checkIfFilled(int ingCount, int stepCount){
        //Check Title field
        if (titleET.getText().toString().isEmpty()) return false;

        //Check all Ingredient Fields
        for (int i = 0; i<ingCount; i++){
            for (int j = 0; j<Ing1.length; j++){
                if (allIngredients.get(i)[j].getText().toString().isEmpty()) return false;
            }

        }

        //Check all Step Fields
        for (int i = 0; i<stepCount; i++){
            for (int j = 0; j<Step1.length; j++){
                if (allSteps.get(i)[j].getText().toString().isEmpty()) return false;
            }

        }
        return true;

    }

    public void setIngredients(int ingCount){
        //NOTICE: ingCount is the ACTUAL number of ings, not the index in the arraylist
        for (int i = 0; i<ingCount; i++){

            allIngredients.get(i)[0].setEnabled(true);
            allIngredients.get(i)[0].setVisibility(View.VISIBLE);

            allIngredients.get(i)[1].setEnabled(true);
            allIngredients.get(i)[1].setVisibility(View.VISIBLE);

            allIngredients.get(i)[2].setEnabled(true);
            allIngredients.get(i)[2].setVisibility(View.VISIBLE);



        }
        for (int i = ingCount; i<3; i++){

            allIngredients.get(i)[0].setEnabled(false);
            allIngredients.get(i)[0].setVisibility(View.INVISIBLE);

            allIngredients.get(i)[1].setEnabled(false);
            allIngredients.get(i)[1].setVisibility(View.INVISIBLE);

            allIngredients.get(i)[2].setEnabled(false);
            allIngredients.get(i)[2].setVisibility(View.INVISIBLE);

        }
    }

    public void setSteps(int stepCount){
        //NOTICE: stepCount is the ACTUAL number of steps, not the index in the arraylist
        for (int i = 0; i<stepCount; i++){

            allSteps.get(i)[0].setEnabled(true);
            allSteps.get(i)[0].setVisibility(View.VISIBLE);

            allSteps.get(i)[1].setEnabled(true);
            allSteps.get(i)[1].setVisibility(View.VISIBLE);

        }
        for (int i = stepCount; i<3; i++){

            allSteps.get(i)[0].setEnabled(false);
            allSteps.get(i)[0].setVisibility(View.INVISIBLE);


            allSteps.get(i)[1].setEnabled(false);
            allSteps.get(i)[1].setVisibility(View.INVISIBLE);

        }
    }

    public void createRecipe(View view){
        if (checkIfFilled(ingCount, stepCount)){
            title = titleET.getText().toString();
            Recipe recipe = new Recipe(title);
            for (int i = 0; i<ingCount;i++){
                ingName = allIngredients.get(i)[0].getText().toString();
                ingAmount = allIngredients.get(i)[1].getText().toString();
                ingUnits = allIngredients.get(i)[2].getText().toString();
                Ingredient addedIng = new Ingredient
                        (ingName, ingAmount, ingUnits);
                recipe.addIngredient(addedIng);
            }

            for (int i = 0; i<stepCount;i++){
                stepName = allSteps.get(i)[0].getText().toString();
                stepDescription = allSteps.get(i)[1].getText().toString();
                //UPDATE STEP TIME EDIT TEXT and STEP ACTION GIPHY
                stepTime = "1 min.";
                stepAction = "https://media.giphy.com/media/xUOxfjsW9fWPqEWouI/giphy.gif";
                Step addedStep = new Step
                        (stepName, stepDescription, stepTime, stepAction);
                //THE NEXT LINE IS THE ONE THAT DECIDES THE STEP NUMBER THAT IS GOTTEN
                //DURING THE RECIPETOXML METHOD
                recipe.addStep(addedStep);
            }

            recipeToXML(NewCreateRecipe.this, recipe, title);
            uploadXML(this, title);
            lastUploaded = "Recipe For " + title + "." + "xml";
        }
        else{
            Toast.makeText(this, "HUHH", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            if (adapterView.getId()== R.id.ingredientSpinner){
                ingCount = i+ 1;
                setIngredients(ingCount);
            }

            if (adapterView.getId()== R.id.stepSpinner){
                stepCount = i+ 1;
                setSteps(stepCount);
            }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}