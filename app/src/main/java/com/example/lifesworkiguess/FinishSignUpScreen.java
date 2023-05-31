/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is where the user can finish their Sign Up Process, by choosing a username and
 * profile picture.
 */

package com.example.lifesworkiguess;

import static com.example.lifesworkiguess.MyConstants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class FinishSignUpScreen extends AppCompatActivity {

    String cookingStyle, experienceLevel, weeklyHour, email,password, username;
    EditText usernameET;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers, refLessons;
    ValueEventListener lessonLoader;
    FirebaseAuth fAuth;
    TextView signInHere, addPFP, usernameErrorTV;

    ImageView iv;
    StorageReference fStorage;
    Uri selectedImageUri;

    boolean imagePicked, imageUploaded;
    String currentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username_screen);

        Intent gi = getIntent();

        cookingStyle = gi.getStringExtra("Cooking Style");
        experienceLevel = gi.getStringExtra("Experience Level");
        weeklyHour = gi.getStringExtra("Weekly Hours");
        email = gi.getStringExtra("Email");
        password = gi.getStringExtra("Password");

        usernameET = findViewById(R.id.usernameET);

        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users"); //Not directly referencing current user bc the user still wasnt made lolololol
        fAuth = FirebaseAuth.getInstance();

        signInHere = findViewById(R.id.signInHere2);
        signInHere.setPaintFlags(signInHere.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);


        fStorage = FirebaseStorage.getInstance().getReference("Profile Pictures");
        iv = findViewById(R.id.addPFPIV);


        imagePicked = false;
        imageUploaded = false;

        addPFP = findViewById(R.id.addPFPTV);


        //For Error Checks
        usernameErrorTV = findViewById(R.id.UsernameScreen_UsernameError);
        usernameErrorTV.setVisibility(View.INVISIBLE);

        usernameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus)
                {
                    // The user has exited the EditText
                    String username = usernameET.getText().toString();


                        myServices. isUsernameAvailable(username, new OnUsernameCheckListener() {
                            @Override
                            public void onUsernameCheck(boolean isAvailable) {

                                if (isAvailable)  //Username IS Available
                                {
                                    usernameErrorTV.setVisibility(View.INVISIBLE);
                                }

                                else   //Username ISN'T Available
                                {
                                    usernameErrorTV.setVisibility(View.VISIBLE);
                                    usernameErrorTV.setText(MyConstants.USERNAME_ERROR_MESSAGE);

                                }
                            }
                        });
                    }

            }
        });


    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refLessons!=null && lessonLoader!=null) refUsers.removeEventListener(lessonLoader);

    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refLessons!=null && lessonLoader!=null) refUsers.addValueEventListener(lessonLoader);
    }

    public void onDestroy() {

        super.onDestroy();
        if (refLessons!=null && lessonLoader!=null) refUsers.removeEventListener(lessonLoader);

    }


    /**
     * this function starts the process of picking an image from the Gallery.
     * <p>
     *
     * @param
     *
     *
     *
     * @return	None
     */
    public void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //FOR GALLERY
        if (resultCode == RESULT_OK) {

            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {

                    imagePicked = true;
                    iv.setImageURI(selectedImageUri);
                    Bitmap bm = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                    iv.setImageBitmap(myServices.getCircularBitmap(bm));
                    addPFP.setText("Your Profile Picture");

                }
                else Toast.makeText(this, "Error, File was not selected", Toast.LENGTH_LONG).show();



            }
        }

        //FOR CAMERA
        if (requestCode == MyConstants.CAMERA_REQUEST_CODE)
        {

            if(resultCode == Activity.RESULT_OK){

                File f = new File(currentPhotoPath);
                selectedImageUri = Uri.fromFile(f);
                iv.setImageURI(selectedImageUri);
                addPFP.setText("Your Profile Picture");


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
                selectedImageUri = FileProvider.getUriForFile(this,
                        "com.example.lifesworkiguess.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
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

    public void selectPFP(View view){
        AlertDialog.Builder selectPictureDialogBuilder = new AlertDialog.Builder(FinishSignUpScreen.this);

        selectPictureDialogBuilder.setTitle("Choose Photo");


        selectPictureDialogBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });


        selectPictureDialogBuilder.setNegativeButton("Use Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                askCameraPermissions();
            }
        });

        selectPictureDialogBuilder.setPositiveButton("From Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                imageChooser();

            }
        });

        AlertDialog selectPictureDialog = selectPictureDialogBuilder.create();
        selectPictureDialog.show();
    }


    /**
     * if all of the input from the user is valid, this function creates a User for the user and uploads its details to Firebase
     * otherwise, this function alerts the user about invalid input.
     * <p>
     *
     * @param view - the button pressed
     *
     *
     *
     * @return
     */
    public void finishSignUp(View view){
        username = usernameET.getText().toString();

        myServices.isUsernameAvailable(username, new OnUsernameCheckListener() {
            @Override
            public void onUsernameCheck(boolean isAvailable) {

                if (isAvailable) //Username IS Available
                {
                    fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){


                                //Keeping Login Info
                                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                SharedPreferences.Editor editor=settings.edit();
                                editor.putString(MyConstants.LOGIN_EMAIL, email);
                                editor.putString(MyConstants.LOGIN_PASSWORD, password);
                                editor.commit();

                                //Logging in
                                fAuth.signInWithEmailAndPassword(email, password);
                                FirebaseUser currentUser = fAuth.getCurrentUser();

                                //Checking if User Uploaded a PFP (ADD DIALOG BOX ALERTING USER TO UPLOAD HIS OWN PROFILE PICTURE)
                                if (selectedImageUri==null){
                                    int defaultPFPResourceId = getResources().getIdentifier("default_profile_picture", "drawable", getPackageName());
                                    selectedImageUri = Uri.parse("android.resource://" + getPackageName() + "/" + defaultPFPResourceId);
                                }
                                FirebaseStorage fStorage = FirebaseStorage.getInstance();
                                StorageReference fDownRef = fStorage.getReference("Users").child(currentUser.getUid()).child(MyConstants.PROFILE_PICTURE);
                                fDownRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Toast.makeText(FinishSignUpScreen.this, "User Created!", Toast.LENGTH_LONG).show();
                                        User newUser = new User(username, email, password, cookingStyle, experienceLevel, weeklyHour, FINISHED_SETUP);
                                        //GETTING LESSON NUMBER
                                        refLessons = FBDB.getReference("Courses").child(newUser.getSelectedCourse());
                                        lessonLoader = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                long lessonCount = snapshot.getChildrenCount();
                                                for (int i = 0; i<lessonCount; i++){

                                                    newUser.getLessonsStatus().add(0);
                                                    newUser.getLessonsRating().get(0).add("0"); //WE ADD THE FIRST SELECTED COURSE NAME IN THE USER CONSTRUCTOR

                                                }
                                                refUsers.child(currentUser.getUid()).setValue(newUser);
                                                Intent courseScreen = new Intent(FinishSignUpScreen.this, HomeScreen.class );
                                                courseScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(courseScreen);

                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        };
                                        refLessons.addValueEventListener(lessonLoader);

                                    }
                                });


                            }
                            else if(!task.isSuccessful()){
                                String errorMessage = task.getException().toString();
                                Toast.makeText(FinishSignUpScreen.this,
                                        ""+ errorMessage.substring(errorMessage.indexOf(":")+2),
                                        Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                }

                else //Username ISN'T Available
                {
                    Toast.makeText(FinishSignUpScreen.this, USERNAME_ERROR_MESSAGE, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    /**
     * this function starts the LogIn Activity.
     * <p>
     *
     * @param view - the button pressed
     *
     *
     *
     * @return
     */
    public void toLogIn(View view){
        Intent logInScreen = new Intent(this, LogIn.class);
        startActivity(logInScreen);

    }

}