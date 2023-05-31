/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can rate, review (if the Lesson finished is a CommunityLesson),
 * and add a photo of the dish they made.
 */

package com.example.lifesworkiguess;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.Space;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class LessonFinished extends AppCompatActivity {

    Uri photoURI;
    ImageView dishPhotoIV;
    RatingBar ratingBar;
    TextView reviewTV, ratingMessageTV;
    ScrollView reviewSV;
    EditText reviewET;
    Space reviewSpace;

    String currentPhotoPath;
    boolean dishHasPhoto;

    //For Permanent Lesson
    int lessonPosition;

    //For Community Lesson
    String creatorID;
    int lessonNumber;
    DatabaseReference refCommunityLessons, refCommunityLessonsByUser;
    ValueEventListener communityLessonGetter, communityLessonGetterByUser;

    //For Both
    int mode;
    String lessonName;


    FirebaseAuth fAuth;
    FirebaseUser loggedInUser;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    ValueEventListener lessonRater;
    StorageReference fDownRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_finished);

        dishHasPhoto = false;

        dishPhotoIV = findViewById(R.id.dishPhotoIV);

        ratingMessageTV = findViewById(R.id.LessonFinishedRatingMessageTV);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        ratingBar.setRating(3);

        reviewTV = findViewById(R.id.LessonFinishedReviewTV);
        reviewSV = findViewById(R.id.LessonFinishedReviewSV);
        reviewET = findViewById(R.id.LessonFinishedReviewET);
        reviewET.setMinLines(1);
        reviewET.setMaxLines(Integer.MAX_VALUE);
        reviewSpace = findViewById(R.id.LessonFinishedReviewSpace1);



        Intent gi = getIntent();

        mode = gi.getIntExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.LESSON_INTRO_MODE_ERROR);

        if (mode == MyConstants.PERMENANT_LESSON_INTRO)
        {
            lessonPosition = gi.getIntExtra("Lesson Position in List", MyConstants.NO_LESSON_POSITION);
            lessonName = gi.getStringExtra("Lesson Name");
            ratingMessageTV.setText("Rate how your Dish turned out!\nP.S: Rating a Lesson as 0 counts as not rating it.");
            reviewTV.setVisibility(View.GONE);
            reviewSV.setVisibility(View.GONE);
            reviewSpace.setVisibility(View.GONE);
        }

        else if (mode == MyConstants.COMMUNITY_LESSON_INTRO)
        {

            lessonName = gi.getStringExtra(MyConstants.LESSON_NAME_KEY);
            creatorID = gi.getStringExtra(MyConstants.LESSON_CREATOR_ID_KEY);
            lessonNumber = gi.getIntExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, MyConstants.NO_COMMUNITY_LESSON_NUMBER_ERROR);
            ratingMessageTV.setText("Rate This Recipe!\nP.S: Rating a Lesson as 0 counts as not rating it.");


        }


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button press event here
                //Setting up Alert Dialogs
                AlertDialog.Builder saveDialogBuilder = new AlertDialog.Builder(LessonFinished.this);

                saveDialogBuilder.setTitle("Before Going Back...");
                saveDialogBuilder.setMessage("Are You Sure You want to go back to the Course Screen Without Saving? ");

                saveDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Handle click here
                    }
                });

                saveDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent toHomeScreen = new Intent(LessonFinished.this, HomeScreen.class);
                        toHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(toHomeScreen);
                        finish();
                    }
                });



                // Create and show the AlertDialog
                AlertDialog exitDialog = saveDialogBuilder.create();
                exitDialog.show();
            }
        });




    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refUsers!=null && lessonRater!=null) refUsers.removeEventListener(lessonRater);
        if (refCommunityLessons!=null && communityLessonGetter!=null) refCommunityLessons.removeEventListener(communityLessonGetter);
        if (refCommunityLessonsByUser!=null && communityLessonGetterByUser!=null) refCommunityLessonsByUser.removeEventListener(communityLessonGetterByUser);


    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refUsers!=null && lessonRater!=null) refUsers.addListenerForSingleValueEvent(lessonRater);
        if (refCommunityLessons!=null && communityLessonGetter!=null) refCommunityLessons.addListenerForSingleValueEvent(communityLessonGetter);
        if (refCommunityLessonsByUser!=null && communityLessonGetterByUser!=null) refCommunityLessonsByUser.addListenerForSingleValueEvent(communityLessonGetterByUser);



    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && lessonRater!=null) refUsers.removeEventListener(lessonRater);
        if (refCommunityLessons!=null && communityLessonGetter!=null) refCommunityLessons.removeEventListener(communityLessonGetter);
        if (refCommunityLessonsByUser!=null && communityLessonGetterByUser!=null) refCommunityLessonsByUser.removeEventListener(communityLessonGetterByUser);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyConstants.CAMERA_REQUEST_CODE)
        {

            if(resultCode == Activity.RESULT_OK){

                File f = new File(currentPhotoPath);
                dishPhotoIV.setImageURI(Uri.fromFile(f));
                dishHasPhoto = true;
            }
        }
    }


    /**
     * this function creates a File for the image picked and returns it.
     * <p>
     *
     * @param
     *
     *
     *
     * @return	File
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd__HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFL = File.createTempFile(
                imageFileName, /* prefix */
                " .jpg",       /* suffix */
                storageDir     /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = imageFL.getAbsolutePath();
        return imageFL;
    }

    private void dispatchTakePictureIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex){
                //Error occurred while creating the File

            }
            if (photoFile!= null){
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.lifesworkiguess.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, MyConstants.CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * this function asks for the permission to use the camera, if they haven't been granted.
     * if granted, the function starts the Intent for taking a photo.
     * <p>
     *
     * @param
     *
     *
     *
     * @return
     */
    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MyConstants.CAMERA_PERM_CODE);
        }
        else{
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        } else {
            Toast.makeText(this, "Camera Permission is required to use the camera!", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * this function starts the process of taking a photo with the Camera.
     * <p>
     *
     * @param view - the button pressed.
     *
     *
     *
     * @return	None
     */
    public void addDishPhoto(View view){
        askCameraPermissions();

    }

    /**
     * this function saves and uploads the rating, review, and photo inputted by the user,
     * on the condition they are valid.
     * otherwise, the function alerts the user that some input is invalid with Alert Dialog
     * <p>
     *
     * @param
     *
     *
     *
     * @return	None
     */
    public void next(View view){

        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers = FBDB.getReference("Users").child(loggedInUser.getUid());
        lessonRater = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User currentlyLoggedInUser = snapshot.getValue(User.class);
                float userRating = ratingBar.getRating();
                String userRatingSTR = String.valueOf(userRating);
                if (mode == MyConstants.PERMENANT_LESSON_INTRO)
                {
                    currentlyLoggedInUser.ratePermenantLesson(currentlyLoggedInUser.getSelectedCourse(), lessonPosition, userRatingSTR);
                    refUsers.setValue(currentlyLoggedInUser, new DatabaseReference.CompletionListener() {
                        //FIRST AND ONLY USE OF ON COMPLETE LISTENER WHEN UPDATING AN ITEM IN DATABASE, NOT REALLY NECESSARY ANYMORE BUT NO NEED TO CHANGE
                        @Override
                        public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                            if (error== null){
                                if (dishHasPhoto) {
                                    fDownRef = FirebaseStorage.getInstance().getReference().child("Users").child(loggedInUser.getUid()).
                                            child("Courses").child(currentlyLoggedInUser.getSelectedCourse()).child(lessonName);
                                    fDownRef.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        }
                                    });

                                }


                                Intent toHomeScreen = new Intent(LessonFinished.this, HomeScreen.class);
                                toHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(toHomeScreen);
                                finish();

                            }
                            else{
                                Toast.makeText(LessonFinished.this, error.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }

                else if (mode == MyConstants.COMMUNITY_LESSON_INTRO)
                {

                    String review = reviewET.getText().toString();
                    float rating = ratingBar.getRating();

                    if (rating==0 && (review!=null && !review.isEmpty()))  //User reviews but didnt rate, which i dont allow
                    {
                        //Setting up Alert Dialogs
                        AlertDialog.Builder addRatingDialogBuilder = new AlertDialog.Builder(LessonFinished.this);
                        addRatingDialogBuilder.setTitle("Please Rate this Lesson");

                        addRatingDialogBuilder.setMessage("In order to write a Review for this Lesson, You must also Rate it." +
                                "\nIf You don't want to Rate this Lesson, You can also Delete your Review.");





                        addRatingDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                        // Create and show the AlertDialog
                        AlertDialog addRatingDialog = addRatingDialogBuilder.create();
                        addRatingDialog.show();
                    }

                    else
                    {
                        //Adding the rating to the Community lesson itself
                        ArrayList<String> reviewListForLesson= new ArrayList<>();

                        reviewListForLesson.add(loggedInUser.getUid());

                        if (rating!=0) reviewListForLesson.add(Float.toString(rating));
                        else reviewListForLesson.add(MyConstants.NO_RATING_FOR_COMMUNITY_LESSON);

                        if (review!=null && !review.isEmpty())  reviewListForLesson.add(review);
                        else reviewListForLesson.add(MyConstants.NO_REVIEW_FOR_COMMUNITY_LESSON);

                        if (rating!=0 || (review!=null && !review.isEmpty()) || dishHasPhoto) //User did input at least 1 thing
                        {
                            refCommunityLessons = FBDB.getReference("Community Lessons").child(creatorID + " , " + lessonNumber);
                            communityLessonGetter = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    CommunityLesson finishedLesson = snapshot.getValue(CommunityLesson.class);

                                    finishedLesson.addReview(reviewListForLesson);
                                    refCommunityLessons.setValue(finishedLesson).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            refCommunityLessonsByUser = FBDB.getReference("Community Lessons By User").child(creatorID).child(Integer.toString(lessonNumber));
                                            communityLessonGetterByUser = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    CommunityLesson finishedLesson = snapshot.getValue(CommunityLesson.class);

                                                    finishedLesson.addReview(reviewListForLesson);
                                                    refCommunityLessonsByUser.setValue(finishedLesson).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {

                                                            //Adding the Review to the User's finished lessons Review;

                                                            ArrayList<String> reviewListForUser = new ArrayList<>();

                                                            reviewListForUser.add(creatorID);

                                                            reviewListForUser.add(Integer.toString(lessonNumber));

                                                            if (rating!=0) reviewListForUser.add(Float.toString(rating));
                                                            else reviewListForUser.add(MyConstants.NO_RATING_FOR_COMMUNITY_LESSON);

                                                            if (review!=null && !review.isEmpty())  reviewListForUser.add(review);
                                                            else reviewListForUser.add(MyConstants.NO_REVIEW_FOR_COMMUNITY_LESSON);

                                                            currentlyLoggedInUser.addFinishedCommunityLessonReview(reviewListForUser);
                                                            refUsers.setValue(currentlyLoggedInUser, new DatabaseReference.CompletionListener() {
                                                                //FIRST AND ONLY USE OF ON COMPLETE LISTENER WHEN UPDATING AN ITEM IN DATABASE, NOT REALLY NECESSARY ANYMORE BUT NO NEED TO CHANGE
                                                                @Override
                                                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                                                    if (error== null){
                                                                        if (dishHasPhoto) {
                                                                            fDownRef = FirebaseStorage.getInstance().getReference().child("Users").child(loggedInUser.getUid()).
                                                                                    child("Finished Community Lessons").child(creatorID).child(Integer.toString(lessonNumber));
                                                                            fDownRef.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                                                }
                                                                            });

                                                                        }


                                                                        Intent toCommunityScreen = new Intent(LessonFinished.this, CommunityScreen.class);
                                                                        toCommunityScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                        startActivity(toCommunityScreen);
                                                                        finish();

                                                                    }
                                                                    else{
                                                                        Toast.makeText(LessonFinished.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                                                    }

                                                                }
                                                            });
                                                        }
                                                    });



                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            };
                                            refCommunityLessonsByUser.addListenerForSingleValueEvent(communityLessonGetterByUser);

                                        }
                                    });



                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            refCommunityLessons.addListenerForSingleValueEvent(communityLessonGetter);


                        }

                        else
                        {
                            Intent toCommunityScreen = new Intent(LessonFinished.this, CommunityScreen.class);
                            toCommunityScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(toCommunityScreen);
                            finish();
                        }
                    }





                }



            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(lessonRater);




    }
}