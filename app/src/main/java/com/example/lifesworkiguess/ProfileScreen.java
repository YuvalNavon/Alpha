/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can view their Profile Details, and be directed to edit them,
 * change course, or view more details.
 */


package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileScreen extends AppCompatActivity implements AdapterView.OnItemClickListener {

    FirebaseAuth fAuth;
    FirebaseUser loggedInUser;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers, refLesson;
    ValueEventListener userAndCourseGetter, getLessonName;
    TextView usernameTV, courseTV;
    ImageView profileScreenPFPIV,profileScreenMenuPFPIV ;
    Button changeCourse;
    LinearLayout profileScreen;

    ListView moreDetailsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);





        changeCourse = findViewById(R.id.PFPScreenChangeCourseBTN);


        //Loading User's Info
        loadCurrentUserData();

        moreDetailsList = findViewById(R.id.ProfileScreen_MoreDetailsList);

        String[] moreDetails = new String[]{"View Completed Lessons & Courses", "View Completed Community Recipes", "View Recipes You Made"};
        moreDetailsList.setOnItemClickListener(this);
        moreDetailsList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        ArrayAdapter<String> adp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, moreDetails);
        moreDetailsList.setAdapter(adp);



    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refUsers!=null && userAndCourseGetter !=null) refUsers.removeEventListener(userAndCourseGetter);
        if (refLesson!=null && getLessonName!=null) refLesson.removeEventListener(getLessonName);

    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refUsers!=null && userAndCourseGetter !=null) refUsers.addListenerForSingleValueEvent(userAndCourseGetter);
        if (refLesson!=null && getLessonName!=null) refLesson.addListenerForSingleValueEvent(getLessonName);
        loadCurrentUserData();
    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && userAndCourseGetter !=null) refUsers.removeEventListener(userAndCourseGetter);
        if (refLesson!=null && getLessonName!=null) refLesson.removeEventListener(getLessonName);

    }


    /**
     * this function gets the details of the currently logged in User from Firebase,
     * and displays them.
     *
     * @param
     *
     *
     *
     * @return
     */
    public void loadCurrentUserData()
    {
        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();

        //To make Loading seem smooth 1
        profileScreen = findViewById(R.id.profileScreen);
        profileScreen.setVisibility(View.INVISIBLE);


        //Loading PFP's
        profileScreenPFPIV = findViewById(R.id.profileScreenPFPIV);
        profileScreenMenuPFPIV = findViewById(R.id.profileScreenMenuPFPIV);
        myServices.getProfilePhotoFromFirebase(profileScreenPFPIV, loggedInUser.getUid());
        myServices.getProfilePhotoFromFirebase(profileScreenMenuPFPIV, loggedInUser.getUid());


        //Loading User Data
        usernameTV = findViewById(R.id.profileScreenUsernameIV);
        courseTV = findViewById(R.id.profileScreenCourseTV);

        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
        userAndCourseGetter = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User loggedInUser = snapshot.getValue(User.class);
                usernameTV.setText(loggedInUser.getUsername());
                courseTV.setText(loggedInUser.getSelectedCourse() + " Course");
                if (loggedInUser.getFinishedCourse()==MyConstants.FINISHED_COURSE){
                    changeCourse.setVisibility(View.VISIBLE);
                    changeCourse.setEnabled(true);
                }
                else{
                    changeCourse.setVisibility(View.INVISIBLE);
                    changeCourse.setEnabled(false);
                }


                profileScreen.setVisibility(View.VISIBLE);




            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(userAndCourseGetter);

    }



    /**
     * this function creates the Alert Dialog that asks the user if they're sure they want to change course.
     * if the user agrees, this function starts the ChooseCourse Activity.
     * otherwise, the alert dialog is closed.
     * <p>
     *
     * @param view - the button pressed
     *
     *
     * @return	None
     */
    public void changeCourse(View view){

        //Setting up Alert Dialogs
        AlertDialog.Builder changeCourseDialogBuilder = new AlertDialog.Builder(ProfileScreen.this);
        changeCourseDialogBuilder.setTitle("Are you Sure?");
        changeCourseDialogBuilder.setMessage(
                    "Because You Finished this Course, its Progress will be Saved" +
                            " in your History tab in the Profile screen.\n\nAre you Sure you Want to Change Course?");


        changeCourseDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Handle click here
            }
        });


        changeCourseDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent toChooseCourse = new Intent(ProfileScreen.this, ChooseCourse.class);
                toChooseCourse.putExtra(MyConstants.CHOOSE_COURSE_ORIGIN, MyConstants.FROM_PROFILE);
                startActivity(toChooseCourse);
            }
        });
        // Create and show the AlertDialog
        AlertDialog changeCourseDialog = changeCourseDialogBuilder.create();
        changeCourseDialog.show();

    }


    /**
     * this function starts the EditProfile Activity
     * <p>
     *
     * @param view - the button pressed
     *
     *
     * @return	None
     */
    public void editProfile(View view){
        Intent toEditProfileScreen = new Intent(this, EditProfile.class);
        startActivity(toEditProfileScreen);
    }

    public void goToHomePage(View view){
        myServices.goToHomePage(ProfileScreen.this);
    }

    public void goToCommunityPage(View view){  myServices.goToCommunityPage(ProfileScreen.this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent toCompletedScreen = new Intent(ProfileScreen.this, moreDetailsLists.class);

        if (position==0)
        {
           toCompletedScreen.putExtra("View Mode", "Completed Lessons & Courses");
        }
        if (position==1)
        {
            toCompletedScreen.putExtra("View Mode", "Completed Community Recipes");

        }
        if (position==2)
        {
            toCompletedScreen.putExtra("View Mode", "Your Recipes");

        }
        startActivity(toCompletedScreen);
    }
}