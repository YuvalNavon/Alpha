package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class SignUp extends AppCompatActivity {

    String cookingStyle, experienceLevel, weeklyHour, email,password;
    EditText emailET, passwordET;
    TextView signInHere, emailErrorTV, passwordErrorTV;


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

        emailErrorTV = findViewById(R.id.SignUpEmailError);
        passwordErrorTV = findViewById(R.id.SignUpPasswordError);

        emailErrorTV.setVisibility(View.INVISIBLE);
        passwordErrorTV.setVisibility(View.INVISIBLE);


        emailET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    // The user has exited the EditText
                    String email = emailET.getText().toString();

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
                                        emailErrorTV.setText(MyConstants.INVALID_FORMAT_EMAIL_ERROR_MESSAGE );
                                    }
                                }

                                else // Email ISN'T available
                                {
                                    emailErrorTV.setVisibility(View.VISIBLE);
                                    emailErrorTV.setText(MyConstants.USED_EMAIL_ERROR_MESSAGE);
                                }
                            }
                        });
                    }

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
            }


        });

        signInHere = findViewById(R.id.signInTV);
        signInHere.setPaintFlags(signInHere.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);




    }

    public boolean noFieldsClear(){
        return !emailET.getText().toString().isEmpty() &&
                !passwordET.getText().toString().isEmpty();
    }



    public void Register(View view){
        email = emailET.getText().toString();
        password = passwordET.getText().toString();

        myServices.isEmailAvailable(email, new OnEmailCheckListener() {
            @Override
            public void onEmailCheck(boolean isAvailable) {
                if (isAvailable)   // Email IS available
                {
                    if (noFieldsClear() && myServices.emailInFormat(email ) && myServices.passwordValid(password))
                    {
                        email = email.toLowerCase(Locale.ROOT);
                        Intent usernameScreen = new Intent(SignUp.this, FinishSignUpScreen.class);
                        usernameScreen.putExtra("Cooking Style", cookingStyle);
                        usernameScreen.putExtra("Experience Level", experienceLevel);
                        usernameScreen.putExtra("Weekly Hours", weeklyHour);
                        usernameScreen.putExtra("Email", email);
                        usernameScreen.putExtra("Password", password);
                        startActivity(usernameScreen);


                    }
                    else{

                        if (!noFieldsClear())  Toast.makeText(SignUp.this, "Please Fill all fields!", Toast.LENGTH_LONG).show();

                        else if (!myServices.emailInFormat(email))  Toast.makeText(SignUp.this, MyConstants.INVALID_FORMAT_EMAIL_ERROR_MESSAGE, Toast.LENGTH_LONG).show();

                        else if (!myServices.passwordValid(password))  Toast.makeText(SignUp.this, MyConstants.PASSWORD_ERROR_MESSAGE, Toast.LENGTH_LONG).show();

                    }
                }

                else // Email ISN'T available
                {
                    Toast.makeText(SignUp.this, MyConstants.USED_EMAIL_ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                }
            }
        });




    }


    public void toLogIn(View view){
        Intent logInScreen = new Intent(this, LogIn.class);
        startActivity(logInScreen);

    }





}