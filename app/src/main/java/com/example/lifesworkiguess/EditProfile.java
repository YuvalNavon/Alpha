package com.example.lifesworkiguess;

import static com.example.lifesworkiguess.MyConstants.SELECT_PICTURE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditProfile extends AppCompatActivity {

    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    ValueEventListener courseGetter;
    FirebaseAuth fAuth;
    FirebaseUser loggedInUser;

    EditText emailET,passwordET,usernameET;
    ImageView PFPIV;

    Uri selectedImageUri;

    String originalEmail, originalPassword, originalUsername;
    boolean PFPChanged;


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
        myServices.getProfilePhotoFromFirebase(PFPIV);
        PFPChanged = false;


        //Setting Current User Info
        emailET = findViewById(R.id.editProfileEmailET);
        passwordET = findViewById(R.id.editProfilePasswordET);
        usernameET = findViewById(R.id.editProfileUsernameET);

        courseGetter = new ValueEventListener() {
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
        refUsers.addValueEventListener(courseGetter);






    }

    @Override
    protected void onPause() {

        super.onPause();
        if (refUsers!=null && courseGetter!=null) refUsers.removeEventListener(courseGetter);

    }


    @Override
    protected void onResume() {

        super.onResume();
        if (refUsers!=null && courseGetter!=null) refUsers.addValueEventListener(courseGetter);
    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && courseGetter!=null) refUsers.removeEventListener(courseGetter);

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
    }

    public boolean noFieldsClear(){
        return !emailET.getText().toString().isEmpty() &&
                !passwordET.getText().toString().isEmpty() &&
                !usernameET.getText().toString().isEmpty();
    }

    public void save(int mode){
        //Check if Changes were Made
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();
        String username = usernameET.getText().toString();

        if (email.equals(originalEmail) && password.equals(originalPassword) &&
                username.equals(originalUsername) && !PFPChanged ){
            Toast.makeText(this, "NO CHANGES", Toast.LENGTH_SHORT).show();
            if (mode==MyConstants.EDIT_PFP_SCREEN_SAVE_MODE){
                Intent toHomeScreen = new Intent(EditProfile.this, HomeScreen.class);
                startActivity(toHomeScreen);
            }
            else if (mode==MyConstants.EDIT_PFP_SCREEN_SAVE_AND_CHANGE_COURSE_MODE){
                Intent toChooseCourse = new Intent(EditProfile.this, ChooseCourse.class);
                toChooseCourse.putExtra(MyConstants.CHOOSE_COURSE_ORIGIN, MyConstants.FROM_PROFILE);
                startActivity(toChooseCourse);
            }

        }
        else{
            //Add Check that new Email isn't already used
            if (noFieldsClear() && myServices.emailInFormat(email) && myServices.passwordValid(password) && myServices.usernameAvailable(username))
            {

                ValueEventListener courseGetter = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        loggedInUser.updateEmail(email);
                        loggedInUser.updatePassword(password);

                        User currentlyLoggedInUser = snapshot.getValue(User.class);
                        currentlyLoggedInUser.setEmail(email);
                        currentlyLoggedInUser.setPassword(password);
                        currentlyLoggedInUser.setUsername(username);
                        refUsers.setValue(currentlyLoggedInUser);

                        if (PFPChanged) myServices.uploadProfilePhotoToFirebase(EditProfile.this, selectedImageUri);


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                // Actions to do after 1.5 second
                                if (mode==MyConstants.EDIT_PFP_SCREEN_SAVE_MODE){
                                    Intent toHomeScreen = new Intent(EditProfile.this, HomeScreen.class);
                                    startActivity(toHomeScreen);
                                }
                                else if (mode==MyConstants.EDIT_PFP_SCREEN_SAVE_AND_CHANGE_COURSE_MODE){
                                    Intent toChooseCourse = new Intent(EditProfile.this, ChooseCourse.class);
                                    toChooseCourse.putExtra(MyConstants.CHOOSE_COURSE_ORIGIN, MyConstants.FROM_PROFILE);
                                    startActivity(toChooseCourse);
                                }
                            }
                        }, 1500);







                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                refUsers.addValueEventListener(courseGetter);


            }

            else{
                if (!noFieldsClear())
                    Toast.makeText(EditProfile.this, "Please Fill all fields!", Toast.LENGTH_LONG).show();

                if (!myServices.emailInFormat(email))
                    Toast.makeText(EditProfile.this, "Please enter a Valid Email Address", Toast.LENGTH_LONG).show();

//                    if (!notInUse)
//                        Toast.makeText(EditProfile.this, "Email Address already in Use! try signing in or entering a different Email Address",
//                                Toast.LENGTH_LONG).show();


                if (!myServices.passwordValid(password))
                    Toast.makeText(EditProfile.this, "Please enter a Valid Password (longer than 6 characters)", Toast.LENGTH_LONG).show();


            }
        }
    }

    public void changePFP(View view){
        imageChooser();
    }

    public void saveChanges(View view){
          save(MyConstants.EDIT_PFP_SCREEN_SAVE_MODE);
    }

    public void changeCourse(View view){
        save(MyConstants.EDIT_PFP_SCREEN_SAVE_AND_CHANGE_COURSE_MODE);
    }

    public void signOut(View view){
        fAuth.signOut();

        //Resetting Login Info
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        SharedPreferences.Editor editor=settings.edit();
        editor.putString("Email", null);
        editor.putString("Password", null);
        editor.commit();

        //Back to MainActivity
        Intent toMain = new Intent(this, MainActivity.class);
        startActivity(toMain);

    }
}