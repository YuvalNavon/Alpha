package com.example.lifesworkiguess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CompletedLessonsAdapter extends RecyclerView.Adapter<CompletedLessonsViewHolder>{

    Context context;
    String pickedCourseName;
    ArrayList<String> lessonsNames;
    ArrayList<String> lessonsRatings;
    User currentUser;
    OnItemClickListener listener;
    FirebaseUser currentlyLoggedInUser;

    public CompletedLessonsAdapter(Context context, User currentUser,  String pickedCourseName, OnItemClickListener listener) {
        this.context = context;
        this.currentUser = currentUser;
        this.pickedCourseName = pickedCourseName;
        this.listener = listener;

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        currentlyLoggedInUser = fAuth.getCurrentUser();

    }

    public CompletedLessonsAdapter(Context context, String pickedCourseName, ArrayList<String> lessonsNames, ArrayList<String> lessonsRatings, OnItemClickListener listener) {
        this.context = context;
        this.pickedCourseName = pickedCourseName;
        this.lessonsNames = lessonsNames;
        this.lessonsRatings = lessonsRatings;
        this.listener = listener;
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        currentlyLoggedInUser = fAuth.getCurrentUser();
    }

    @Override
    public CompletedLessonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.completed_lessons_items, parent, false);
        return new CompletedLessonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedLessonsViewHolder holder, int position) {
        holder.dishRatingRB.setEnabled(false);



        String lessonName = lessonsNames.get(position);
        Float lessonRating = Float.valueOf(lessonsRatings.get(position));

        holder.lessonNameTV.setText(lessonName);
        holder.dishRatingRB.setRating(lessonRating);
        holder.dishIV.setImageResource(R.drawable.add_dish);

        FirebaseStorage fStorage = FirebaseStorage.getInstance();
            StorageReference dishPhotoRef = fStorage.getReference("Users").child(currentlyLoggedInUser.getUid()).child("Courses").
                    child(pickedCourseName).child(lessonName);
            long MAXBYTES = 1024 * 1024 * 5;
            dishPhotoRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                    // Create a temporary file to save the image data
                    File tempFile = null;
                    try {
                        tempFile = File.createTempFile("tempImage", ".jpg");
                        FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
                        fileOutputStream.write(bytes);
                        fileOutputStream.close();

                        // Get the EXIF orientation information
                        ExifInterface exifInterface = new ExifInterface(tempFile.getAbsolutePath());
                        int orientation = exifInterface.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        int rotationAngle = 0;
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotationAngle = 90;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotationAngle = 180;
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotationAngle = 270;
                                break;
                            default:
                                rotationAngle = 0;
                                break;
                        }

                        // Rotate the Bitmap by the calculated rotation angle
                        Matrix matrix = new Matrix();
                        matrix.setRotate(rotationAngle);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);



                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    holder.dishIV.setImageBitmap(bitmap);
                }
            });




    }






    @Override
    public int getItemCount() {
        return lessonsNames.size();
    }


    public interface OnItemClickListener {
        void onItemClickCompletedLessons(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
