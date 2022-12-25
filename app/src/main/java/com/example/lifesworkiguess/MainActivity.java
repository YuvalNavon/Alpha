package com.example.lifesworkiguess;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



//        emailET = findViewById(R.id.email);
//        passwordET = findViewById(R.id.password);




    }



    public void chooseCourse(View view){
        Intent si = new Intent(this, ChooseCourse.class);
        startActivity(si);
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