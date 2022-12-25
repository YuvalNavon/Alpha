package com.example.lifesworkiguess;

import static com.example.lifesworkiguess.MyConstants.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsernameScreen extends AppCompatActivity {

    String cookingStyle, experienceLevel, weeklyHour, email,password, username;
    EditText usernameET;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    FirebaseAuth fAuth;
    TextView signInHere;



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
        refUsers=FBDB.getReference("Users");
        fAuth = FirebaseAuth.getInstance();

        signInHere = findViewById(R.id.signInHere2);
        signInHere.setPaintFlags(signInHere.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);


    }


    public boolean usernameAvailable(String username){
        return true;
    }

    public void finishSignUp(View view){
        username = usernameET.getText().toString();
        if (usernameAvailable(username)){
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                        Toast.makeText(UsernameScreen.this, "User Created!", Toast.LENGTH_LONG).show();
                        User newUser = new User(username, email, password, cookingStyle, experienceLevel, weeklyHour, FINISHED_SETUP);
                        refUsers.child(username).setValue(newUser);
                        Intent courseScreen = new Intent(UsernameScreen.this, HomeScreen.class );
                        courseScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(courseScreen);

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
        else{
            Toast.makeText(UsernameScreen.this, "Username not valid", Toast.LENGTH_LONG).show();

        }
    }

    public void toLogIn(View view){
        Intent logInScreen = new Intent(this, LogIn.class);
        startActivity(logInScreen);

    }

}