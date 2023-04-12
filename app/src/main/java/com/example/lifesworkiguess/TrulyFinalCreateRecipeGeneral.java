package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class TrulyFinalCreateRecipeGeneral extends AppCompatActivity {

    //From This
    EditText recipeNameET, recipeDescriptionET;
    String recipeName, recipeDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truly_final_create_recipe_general);

        recipeNameET = findViewById(R.id.CR_recipeNameET);
        recipeDescriptionET = findViewById(R.id.CR_recipeDescriptionET);
    }


    public boolean userHasMadeThisRecipeBefore(String recipeName){

        return false;
    }
    public boolean recipeNameHasComma(String recipeName) {
        return recipeName.contains(",");
    }

    public void next(View view){

        Intent toAddRecipeImage = new Intent(this, TrulyFinalCreateRecipeImage.class);
        toAddRecipeImage.putExtra("Previous Activity", MyConstants.NOT_FROM_FINISH_SCREEN);

        recipeName = recipeNameET.getText().toString();
        recipeDescription = recipeDescriptionET.getText().toString();

        if (!recipeName.isEmpty() && !recipeDescription.isEmpty() &&!userHasMadeThisRecipeBefore(recipeName))
        {
            //From This
            toAddRecipeImage.putExtra(MyConstants.CUSTOM_RECIPE_NAME, recipeName);
            toAddRecipeImage.putExtra(MyConstants.CUSTOM_RECIPE_DESCRIPTION, recipeDescription);


            startActivity(toAddRecipeImage);
        }

        else{
            if (recipeName.isEmpty())
            {
                Toast.makeText(this, "Please Enter a Name for this Recipe!", Toast.LENGTH_SHORT).show();
            }

            if (recipeDescription.isEmpty())
            {
                Toast.makeText(this, "Please Enter a Description for this Recipe!", Toast.LENGTH_SHORT).show();
            }

            if (userHasMadeThisRecipeBefore(recipeName))
            {
                Toast.makeText(this, "You made a Recipe with the Same Name before!", Toast.LENGTH_SHORT).show();
                //better to use Alert Dialog for this one
            }

            if (recipeNameHasComma(recipeName))
            {
                Toast.makeText(this, "The Recipe's Name can't have ',' in it!", Toast.LENGTH_SHORT).show();
                //better to use Alert Dialog for this one
            }
        }



    }
}