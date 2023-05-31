/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Activity is the First Activity a new user will experience.
 * in it, the user can start the SignUp or LogIn process.
 */


package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class StartScreen extends AppCompatActivity {

    FirebaseAuth fAuth;
    LinearLayout mainBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //To make Loading seem smooth 1
        mainBackground = findViewById(R.id.mainBackground);
        mainBackground.setVisibility(View.INVISIBLE);

        fAuth = FirebaseAuth.getInstance();
        SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
        String email = settings.getString(MyConstants.LOGIN_EMAIL, null);
        String password = settings.getString(MyConstants.LOGIN_PASSWORD, null);
        if ( email!=null && email.length()!=0 && password!=null && password.length()!=0){
            fAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // User exists
                                Intent toHomeScreen = new Intent(StartScreen.this, HomeScreen.class);
                                startActivity(toHomeScreen);
                            }
                            else {
                                //Resetting Login Info
                                SharedPreferences settings=getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                                SharedPreferences.Editor editor=settings.edit();
                                editor.putString("Email", null);
                                editor.putString("Password", null);
                                editor.commit();

                                //To make Loading seem smooth 2
                                mainBackground.setVisibility(View.VISIBLE);

                            }
                            }
                        });

        }

        else  mainBackground.setVisibility(View.VISIBLE);





    }



    /**
     * this function starts the ChooseCourse Activity.
     * @param view - the button pressed.
     *
     *
     * @return
     */
    public void getStarted(View view){
        Intent toChooseCourse = new Intent(this, ChooseCourse.class);
        toChooseCourse.putExtra(MyConstants.CHOOSE_COURSE_ORIGIN, MyConstants.FROM_MAIN_ACTIVITY);
        startActivity(toChooseCourse);
    }


    /**
     * this function starts the LogIn Activity.
     * @param view - the button pressed.
     *
     *
     * @return
     */
    public void logIn(View view){
        Intent si = new Intent(this, LogIn.class);
        startActivity(si);
    }


}