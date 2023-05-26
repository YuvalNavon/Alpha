package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Locale;

public class LogIn extends AppCompatActivity {

TextView signUpTV;
EditText emailET, passwordET;
String email, password;
FirebaseAuth fAuth;

    //login via google for the first time without signing up with it must take user to profile creation screen. take username to a different screen
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        signUpTV = findViewById(R.id.signUpTV);
        signUpTV.setPaintFlags(signUpTV.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);


        emailET = findViewById(R.id.EmailLogInET);
        passwordET = findViewById(R.id.PasswordLogInET);

        fAuth = FirebaseAuth.getInstance();

    }

    public void signIn(View view){
        email = emailET.getText().toString();
        email = email.toLowerCase(Locale.ROOT);
        password = passwordET.getText().toString();


        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){

                    //Keeping Login Info
                    SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putString("Email", email);
                    editor.putString("Password", password);
                    editor.commit();


                    Toast.makeText(LogIn.this, "Sign In Successful!", Toast.LENGTH_LONG).show();
                    Intent toHomeScreen = new Intent(LogIn.this, HomeScreen.class);
                    toHomeScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toHomeScreen);
                }

                if (!task.isSuccessful()){
                    String errorMessage = task.getException().toString();
                    Toast.makeText(LogIn.this, "" + errorMessage.substring(errorMessage.indexOf(":")+2), Toast.LENGTH_LONG).show();
                }
               }
        });
    }

    public void toSignUp(View view){
        Intent toChooseCourse = new Intent(this, ChooseCourse.class);
        toChooseCourse.putExtra("Previous Activity", MyConstants.FROM_MAIN_ACTIVITY);
        startActivity(toChooseCourse);
    }
}