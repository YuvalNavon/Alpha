package com.example.lifesworkiguess;

import static com.example.lifesworkiguess.MyConstants.SELECT_PICTURE;
import static com.example.lifesworkiguess.MyConstants.USERNAME_ERROR_MESSAGE;

import androidx.activity.OnBackPressedCallback;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class EditProfile extends AppCompatActivity {

    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    ValueEventListener userInfo, courseCompletionStatus;
    FirebaseAuth fAuth;
    FirebaseUser loggedInUser;

    EditText emailET,passwordET,usernameET;
    TextView emailErrorTV, passwordErrorTV, usernameErrorTV;
    ImageView PFPIV;
    LinearLayout layout;

    Uri selectedImageUri;

    String originalEmail, originalPassword, originalUsername;
    boolean PFPChanged;
    String currentPhotoPath;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");

        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();
        refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());


        //Setting Current User Profile Picture (PFP)
        PFPIV = findViewById(R.id.editProfilePFPIV);
        myServices.getProfilePhotoFromFirebase(PFPIV, loggedInUser.getUid());
        PFPChanged = false;


        emailET = findViewById(R.id.editProfileEmailET);
        passwordET = findViewById(R.id.editProfilePasswordET);
        usernameET = findViewById(R.id.editProfileUsernameET);


        //Getting Original User Info

        userInfo = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                User currentlyLoggedUser = snapshot.getValue(User.class);

                originalEmail = currentlyLoggedUser.getEmail();
                emailET.setText(originalEmail);

                originalPassword = currentlyLoggedUser.getPassword();
                passwordET.setText(originalPassword);

                originalUsername = currentlyLoggedUser.getUsername();
                usernameET.setText(originalUsername);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(userInfo);


        //Setting up Input Error Listeners

        emailErrorTV = findViewById(R.id.EditProfile_EmailError);
        passwordErrorTV = findViewById(R.id.EditProfile_PasswordError);
        usernameErrorTV = findViewById(R.id.EditProfile_UsernameError);

        emailErrorTV.setVisibility(View.INVISIBLE);
        passwordErrorTV.setVisibility(View.INVISIBLE);
        usernameErrorTV.setVisibility(View.INVISIBLE);


        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {

                    // The user has exited the EditText

                    String email = emailET.getText().toString();

                    if (!email.equals(originalEmail)) //User has changed email, if they hadnt no checking needed
                    {
                        if (email.length()>0){ //Just so an error message isnt displayed before the user even starts writing
                            myServices.isEmailAvailable(email, new OnEmailCheckListener() {
                                @Override
                                public void onEmailCheck(boolean isAvailable) {

                                    if (isAvailable) // Email IS available, now we check if its in a valid format
                                    {
                                        if (myServices.emailInFormat(email)) emailErrorTV.setVisibility(View.INVISIBLE);

                                        else
                                        {
                                            emailErrorTV.setVisibility(View.VISIBLE);
                                            emailErrorTV.setText("Please Enter a Valid Email Address" );
                                        }
                                    }

                                    else // Email ISN'T available
                                    {
                                        emailErrorTV.setText("Email Address Already Used");
                                        emailErrorTV.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                    }

                    else emailErrorTV.setVisibility(View.INVISIBLE); //User hasnt changed email, no need for error message


                }
            }
        });

        passwordET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus)
                {
                    // The user has exited the EditText

                    String password = passwordET.getText().toString();

                    if (!password.equals(originalPassword)) //User has changed password, if they hadnt no checking needed
                    {
                        if (password.length()>0){ //Just so the error message isnt displayed before the user even starts writing
                            if (myServices.passwordValid(password))
                            {
                                passwordErrorTV.setVisibility(View.INVISIBLE);

                            }

                            else
                            {
                                passwordErrorTV.setVisibility(View.VISIBLE);
                                passwordErrorTV.setText(MyConstants.PASSWORD_ERROR_MESSAGE);
                            }
                        }
                    }

                    else passwordErrorTV.setVisibility(View.INVISIBLE); //User hasnt changed password, no need for error message

                }

            }
        });

        usernameET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus)
                {
                    // The user has exited the EditText
                    String username = usernameET.getText().toString();

                    if (!username.equals(originalUsername))  //User has changed username, if they hadnt no checking needed
                    {
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

                    else usernameErrorTV.setVisibility(View.INVISIBLE); //User hasnt changed username, no need for error message

                }

            }
        });


        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
              preSave("Pressed Back");
            }
        });
    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refUsers!=null && userInfo !=null) refUsers.removeEventListener(userInfo);
        if (refUsers!=null && courseCompletionStatus !=null) refUsers.removeEventListener(courseCompletionStatus);


    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refUsers!=null && userInfo !=null) refUsers.addListenerForSingleValueEvent(userInfo);
        if (refUsers!=null && courseCompletionStatus !=null) refUsers.addListenerForSingleValueEvent(courseCompletionStatus);

    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && userInfo !=null) refUsers.removeEventListener(userInfo);
        if (refUsers!=null && courseCompletionStatus !=null) refUsers.removeEventListener(courseCompletionStatus);


    }


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

                    PFPIV.setImageURI(selectedImageUri);
                    Bitmap bm = ((BitmapDrawable) PFPIV.getDrawable()).getBitmap();
                    PFPIV.setImageBitmap(myServices.getCircularBitmap(bm));
                    PFPChanged = true;

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
                PFPIV.setImageURI(selectedImageUri);
                PFPChanged = true;


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
                selectedImageUri = FileProvider.getUriForFile(this,
                        "com.example.lifesworkiguess.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
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

    public boolean noFieldsClear(){
        return !emailET.getText().toString().isEmpty() &&
                !passwordET.getText().toString().isEmpty() &&
                !usernameET.getText().toString().isEmpty();
    }

    public boolean noChangesMade(String email, String password, String username){
        return email.equals(originalEmail) && password.equals(originalPassword) &&
                username.equals(originalUsername) && !PFPChanged;
    }



    public void makeCourseChangeDialog(){
        fAuth = FirebaseAuth.getInstance();
        loggedInUser = fAuth.getCurrentUser();
        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
        courseCompletionStatus = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);

                //Setting up Alert Dialogs
                AlertDialog.Builder changeCourseDialogBuilder = new AlertDialog.Builder(EditProfile.this);
                changeCourseDialogBuilder.setTitle("Are you Sure?");

                if (currentUser.hasFinishedCourse()) //User HAS FINISHED COURSE
                {
                    changeCourseDialogBuilder.setMessage(
                            "Because You Finished this Course, its Progress will be Saved" +
                                    " in your History tab in the Profile screen.\n\nAre you Sure you Want to Change Course?");
                }

                else //User HASNT FINISHED COURSE
                {
                    changeCourseDialogBuilder.setMessage(
                            "Because You Didn't Finish this Course, its Progress WILL NOT be Saved " +
                                    "in your History tab in the Profile screen.\n\nAre you Sure you Want to Change Course?");
                }

                changeCourseDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                    }
                });


                changeCourseDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent toChooseCourse = new Intent(EditProfile.this, ChooseCourse.class);
                        toChooseCourse.putExtra(MyConstants.CHOOSE_COURSE_ORIGIN, MyConstants.FROM_PROFILE);
                        startActivity(toChooseCourse);
                    }
                });

                // Create and show the AlertDialog
                AlertDialog changeCourseDialog = changeCourseDialogBuilder.create();
                changeCourseDialog.show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(courseCompletionStatus);

    }

    public void disableClickableViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                disableClickableViews(child);
            }
        } else {
            view.setEnabled(false);
        }
    }

    public void enableClickableViews(View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                enableClickableViews(child);
            }
        } else {
            view.setEnabled(true);
        }
    }




    public void save(int mode){

        View rootView = findViewById(R.id.EditProfileLL);
        disableClickableViews(rootView);

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String username = usernameET.getText().toString();

        if (noChangesMade(email, password, username))
        {
            Toast.makeText(this, "No changes", Toast.LENGTH_SHORT).show();
        }

        else
        {
            myServices.isEmailAvailable(email, new OnEmailCheckListener() {
                @Override
                public void onEmailCheck(boolean isAvailable) {

                    if (isAvailable || email.equals(originalEmail)) // Email IS available
                    {
                        myServices.isUsernameAvailable(username, new OnUsernameCheckListener() {
                            @Override
                            public void onUsernameCheck(boolean isAvailable) {

                                if (isAvailable || username.equals(originalUsername)) //Username IS Available
                                {
                                    if (noFieldsClear() && myServices.emailInFormat(email) && myServices.passwordValid(password))
                                    {


                                         userInfo = new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                loggedInUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful())
                                                        {
                                                            loggedInUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        originalEmail = email;
                                                                        originalPassword = password;
                                                                        originalUsername = username;

                                                                        User currentlyLoggedInUser = snapshot.getValue(User.class);
                                                                        currentlyLoggedInUser.setEmail(email);
                                                                        currentlyLoggedInUser.setPassword(password);
                                                                        currentlyLoggedInUser.setUsername(username);
                                                                        refUsers.setValue(currentlyLoggedInUser);

                                                                        //Keeping Login Info
                                                                        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor=settings.edit();
                                                                        editor.putString(MyConstants.LOGIN_EMAIL, email);
                                                                        editor.putString(MyConstants.LOGIN_PASSWORD, password);
                                                                        editor.commit();


                                                                        if (PFPChanged) //This is myServices.uploadProfilePhotoToFirebase but i didnt use it bc
                                                                            //i want the screen to change as soon as the image is uploaded
                                                                        {
                                                                            FirebaseStorage fStorage = FirebaseStorage.getInstance();
                                                                            StorageReference fDownRef = fStorage.getReference("Users")
                                                                                    .child(loggedInUser.getUid()).child(MyConstants.PROFILE_PICTURE);
                                                                            fDownRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                                @Override
                                                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                                    PFPChanged = false;
                                                                                    if (mode==MyConstants.EDIT_PFP_SCREEN_SAVE_MODE)
                                                                                    {
                                                                                        finish();
                                                                                    }

                                                                                    else if(mode==MyConstants.EDIT_PFP_SCREEN_SAVE_AND_CHANGE_COURSE_MODE)
                                                                                    {
                                                                                        makeCourseChangeDialog();
                                                                                    }
                                                                                }

                                                                            });
                                                                        }

                                                                        else
                                                                        {
                                                                            if (mode==MyConstants.EDIT_PFP_SCREEN_SAVE_MODE)
                                                                            {
                                                                                finish();
                                                                            }

                                                                            else if(mode==MyConstants.EDIT_PFP_SCREEN_SAVE_AND_CHANGE_COURSE_MODE)
                                                                            {
                                                                                View rootView = findViewById(R.id.EditProfileLL);
                                                                                enableClickableViews(rootView);
                                                                                makeCourseChangeDialog();
                                                                            }
                                                                        }




                                                                    }

                                                                    else
                                                                    {

                                                                        View rootView = findViewById(R.id.EditProfileLL);
                                                                        enableClickableViews(rootView);
                                                                        Toast.makeText(EditProfile.this, task.getException().toString().substring(120), Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        else
                                                        {

                                                            View rootView = findViewById(R.id.EditProfileLL);
                                                            enableClickableViews(rootView);

                                                        }
                                                    }
                                                });




                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        };
                                        refUsers.addListenerForSingleValueEvent(userInfo);


                                    }

                                    else
                                    {

                                        View rootView = findViewById(R.id.EditProfileLL);
                                        enableClickableViews(rootView);

                                        if (!noFieldsClear())
                                            Toast.makeText(EditProfile.this, "Please Fill all fields!", Toast.LENGTH_LONG).show();

                                        else if (!myServices.emailInFormat(email))
                                            Toast.makeText(EditProfile.this, MyConstants.INVALID_FORMAT_EMAIL_ERROR_MESSAGE, Toast.LENGTH_LONG).show();

                                        else if (!myServices.passwordValid(password))
                                            Toast.makeText(EditProfile.this, MyConstants.PASSWORD_ERROR_MESSAGE, Toast.LENGTH_LONG).show();


                                    }
                                }

                                else //Username ISN'T Available
                                {

                                    View rootView = findViewById(R.id.EditProfileLL);
                                    enableClickableViews(rootView);
                                    Toast.makeText(EditProfile.this, USERNAME_ERROR_MESSAGE, Toast.LENGTH_LONG).show();

                                }
                            }
                        });

                    }

                    else  // Email ISN'T available
                    {

                        View rootView = findViewById(R.id.EditProfileLL);
                        enableClickableViews(rootView);
                        Toast.makeText(EditProfile.this, MyConstants.USED_EMAIL_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();

                    }
                }
            });


        }
    }

    public void changePFP(View view){

        AlertDialog.Builder selectPictureDialogBuilder = new AlertDialog.Builder(EditProfile.this);

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
        selectPictureDialog.show();    }


    public void preSave(String origin){


        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String username = usernameET.getText().toString();



        if (noChangesMade(email, password, username))
        {
            finish();
        }

        else
        {
            AlertDialog.Builder confirmChangesBuilder = new AlertDialog.Builder(this);

            if (origin.equals("Pressed Save"))
            {
                confirmChangesBuilder.setTitle("Confirm Changes");
                confirmChangesBuilder.setMessage("Are You Sure You want to Save these Changes?");
            }

            else if (origin.equals("Pressed Back"))
            {
                confirmChangesBuilder.setTitle("Before Going Back...");
                confirmChangesBuilder.setMessage("We noticed You made some Changes.\n Would You like to Save your Changes?");
            }

            confirmChangesBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                }
            });


            confirmChangesBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // Handle click here
                    save(MyConstants.EDIT_PFP_SCREEN_SAVE_MODE);

                }
            });

            AlertDialog confirmChangesDialog = confirmChangesBuilder.create();
            confirmChangesDialog.show();



        }

    }

    public void saveChanges(View view){

        preSave("Pressed Save");
    }

    public void changeCourse(View view){
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String username = usernameET.getText().toString();


        if (noChangesMade(email, password, username))
        {
            makeCourseChangeDialog();
        }

        else
        {
            AlertDialog.Builder confirmChangesBuilder = new AlertDialog.Builder(this);

            confirmChangesBuilder.setTitle("Confirm Changes");
            confirmChangesBuilder.setMessage("You have some Unsaved Changes.\nWould You like to Save before Changing Course?");

            confirmChangesBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    makeCourseChangeDialog();

                }
            });


            confirmChangesBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {

                    // Handle click here
                    save(MyConstants.EDIT_PFP_SCREEN_SAVE_AND_CHANGE_COURSE_MODE); //We makeCourseChangeDialog there



                }
            });

            AlertDialog confirmChangesDialog = confirmChangesBuilder.create();
            confirmChangesDialog.show();
        }








    }

    public void signOut(View view){

        AlertDialog.Builder signOutBuilder = new AlertDialog.Builder(this);

        signOutBuilder.setTitle("Before You Go...");
        signOutBuilder.setMessage("Are You Sure You Want to Sign Out?");


        signOutBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });


        signOutBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                // Handle click here
                fAuth.signOut();

                //Resetting Login Info
                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                SharedPreferences.Editor editor=settings.edit();
                editor.putString("Email", null);
                editor.putString("Password", null);
                editor.commit();

                //Back to StartScreen
                Intent toMain = new Intent(EditProfile.this, StartScreen.class);
                toMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toMain);

            }
        });

        AlertDialog signOutDialog = signOutBuilder.create();
        signOutDialog.show();




    }
}