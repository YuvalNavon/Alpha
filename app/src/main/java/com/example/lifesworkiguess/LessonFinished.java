package com.example.lifesworkiguess;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
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
import java.util.Date;

public class LessonFinished extends AppCompatActivity {

    Uri photoURI;
    ImageView dishPhotoIV;
    RatingBar ratingBar;

    String currentPhotoPath, lessonName;
    boolean dishHasPhoto;

    int lessonPosition;

    FirebaseAuth fAuth;
    FirebaseUser loggedInUser;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    StorageReference fDownRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_finished);

        dishHasPhoto = false;

        dishPhotoIV = findViewById(R.id.dishPhotoIV);
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setNumStars(5);
        ratingBar.setRating(3);

        Intent gi = getIntent();
        lessonPosition = gi.getIntExtra("Lesson Position in List", MyConstants.NO_LESSON_POSITION);
        lessonName = gi.getStringExtra("Lesson Name");



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

    public void addDishPhoto(View view){
        askCameraPermissions();

    }

    public void next(View view){

        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers = FBDB.getReference("Users").child(loggedInUser.getUid());
        ValueEventListener lessonRater = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User currentlyLoggedInUser = snapshot.getValue(User.class);
                currentlyLoggedInUser.rateLesson(lessonPosition, ratingBar.getRating());
                refUsers.setValue(currentlyLoggedInUser);

                if (dishHasPhoto) {
                    fDownRef = FirebaseStorage.getInstance().getReference().child("Users").child(loggedInUser.getUid()).
                            child("Courses").child(currentlyLoggedInUser.getSelectedCourse()).child(lessonName);
                    fDownRef.putFile(photoURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(LessonFinished.this, "Photo Uploaded!", Toast.LENGTH_LONG).show();

                        }
                    });

                }


                Intent toHomeScreen = new Intent(LessonFinished.this, HomeScreen.class);
                toHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toHomeScreen);
                finish();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(lessonRater);

//
//        Toast.makeText(LessonFinished.this, "Please wait", Toast.LENGTH_SHORT).show();
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            public void run() {
//                // Actions to do after 2 second (to let dish photo upload and rating change)
//
//              }
//            }, 2000);


    }
}