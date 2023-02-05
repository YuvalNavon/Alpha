package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class SignUp extends AppCompatActivity {

    String cookingStyle, experienceLevel, weeklyHour, email,password;
    EditText emailET, passwordET;
    TextView signInHere, emailSignUpCheck;
    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    boolean notInUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent gi = getIntent();

        cookingStyle = gi.getStringExtra("Cooking Style");
        experienceLevel = gi.getStringExtra("Experience Level");
        weeklyHour = gi.getStringExtra("Weekly Hours");

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);

        signInHere = findViewById(R.id.signInTV);
        signInHere.setPaintFlags(signInHere.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        emailSignUpCheck = findViewById(R.id.emailSignUpCheck);

        FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers=FBDB.getReference("Users");


    }

    public boolean noFieldsClear(){
        return !emailET.getText().toString().isEmpty() &&
                !passwordET.getText().toString().isEmpty();
    }



    public void emailNotInUse(String email){
        notInUse = true;

//        ValueEventListener userListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren()){
//                    User checkedUser = data.getValue(User.class);
//                    String checkedEmail = checkedUser.getEmail();
//                    if (email.equals(checkedEmail)){
//                        emailSignUpCheck.setText("IN USE");
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//        refUsers.addValueEventListener(userListener);
//        if (emailSignUpCheck.getText().toString().equals("IN USE")){
//            notInUse = false;
//            emailSignUpCheck.setText("Email address");
//        }
//        refUsers.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot data : snapshot.getChildren()){
//                    User checkedUser = data.getValue(User.class);
//                    String checkedEmail = checkedUser.getEmail();
//                    if (email.equals(checkedEmail)){
//                        showErrorEmailUsed();
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });

//        if (emailSignUpCheck.getText().toString().equals("IN USE")){
//            notInUse = false;
//
//        }
//        return notInUse;


    }

    public void showErrorEmailUsed(){
        notInUse = false;
    }



    public void Register(View view){
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        emailNotInUse(email);
        if (noFieldsClear() && myServices.emailInFormat(email ) && notInUse && myServices.passwordValid(password))
        {
            email = email.toLowerCase(Locale.ROOT);
            Intent usernameScreen = new Intent(SignUp.this, UsernameScreen.class);
            usernameScreen.putExtra("Cooking Style", cookingStyle);
            usernameScreen.putExtra("Experience Level", experienceLevel);
            usernameScreen.putExtra("Weekly Hours", weeklyHour);
            usernameScreen.putExtra("Email", email);
            usernameScreen.putExtra("Password", password);
            startActivity(usernameScreen);


        }
        else{
            if (!noFieldsClear())
                Toast.makeText(SignUp.this, "Please Fill all fields!", Toast.LENGTH_LONG).show();

            if (!myServices.emailInFormat(email))
                Toast.makeText(SignUp.this, "Please enter a Valid Email Address", Toast.LENGTH_LONG).show();

            if (!notInUse)
                Toast.makeText(SignUp.this, "Email Address already in Use! try signing in or entering a different Email Address",
                        Toast.LENGTH_LONG).show();


            if (!myServices.passwordValid(password))
                Toast.makeText(SignUp.this, "Please enter a Valid Password (longer than 6 characters)", Toast.LENGTH_LONG).show();


        }
        emailSignUpCheck.setText("Email address");


    }


    public void toLogIn(View view){
        Intent logInScreen = new Intent(this, LogIn.class);
        startActivity(logInScreen);

    }





}