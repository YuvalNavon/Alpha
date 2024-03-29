/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can see Uploaded Recipes, and be directed to write their own Recipe.
 */

package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class CommunityScreen extends AppCompatActivity implements CommunityDishesCustomViewHolder.OnItemClickListener, CommunityLessonViewHolder.OnItemClickListener {

    RecyclerView dishesCatagoryRV, foundCommunityLessonsRV;
    FirebaseDatabase FBDB;
    DatabaseReference refCommunityLessons, refUsers;
    ValueEventListener communityLessonsGetter, usernameGetter;


    String pickedDishCatagoryName, searchQuery;
    ArrayList<CommunityLesson> foundLessonsList;
    ArrayList<String> usernamesList, userIDsList;

    ImageView pfpIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community_screen);

        makeDishCatagoryRV();

        //to make sure everything added from previous custom recipes is deleted:
        deleteEverythingFromLastRecipe();

        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseUser loggedInUser =fAuth.getCurrentUser();

        pfpIV = findViewById(R.id.CommunityScreenPFP);
        myServices.getProfilePhotoFromFirebase(pfpIV, loggedInUser.getUid());

    }


    @Override
    protected void onPause() {

        super.onPause();
        if (refUsers!=null && usernameGetter !=null) refUsers.removeEventListener(usernameGetter);
        if (refCommunityLessons!=null && communityLessonsGetter !=null) refCommunityLessons.removeEventListener(communityLessonsGetter);


    }


    @Override
    protected void onResume() {

        super.onResume();

        //to make sure everything added from previous custom recipes is deleted:
        deleteEverythingFromLastRecipe();



    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && usernameGetter !=null) refUsers.removeEventListener(usernameGetter);
        if (refCommunityLessons!=null && communityLessonsGetter !=null) refCommunityLessons.removeEventListener(communityLessonsGetter);


    }


    /**
     * this function deletes all of the details saved while writing a recipe from SharedPreferences
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void deleteEverythingFromLastRecipe(){

        //General - no need, as its the first screen in making recipes so if its closed then the recipe should be gone

        //Image
        File recipeImageFile = new File(getFilesDir(), MyConstants.IMAGE_FILE_NAME);
        if (recipeImageFile.exists()) {
            recipeImageFile.delete();
        }

        File recipeNoImageFile = new File(getFilesDir(), MyConstants.NO_IMAGE_FILE_NAME);
        if (recipeNoImageFile.exists()) {
            recipeNoImageFile.delete();
        }

        //Ingredients, Steps and Extra Info
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, null);
        editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, null);
        editor.putInt(MyConstants.CUSTOM_RECIPE_HOURS_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
        editor.putInt(MyConstants.CUSTOM_RECIPE_MINUTES_SPINNER_CURR_POS, MyConstants.CUSTOM_RECIPE_NO_SPINNER_POS_SAVED);
        editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, null);
        editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, 0);
        editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, false);
        editor.commit();



    }

    /**
     * this function makes and sets the CommunityDishesAdapter for the Recycler view showing the Main Dish Types.
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void makeDishCatagoryRV(){


        dishesCatagoryRV = findViewById(R.id.catagoryDishesRV);

        CommunityDishesAdapter communityDishesAdapter = new CommunityDishesAdapter(CommunityScreen.this,
                MyConstants.dishCatagoryNames, MyConstants.dishCatagoryLogoNames, this::onItemClick);

        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommunityScreen.this);
        linearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        dishesCatagoryRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        dishesCatagoryRV.setAdapter(communityDishesAdapter);
    }


    /**
     * this function gets the List of usernames whose recipes were found.
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void getUsernamesList()
    {
            //First we get the user Details
        usernamesList = new ArrayList<>();

        refUsers = FBDB.getReference("Users");
        usernameGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String checkedID : userIDsList) {

                    for (DataSnapshot childSnapshot : snapshot.getChildren())
                    {
                        if (childSnapshot.getKey().equals(checkedID))
                        {
                            User addedUser = childSnapshot.getValue(User.class);
                            usernamesList.add(addedUser.getUsername());
                        }
                    }
                }

                //Now to make the Community Lessons RV
                makeFoundLessonsRV();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(usernameGetter);

    }

    /**
     * this function makes and sets the CommunityLessonCustomAdapter for the Recycler view showing the Found Community Lessons.
     * <p>
     *
     * @param
     *
     *
     * @return	None
     */
    public void makeFoundLessonsRV()
    {

        foundCommunityLessonsRV = findViewById(R.id.CommunityLessonsRV);

        // Create an instance of your adapter
        CommunityLessonCustomAdapter adapter =
                new CommunityLessonCustomAdapter(CommunityScreen.this, foundLessonsList, userIDsList, usernamesList, this::onItemClick2);


        // Set the layout manager for the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommunityScreen.this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        foundCommunityLessonsRV.setLayoutManager(linearLayoutManager);

        // Set the adapter for the RecyclerView
        foundCommunityLessonsRV.setAdapter(adapter);

        //DividerItemDecoration
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(CommunityScreen.this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(foundCommunityLessonsRV.getContext(),
                R.drawable.divider_black));
        foundCommunityLessonsRV.addItemDecoration(dividerItemDecoration);
    }


    /**
     * this function uses the database to create a list of all the lessons that include the input of the user in the search bar, in their name or description.
     * <p>
     *
     * @param view - the button that was clicked
     *
     *
     * @return	None
     */
    public void search(View view)
    {
        EditText searchBar = findViewById(R.id.CommunityScreen_Searchbar);
        searchQuery = searchBar.getText().toString();
        if (searchQuery!=null && !searchQuery.isEmpty())
        {
            foundLessonsList = new ArrayList<>();
            userIDsList = new ArrayList<>();

            searchQuery = searchQuery.toLowerCase(Locale.ROOT);
            refCommunityLessons = FBDB.getReference("Community Lessons");

            Query query = refCommunityLessons.orderByChild("lessonName");

            communityLessonsGetter = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {


                        String checkedLessonName = childSnapshot.child("lessonName").getValue(String.class); //Could use "lessonRecipeName" instead of "lessonName"
                        String checkedLessonDescription = childSnapshot.child("description").getValue(String.class);

                        checkedLessonName = checkedLessonName.toLowerCase();
                        checkedLessonDescription = checkedLessonDescription.toLowerCase();
                        if (checkedLessonName.contains(searchQuery) || checkedLessonDescription.contains(searchQuery)) {

                            CommunityLesson foundLesson = childSnapshot.getValue(CommunityLesson.class);
                            if (foundLesson.isActive())
                            {
                                foundLessonsList.add(foundLesson);
                                userIDsList.add(foundLesson.getUserID());
                            }

                        }
                    }

                    getUsernamesList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            query.addListenerForSingleValueEvent(communityLessonsGetter);
        }

    }

    /**
     * this function uses the database to create a list of all the lessons that include the Main Dish Type name that the user clicked, in their name or description.
     * <p>
     *
     * @param position - the position of the item that was clicked
     *
     *
     * @return	None
     */
    @Override
    public void onItemClick(int position) {

        foundLessonsList = new ArrayList<>();
        userIDsList = new ArrayList<>();

        pickedDishCatagoryName = MyConstants.dishCatagoryNames[position];
        pickedDishCatagoryName = pickedDishCatagoryName.toLowerCase();

        refCommunityLessons = FBDB.getReference("Community Lessons");

        Query query = refCommunityLessons.orderByChild("lessonName");

        communityLessonsGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {


                    String checkedLessonName = childSnapshot.child("lessonName").getValue(String.class); //Could use "lessonRecipeName" instead of "lessonName"
                    String checkedLessonDescription = childSnapshot.child("description").getValue(String.class);

                    checkedLessonName = checkedLessonName.toLowerCase();
                    checkedLessonDescription = checkedLessonDescription.toLowerCase();
                    if (checkedLessonName.contains(pickedDishCatagoryName) || checkedLessonDescription.contains(pickedDishCatagoryName)) {

                        CommunityLesson foundLesson = childSnapshot.getValue(CommunityLesson.class);
                        if (foundLesson.isActive())
                        {
                            foundLessonsList.add(foundLesson);
                            userIDsList.add(foundLesson.getUserID());
                        }

                    }
                }

                getUsernamesList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        query.addListenerForSingleValueEvent(communityLessonsGetter);
        
    }



    public void toCreateRecipe(View view){
        Intent toCreateRecipeScreen = new Intent(this, CreateRecipeGeneral.class);
        startActivity(toCreateRecipeScreen);
    }


    /**
     * this function sends the user to the NewLessonIntro Activity, to start the Lesson that was clicked.
     * <p>
     *
     * @param position - the position of the item that was clicked
     *
     *
     * @return	None
     */
    @Override
    public void onItemClick2(int position) {

        Intent toLessonIntro = new Intent(this, NewLessonIntro.class);
        toLessonIntro.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.COMMUNITY_LESSON_INTRO);
        toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, userIDsList.get(position));
        toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY, usernamesList.get(position));
        toLessonIntro.putExtra(MyConstants.LESSON_NAME_KEY, foundLessonsList.get(position).getLessonName());
        toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY, foundLessonsList.get(position).getDescription());
        toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, foundLessonsList.get(position).getNumber());


        startActivity(toLessonIntro);


    }


    public void goToHomePage(View view){
        myServices.goToHomePage(CommunityScreen.this);
    }

    public void goToProfile(View view){
        myServices.goToProfilePage(CommunityScreen.this);
    }

}