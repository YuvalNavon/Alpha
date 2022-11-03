package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraUpload extends AppCompatActivity {

    String currentPhotoPath;
    int CAMERA_PERM_CODE, CAMERA_REQUEST_CODE;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_upload);

        CAMERA_PERM_CODE = 101;
        CAMERA_REQUEST_CODE = 102;
        iv = findViewById(R.id.taken);

    }

    public void takePhoto(View view){
        askCameraPermissions();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE)
        {

            if(resultCode == Activity.RESULT_OK){
                System.out.println("WORKSSSSSSSSSSSSS 333333333333");

                File f = new File(currentPhotoPath);
              iv.setImageURI(Uri.fromFile(f));
          }
        }
    }


    private File createImageFile() throws IOException{
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
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.lifesworkiguess.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.Camera);
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
        if (item.getTitle().toString().equals("User Authentication") ){
            Intent si = new Intent(this, MainActivity.class);
            startActivity(si);
        }

        return true;
    }

}