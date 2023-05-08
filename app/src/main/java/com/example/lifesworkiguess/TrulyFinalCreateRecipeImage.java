package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TrulyFinalCreateRecipeImage extends AppCompatActivity {

    //From General
    String recipeName, recipeDescription;

    //From this
    ImageView recipeImage;
    Uri recipeImageUri;
    String currentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truly_final_create_recipe_image);

        recipeImage = findViewById(R.id.IVCreateRecipe);

        //getting saved image

        getSavedImage();




        Intent gi = getIntent();
        //We check if the user got to this activity from the finish screen or from the activity before this one

        if (gi.getStringExtra("Previous Activity").equals(MyConstants.FROM_FINISH_SCREEN)){
            //Right now I do not allow users to edit by pressing on items from the finish screen, so  this will remain empty for now
        }


        else if (gi.getStringExtra("Previous Activity").equals(MyConstants.NOT_FROM_FINISH_SCREEN)){
            //From General
            recipeName =  gi.getStringExtra(MyConstants.CUSTOM_RECIPE_NAME);
            recipeDescription = gi.getStringExtra(MyConstants.CUSTOM_RECIPE_DESCRIPTION);

        }



    }

    public void onDestroy() {

        super.onDestroy();
        //The recipeImageUri is deleted when the user finishes the recipe, either by uploading it or by going back to the community screen
        if (recipeImageUri !=null){

            saveRecipeImage();

        }

    }

    public void saveRecipeImage(){


        //DIFFERENT WAY - SAVING THE PICKED IMAGE TO MY APP FOLDER (ONLY REQUIRED FOR IMAGES FROM GALLERY, NOT CAMERA):

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), recipeImageUri);
            FileOutputStream fos = openFileOutput(MyConstants.IMAGE_FILE_NAME, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getSavedImage(){
        // Check if the image file already exists in the app's internal storage directory
        File SelectedImageFile = new File(getFilesDir(), MyConstants.IMAGE_FILE_NAME);
        if (SelectedImageFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(SelectedImageFile.getAbsolutePath());
            recipeImage.setImageBitmap(bitmap);        }
    }


    //GALLERY

    public void PickPhoto(View view){
        imageChooser();


    }

    void imageChooser() {

        // create an instance of the
        // intent of the type image
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, MyConstants.REQUEST_CODE_OPEN_DOCUMENT);
    }


    // this function is triggered when user
    // selects the image from the imageChooser
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //THIS METHOD IS USED BOTH BY PICKING FROM GALLERY AND BY TAKING A PICTURE WITH CAMERA, I CANT HAVE THE SAME METHOD WRITTEN TWICE SO I PUT THE CONTENTS OF
        //EACH METHOD IN THIS ONE METHOD AND DIVIDED IT BY NOTE

        //FOR GALLERY
        if (requestCode == MyConstants.REQUEST_CODE_OPEN_DOCUMENT && resultCode == RESULT_OK) {

            // Get the url of the image from data
            recipeImageUri = data.getData();
            if (null != recipeImageUri) {

                recipeImage.setImageURI(recipeImageUri);

//                try
//                {
//                    if (myServices.isFileExists(this, MyConstants.IMAGE_FILE_NAME))
//                        myServices.deleteFile(this, MyConstants.IMAGE_FILE_NAME);
//
//                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), recipeImageUri);
//                    File file = new File(getFilesDir(), MyConstants.IMAGE_FILE_NAME);
//                    FileOutputStream out = new FileOutputStream(file);
//                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    out.flush();
//                    out.close();
//                }
//
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }

            }
            else Toast.makeText(this, "Error, File was not selected", Toast.LENGTH_LONG).show();




        }


        //FOR CAMERA
        if (requestCode == MyConstants.CAMERA_REQUEST_CODE)
        {

            if(resultCode == Activity.RESULT_OK){

                File f = new File(currentPhotoPath);
                recipeImage.setImageURI(Uri.fromFile(f));

//                Bundle extras = data.getExtras();
//                Bitmap imageBitmap = (Bitmap) extras.get("data");
//                try {
//                    if (myServices.isFileExists(this, MyConstants.IMAGE_FILE_NAME))
//                        myServices.deleteFile(this, MyConstants.IMAGE_FILE_NAME);
//                    File file = new File(getFilesDir(), MyConstants.IMAGE_FILE_NAME);
//                    FileOutputStream out = new FileOutputStream(file);
//                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    out.flush();
//                    out.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
    }



    //CAMERA

    public void takePhoto(View view){
        askCameraPermissions();
    }



    private File createImageFile() throws IOException {
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
                recipeImageUri = FileProvider.getUriForFile(this,
                        "com.example.lifesworkiguess.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, recipeImageUri);
                startActivityForResult(takePictureIntent, MyConstants.CAMERA_REQUEST_CODE);
            }
        }
    }

    private void askCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MyConstants.CAMERA_PERM_CODE);
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



    //BACK TO NORMAL
    public void next(View view){

        if (recipeImageUri !=null){

            saveRecipeImage();

        }

        Intent toAddRecipeIngredients = new Intent(this, TrulyFinalCreateRecipeIngredients.class);
        toAddRecipeIngredients.putExtra("Previous Activity", MyConstants.NOT_FROM_FINISH_SCREEN);

        //From General
        toAddRecipeIngredients.putExtra(MyConstants.CUSTOM_RECIPE_NAME, recipeName);
        toAddRecipeIngredients.putExtra(MyConstants.CUSTOM_RECIPE_DESCRIPTION, recipeDescription);


        //From this no need to save anything in intent, recipeImage is saved to files.


        startActivity(toAddRecipeIngredients);

    }

    public void back(View view){
        finish();

    }





}