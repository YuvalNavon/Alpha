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

public class MainActivity extends AppCompatActivity {

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
                                Intent toHomeScreen = new Intent(MainActivity.this, HomeScreen.class);
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



    public void getStarted(View view){
        Intent toChooseCourse = new Intent(this, ChooseCourse.class);
        toChooseCourse.putExtra(MyConstants.CHOOSE_COURSE_ORIGIN, MyConstants.FROM_MAIN_ACTIVITY);
        startActivity(toChooseCourse);
    }

    public void logIn(View view){
        Intent si = new Intent(this, LogIn.class);
        startActivity(si);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.UserAuthentication);
        return true;
    }

    /**
     * Starts the CreditsScreen activity.
     * <p>
     *
     * @param	item - the MenuItem that is clicked (in this case, only the Credits Screen option).
     * @return	boolean true - mandatory
     */
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().toString().equals("Gallary Upload") ){
            Intent si = new Intent(this, GallaryChoose.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Camera") ){
            Intent si = new Intent(this, CameraUpload.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Notification") ){
            Intent si = new Intent(this, NotificationScreen.class);
            startActivity(si);
        }

        if (item.getTitle().toString().equals("Create Recipe") ){
            Intent si = new Intent(this, ExEmElFormat.class);
            startActivity(si);
        }
        if (item.getTitle().toString().equals("Test") ){
            Intent si = new Intent(this, NewCreateRecipe.class);
            startActivity(si);
        }

        return true;
    }
}