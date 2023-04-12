package com.example.lifesworkiguess;

import static com.example.lifesworkiguess.MyConstants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
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

public class UsernameScreen extends AppCompatActivity {

    String cookingStyle, experienceLevel, weeklyHour, email,password, username;
    EditText usernameET;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers, refLessons;
    ValueEventListener lessonLoader;
    FirebaseAuth fAuth;
    TextView signInHere, addPFP;

    ImageView iv;
    StorageReference fStorage;
    Uri selectedImageUri;

    boolean imagePicked, imageUploaded;



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
//        //FIX NOT ROUNDING IMAGE
//        Bitmap bm = ((BitmapDrawable) iv.getDrawable()).getBitmap();
//        iv.setImageBitmap(myServices.getCircularBitmap(bm));
//        //FIX NOT ROUNDING IMAGE

        imagePicked = false;
        imageUploaded = false;

        addPFP = findViewById(R.id.addPFPTV);

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

                    imagePicked = true;
                    iv.setImageURI(selectedImageUri);
                    Bitmap bm = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                    iv.setImageBitmap(myServices.getCircularBitmap(bm));
                    addPFP.setText("Your Profile Picture");

                }
                else Toast.makeText(this, "Error, File was not selected", Toast.LENGTH_LONG).show();



            }
        }
    }




    public void selectPFP(View view){
        imageChooser();

    }

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
                                        Toast.makeText(UsernameScreen.this, "Photo Uploaded!", Toast.LENGTH_LONG).show();
                                        Toast.makeText(UsernameScreen.this, "User Created!", Toast.LENGTH_LONG).show();
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
                                                Intent courseScreen = new Intent(UsernameScreen.this, HomeScreen.class );
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
                                Toast.makeText(UsernameScreen.this,
                                        ""+ errorMessage.substring(errorMessage.indexOf(":")+2),
                                        Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                }

                else //Username ISN'T Available
                {
                    Toast.makeText(UsernameScreen.this, "Username not valid", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void toLogIn(View view){
        Intent logInScreen = new Intent(this, LogIn.class);
        startActivity(logInScreen);

    }

}