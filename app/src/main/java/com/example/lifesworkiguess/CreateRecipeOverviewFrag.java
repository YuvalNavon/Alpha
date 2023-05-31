/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Fragment is where the user can View all of the details for the CommunityLesson they have written/edited, excluding Ingredients and Steps.
 * In addition, the user can upload their lesson in this fragment.
 */


package com.example.lifesworkiguess;

import static android.content.Context.MODE_PRIVATE;
import static com.example.lifesworkiguess.myServices.recipeToXML;
import static com.example.lifesworkiguess.myServices.uploadXML;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class CreateRecipeOverviewFrag extends Fragment {

    String recipeName, recipeDescription;
    String recipeTime, recipeDifficultyLevel;
    int recipeServeCount;
    boolean recipeKosher;
    ArrayList<Ingredient> ingredientsList;
    ArrayList<Step> stepsList;
    String jsonofIngredients, jsonofSteps;
    ArrayList<String[]> ingredientsInStringLists, stepsInStringLists;

    TextView recipeNameTV, recipeDescriptionTV, recipeTimeTV, recipeDifficultyLevelTV, recipeServeCountTV, recipeKosherTV;
    ImageView recipeImage, kosherImage;
    Button finish;

    FirebaseDatabase FBDB;
    DatabaseReference refUsers, refCommunityLessons, refCommunityLessonsByUser;
    ValueEventListener userRecipeAdder, lessonEditor, lessonByUserEditor;
    FirebaseUser loggedInUser;
    FirebaseAuth fAuth;


    public CreateRecipeOverviewFrag() {
        // Required empty public constructor
    }


    public static CreateRecipeOverviewFrag newInstance(String param1, String param2) {
        CreateRecipeOverviewFrag fragment = new CreateRecipeOverviewFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         SharedPreferences settings=getActivity().getSharedPreferences("PREFS_NAME",MODE_PRIVATE);

         recipeName = settings.getString(MyConstants.CUSTOM_RECIPE_NAME, null);
         recipeDescription = settings.getString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, null);
         recipeTime = settings.getString(MyConstants.CUSTOM_RECIPE_TIME, null);
         recipeDifficultyLevel = settings.getString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
         recipeServeCount = settings.getInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, MyConstants.NO_SERVE_COUNT_ERROR);
         recipeKosher = settings.getBoolean(MyConstants.CUSTOM_RECIPE_KOSHER,false);
         jsonofIngredients = settings.getString(MyConstants.CUSTOM_RECIPE_INGREDIENTS,null);
         jsonofSteps = settings.getString(MyConstants.CUSTOM_RECIPE_STEPS,null);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_truly_final_create_recipe_overview, container, false);

        recipeImage = view.findViewById(R.id.CR_Finish_ImageIV);
        recipeNameTV = view.findViewById(R.id.CR_Finish_NameTV);
        recipeDescriptionTV = view.findViewById(R.id.CR_Finish_DescriptionTV);
        recipeTimeTV = view.findViewById(R.id.CR_Finish_TimeTV);
        recipeDifficultyLevelTV = view.findViewById(R.id.CR_Finish_DifficultyTV);
        recipeKosherTV = view.findViewById(R.id.CR_Finish_KosherTV);
        kosherImage = view.findViewById(R.id.CR_Finish_KosherIV);
        recipeServeCountTV = view.findViewById(R.id.CR_Finish_ServeCountTV);

        File SelectedImageFile = new File(getContext().getFilesDir(), MyConstants.IMAGE_FILE_NAME);
        if (SelectedImageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(SelectedImageFile.getAbsolutePath());

            recipeImage.setImageBitmap(bitmap);

            //TO make sure image isnt DISPLAYED HORIZONTALLY (FROM CHATGPT)
            try
            {
                // Check the orientation of the image using its EXIF metadata
                ExifInterface exif = new ExifInterface(SelectedImageFile.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // Rotate the image if necessary
                Matrix matrix = new Matrix();
                switch (orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        break;
                }

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }

            catch (IOException e)
            {

            }

            recipeImage.setImageBitmap(bitmap);
        }

        else
        {
            recipeImage.setImageResource(R.drawable.add_dish);
        }

        recipeNameTV.setText(recipeName);
        recipeDescriptionTV.setText(recipeDescription);
        recipeTimeTV.setText(recipeTime);
        recipeDifficultyLevelTV.setText(recipeDifficultyLevel);
        if (recipeKosher) {
            recipeKosherTV.setText("KOSHER");
            kosherImage.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
        }
        else{
            recipeKosherTV.setText("NOT\nKOSHER");
            kosherImage.setImageResource(android.R.drawable.ic_delete);
        }
        recipeServeCountTV.setText(Integer.toString(recipeServeCount));

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String[]>>(){}.getType();

        ingredientsInStringLists = gson.fromJson(jsonofIngredients, type);
        if(ingredientsInStringLists!=null)  StringListsToIngredients(); //can't be null cause we have input checks in ingredients screen but still


        stepsInStringLists = gson.fromJson(jsonofSteps, type);
        if (stepsInStringLists!=null) StringListsToSteps(); //can't be null cause we have input checks in steps screen but still

        finish = view.findViewById(R.id.CR_finishBTN);


        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadRecipe(v);
            }
        });


        if (getActivity().getIntent().getStringExtra("Previous Activity")!=null &&
                getActivity().getIntent().getStringExtra("Previous Activity").equals(MyConstants.FROM_PROFILE_AKA_EDIT_MODE))
        {
            recipeNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent goToEditGeneral = new Intent(getContext(), CreateRecipeGeneral.class);
                    goToEditGeneral.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);
                    getContext().startActivity(goToEditGeneral);
                }
            });

            recipeDescriptionTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToEditGeneral = new Intent(getContext(), CreateRecipeGeneral.class);
                    goToEditGeneral.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);
                    getContext().startActivity(goToEditGeneral);
                }
            });

            LinearLayout extraInfoLL = view.findViewById(R.id.CR_Finish_ExtraInfoLL);
            extraInfoLL.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToEditExtraInfo = new Intent(getContext(), CreateRecipeExtraInfo.class);
                    goToEditExtraInfo.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);
                    getContext().startActivity(goToEditExtraInfo);
                }
            });

            recipeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent goToEditImage= new Intent(getContext(), CreateRecipeImage.class);
                    goToEditImage.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);
                    getContext().startActivity(goToEditImage);
                }
            });

       }

        else
        {
            TextView editMessage = view.findViewById(R.id.CR_OverviewMessage);
            editMessage.setText("Feel Free to go Back and Change Anything.");
        }
        return view;
    }


    @Override
    public void onPause() {
        super.onPause();

        if (refUsers!=null && userRecipeAdder!=null) refUsers.removeEventListener(userRecipeAdder);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (refUsers!=null && userRecipeAdder!=null) refUsers.addListenerForSingleValueEvent(userRecipeAdder);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (refUsers!=null && userRecipeAdder!=null) refUsers.removeEventListener(userRecipeAdder);

    }


    /**
     * this function creates an ArrayList<Ingredient> of Ingredients from the ArrayList<String[]> List of Ingredient.
     * Then, this function sets that Step List in the StepViewModel.
     * @param
     *
     *
     * @return
     */
        public void StringListsToIngredients(){  //I use this method in a bunch of different activities but it feels like it should be like this for possible changes
        //depending on each activity, instead of putting it in myServices

        ingredientsList = new ArrayList<>();
        //Each ArrayList<String> in the ingredientsInLists is of the following format: name, amount, units
        for (String[] ingredientStringList : ingredientsInStringLists){
            String ingredientName = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_NAME_INDEX];
            String ingredientAmount = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_AMOUNT_INDEX];
            String ingredientUnits = ingredientStringList[MyConstants.STRING_LIST_INGREDIENT_UNITS_INDEX];

            //We make an ingredient out of every List in the ingredientsInLists and add it to the ingredientsList
            Ingredient ingredient = new Ingredient(ingredientName, ingredientAmount, ingredientUnits);
            ingredientsList.add(ingredient);

        }

    }

    /**
     * this function creates an ArrayList<Step> of steps from the ArrayList<String[]> List of Steps.
     * Then, this function sets that Step List in the StepViewModel.
     * @param
     *
     *
     * @return
     */
    public void StringListsToSteps(){ //I use this method in a bunch of different activities but it feels like it should be like this for possible changes
        //depending on each activity, instead of putting it in myServices
        stepsList = new ArrayList<>();
        for (String[] currStepString : stepsInStringLists){

            String stepName = currStepString[MyConstants.STRING_LIST_STEP_NAME_INDEX];
            String stepDescription = currStepString[MyConstants.STRING_LIST_STEP_DESCRIPTION_INDEX];
            String stepTime = currStepString[MyConstants.STRING_LIST_STEP_TIME_INDEX];
            String stepAction = currStepString[MyConstants.STRING_LIST_STEP_ACTION_INDEX];
            // String stepNumber - No need, we get that from index

            Step currStep = new Step(stepName, stepDescription, stepTime, stepAction, stepsInStringLists.indexOf(currStepString));
            stepsList.add(currStep);
        }
    }


    /**
     * this function  uploads the CommunityLesson and Recipe created/edited by the user to Firebase.
     *
     * @param view - the button pressed.
     *
     *
     * @return
     */
    public void uploadRecipe(View view){

        //Adding the Recipe to the LoggedInUser List of made recipes

        Context context = getContext();

        if (getActivity().getIntent().getStringExtra("Previous Activity")!=null &&
                getActivity().getIntent().getStringExtra("Previous Activity").equals(MyConstants.FROM_PROFILE_AKA_EDIT_MODE))
        { //User is editing uploaded Recipe

            SharedPreferences settings=getActivity().getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
            int lessonNumber = settings.getInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_NUMBER, MyConstants.NO_COMMUNITY_LESSON_NUMBER_ERROR);

            fAuth = FirebaseAuth.getInstance();
            loggedInUser = fAuth.getCurrentUser();
            FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
            refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
            userRecipeAdder = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User currentUser = snapshot.getValue(User.class);
                    currentUser.changeCustomRecipeName(lessonNumber,  recipeName);
                    refUsers.setValue(currentUser);


                    refCommunityLessons =FBDB.getReference("Community Lessons").child(loggedInUser.getUid() +  " , " + lessonNumber);
                    lessonEditor = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            CommunityLesson originalLesson = snapshot.getValue(CommunityLesson.class);
                            originalLesson.setLessonName(recipeName);
                            originalLesson.setLessonRecipeName(recipeName);
                            originalLesson.setDescription(recipeDescription);
                            originalLesson.setTime(recipeTime);
                            originalLesson.setDifficulty(recipeDifficultyLevel);
                            originalLesson.setKosher(recipeKosher);
                            originalLesson.setServeCount(recipeServeCount);

                            refCommunityLessons.setValue(originalLesson);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    refCommunityLessons.addListenerForSingleValueEvent(lessonEditor);

                    refCommunityLessonsByUser = FBDB.getReference("Community Lessons By User").child(loggedInUser.getUid()).child(Integer.toString(lessonNumber));
                    lessonByUserEditor = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            CommunityLesson originalLesson = snapshot.getValue(CommunityLesson.class);
                            originalLesson.setLessonName(recipeName);
                            originalLesson.setLessonRecipeName(recipeName);
                            originalLesson.setDescription(recipeDescription);
                            originalLesson.setTime(recipeTime);
                            originalLesson.setDifficulty(recipeDifficultyLevel);
                            originalLesson.setKosher(recipeKosher);
                            originalLesson.setServeCount(recipeServeCount);

                            refCommunityLessonsByUser.setValue(originalLesson);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    refCommunityLessonsByUser.addListenerForSingleValueEvent(lessonByUserEditor);


                    //Adding the Recipe to the FirebaseStorage
                    Recipe addedRecipe = new Recipe(recipeName, ingredientsList, stepsList);
                    recipeToXML(context, addedRecipe);
                    uploadXML(context,  Integer.toString(lessonNumber));


                    //Checking if the lesson has a photo
                    File selectedRecipeImageFile = new File(getContext().getFilesDir(), MyConstants.IMAGE_FILE_NAME);

                    //Default no Photo
                    int resourceId = getResources().getIdentifier("add_dish_photo", "drawable", getContext().getPackageName());
                    Uri selectedRecipeImageFileURI = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + resourceId);


                    //Uploading the RecipeImage to the FirebaseStorage
                    if (myServices.isFileExists(context , MyConstants.IMAGE_FILE_NAME))
                    {
                        FirebaseAuth fAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentlyLoggedInUser = fAuth.getCurrentUser();
                        selectedRecipeImageFileURI = Uri.fromFile(selectedRecipeImageFile);
                        StorageReference fStorage = FirebaseStorage.getInstance().getReference("Community Recipes").child(currentlyLoggedInUser.getUid())
                                .child(Integer.toString(lessonNumber));
                        if (selectedRecipeImageFileURI!=null){
                            StorageReference fRef = fStorage.child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);

                            fRef.putFile(selectedRecipeImageFileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(context, "Error, File was not Uploaded", Toast.LENGTH_LONG).show();
                        }
                    }


                    //General - no need, as its the first screen in making recipes so if its closed then the recipe should be gone

                    //Image
                    File recipeImageFile = new File(getContext().getFilesDir(), MyConstants.IMAGE_FILE_NAME);
                    if (recipeImageFile.exists()) {
                        recipeImageFile.delete();
                    }

                    File recipeNoImageFile = new File(getContext().getFilesDir(), MyConstants.NO_IMAGE_FILE_NAME);
                    if (recipeNoImageFile.exists()) {
                        recipeNoImageFile.delete();
                    }

                    //Ingredients, Steps and Extra Info
                    SharedPreferences settings=getActivity().getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                    SharedPreferences.Editor editor=settings.edit();

                    editor.putString(MyConstants.CUSTOM_RECIPE_NAME, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, null);
                    editor.putInt(MyConstants.CUSTOM_RECIPE_HOURS_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
                    editor.putInt(MyConstants.CUSTOM_RECIPE_MINUTES_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
                    editor.putString(MyConstants.CUSTOM_RECIPE_TIME, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
                    editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, MyConstants.NO_SERVE_COUNT_ERROR);
                    editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, false);

                    editor.putInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_NUMBER, MyConstants.NO_COMMUNITY_LESSON_NUMBER_ERROR);
                    editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_NAME,null);
                    editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_DESCRIPTION,null);
                    editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_INGREDIENTS, null);
                    editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_STEPS, null);
                    editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_TIME, null);
                    editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_DIFFICULTY_LEVEL,null);
                    editor.putBoolean(MyConstants.ORIGINAL_CUSTOM_RECIPE_KOSHER, false);
                    editor.putInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_SERVE_COUNT,MyConstants.NO_SERVE_COUNT_ERROR);




                    Intent toCommunityScreen = new Intent(getContext(), CommunityScreen.class);
                    toCommunityScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toCommunityScreen);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            refUsers.addListenerForSingleValueEvent(userRecipeAdder);
        }

        else //User is uploading new Recipe
        {


            fAuth = FirebaseAuth.getInstance();
            loggedInUser = fAuth.getCurrentUser();
            FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
            refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
            userRecipeAdder = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User currentUser = snapshot.getValue(User.class);
                    currentUser.addCustomRecipe(recipeName);
                    refUsers.setValue(currentUser);

                    //Adding the Recipe to the Community Lessons and Community Lessons By User branches of the Database
                    CommunityLesson  addedLesson = new CommunityLesson(recipeName, recipeName, recipeServeCount, recipeTime,
                            recipeDifficultyLevel, recipeKosher, loggedInUser.getUid(), recipeDescription, currentUser.getUploadedRecipeNames().size()-1 );


                    refCommunityLessons =FBDB.getReference("Community Lessons");
                    refCommunityLessons.child( loggedInUser.getUid() + " , " + Integer.toString(currentUser.getUploadedRecipeNames().size()-1)).setValue(addedLesson);

                    refCommunityLessons =FBDB.getReference("Community Lessons By User").child(loggedInUser.getUid());
                    refCommunityLessons.child(Integer.toString(currentUser.getUploadedRecipeNames().size()-1)).setValue(addedLesson);


                    //Adding the Recipe to the FirebaseStorage
                    Recipe addedRecipe = new Recipe(recipeName, ingredientsList, stepsList);
                    recipeToXML(context, addedRecipe);
                    uploadXML(context,  Integer.toString(currentUser.getUploadedRecipeNames().size()-1)); //the -1 is bc we add this recipe to this list right at the start of the onDataChange


                    //Checking if the lesson has a photo
                    File selectedRecipeImageFile = new File(getContext().getFilesDir(), MyConstants.IMAGE_FILE_NAME);

                    //Default no Photo
                    int resourceId = getResources().getIdentifier("add_dish_photo", "drawable", getContext().getPackageName());
                    Uri selectedRecipeImageFileURI = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + resourceId);


                    //Uploading the RecipeImage to the FirebaseStorage
                    if (myServices.isFileExists(context , MyConstants.IMAGE_FILE_NAME))
                    {
                        FirebaseAuth fAuth = FirebaseAuth.getInstance();
                        FirebaseUser currentlyLoggedInUser = fAuth.getCurrentUser();
                        selectedRecipeImageFileURI = Uri.fromFile(selectedRecipeImageFile);
                        StorageReference fStorage = FirebaseStorage.getInstance().getReference("Community Recipes").child(currentlyLoggedInUser.getUid())
                                .child(Integer.toString(currentUser.getUploadedRecipeNames().size()-1)); //the -1 is bc we add this recipe to this list right at the start of the onDataChange);
                        if (selectedRecipeImageFileURI!=null){
                            StorageReference fRef = fStorage.child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);

                            fRef.putFile(selectedRecipeImageFileURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(context, "Error, File was not Uploaded", Toast.LENGTH_LONG).show();
                        }
                    }


                    //General - no need, as its the first screen in making recipes so if its closed then the recipe should be gone

                    //Image
                    File recipeImageFile = new File(getContext().getFilesDir(), MyConstants.IMAGE_FILE_NAME);
                    if (recipeImageFile.exists()) {
                        recipeImageFile.delete();
                    }

                    File recipeNoImageFile = new File(getContext().getFilesDir(), MyConstants.NO_IMAGE_FILE_NAME);
                    if (recipeNoImageFile.exists()) {
                        recipeNoImageFile.delete();
                    }

                    //Ingredients, Steps and Extra Info
                    SharedPreferences settings=getActivity().getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putString(MyConstants.CUSTOM_RECIPE_NAME, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, null);
                    editor.putInt(MyConstants.CUSTOM_RECIPE_HOURS_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
                    editor.putInt(MyConstants.CUSTOM_RECIPE_MINUTES_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
                    editor.putString(MyConstants.CUSTOM_RECIPE_TIME, null);
                    editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
                    editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, 0);
                    editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, false);
                    editor.commit();

                    Intent toCommunityScreen = new Intent(getContext(), CommunityScreen.class);
                    toCommunityScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toCommunityScreen);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            refUsers.addListenerForSingleValueEvent(userRecipeAdder);
        }








    }
}