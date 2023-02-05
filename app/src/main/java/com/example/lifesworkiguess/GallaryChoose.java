package com.example.lifesworkiguess;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class GallaryChoose extends AppCompatActivity {

    int SELECT_PICTURE;
    ImageView iv;
    StorageReference fStorage, fRef, fDownRef;
    Uri selectedImageUri;
    String imageName;

    boolean imagePicked, imageUploaded;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallary_choose);

        fStorage = FirebaseStorage.getInstance().getReference("Profile Pictures");
        SELECT_PICTURE = 1;
        iv = findViewById(R.id.tmuna);

        imagePicked = false;
        imageUploaded = false;


    }

    void imageChooser() {

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
                    UploadImage();

                }
                else Toast.makeText(this, "Error, File was not selected", Toast.LENGTH_LONG).show();



            }
        }
    }

    public String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    public void UploadImage(){
            imageName = System.currentTimeMillis() + "." + getFileExtension(selectedImageUri);
            fRef = fStorage.child(imageName);
            fRef.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(GallaryChoose.this, "Photo Uploaded!", Toast.LENGTH_LONG).show();
                    imageUploaded = true;
                }
            });


    }

    public void PickPhoto(View view){
        imageChooser();


    }

    public void getPhotoFromFirebase(View view){

        if (imagePicked){
            if (imageUploaded){
                fDownRef = fStorage.child(imageName);
                long MAXBYTES = 1024*1024;
                fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                        iv.setImageBitmap(bitmap);
                    }
                });

            }
            else Toast.makeText(GallaryChoose.this, "DATABASE ERROR - Image not found/Uploaded yet", Toast.LENGTH_LONG).show();

        }
        else Toast.makeText(GallaryChoose.this, "Please Choose an image first", Toast.LENGTH_LONG).show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.GallaryUpload);
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
        if (item.getTitle().toString().equals("User Authentication") ){
            Intent si = new Intent(this, MainActivity.class);
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

        return true;
    }

}