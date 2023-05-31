/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the Adapter that is used to Reviews, Ratings, and Images of finished Community Recipes in a Recycler View.
 */


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

import com.google.android.gms.tasks.OnSuccessListener;
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

public class CustomRatingsAdapter extends RecyclerView.Adapter<CustomRatingsViewHolder>{

    Context context;
    String creatorID;
    int lessonNumber;
    ArrayList<ArrayList<String>> ratingsList;
    OnItemClickListener listener;

    DatabaseReference refUsers;
    ValueEventListener getUsername;



    public CustomRatingsAdapter(Context context, ArrayList<ArrayList<String>> ratingsList, String creatorID, int lessonNumber) {
        this.context = context;
        this.ratingsList = ratingsList;
        this.creatorID = creatorID;
        this.lessonNumber = lessonNumber;


    }

    @NonNull
    @Override
    public CustomRatingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ratings_item_view, parent, false);
        return new CustomRatingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRatingsViewHolder holder, int position) {

        ArrayList<String> currentRatingsList = ratingsList.get(position);
        String currentUserID = currentRatingsList.get(0);

        holder.ReviewRB.setEnabled(false);


        if (!currentRatingsList.get(1).equals(MyConstants.NO_RATING_FOR_COMMUNITY_LESSON)) //User rated recipe
        {
            float currentRating = Float.parseFloat(currentRatingsList.get(1));
            holder.ReviewRB.setRating(currentRating);
        }
        else //User didnt rate recipe
        {
            holder.ReviewRB.setVisibility(View.GONE);
        }

        if (!currentRatingsList.get(2).equals(MyConstants.NO_REVIEW_FOR_COMMUNITY_LESSON)) //User reviews recipe
        {
            String currentReview =currentRatingsList.get(2);
            holder.ReviewTV.setText(currentReview);
        }
        else //User didnt review recipe
        {
            holder.ReviewSV.setVisibility(View.GONE);
        }

        myServices.getProfilePhotoFromFirebase(holder.ReviewPFP, currentUserID);

        //Getting Username  (I chose not to put all of the code in the ValueEventListener even tho it means data will be shown async).
        FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
        refUsers = FBDB.getReference("Users").child(currentUserID);
        getUsername = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User currentUser = snapshot.getValue(User.class);
                String currentUsername = currentUser.getUsername();
                holder.ReviewUsernameTV.setText(currentUsername);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        refUsers.addListenerForSingleValueEvent(getUsername);

        //Getting Uploaded Dish Image
        FirebaseStorage fStorage = FirebaseStorage.getInstance();
        StorageReference fDownRef = fStorage.getReference().child("Users").child(currentUserID).
                child("Finished Community Lessons").child(creatorID).child(Integer.toString(lessonNumber));
        long MAXBYTES = 1024*1024 * 5;

        holder.ReviewUploadedDishIV.setVisibility(View.GONE);
        fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                holder.ReviewUploadedDishIV.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);

                //Fixing Rotation (CHATGPT)
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

                holder.ReviewUploadedDishIV.setImageBitmap(bitmap);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (ratingsList!=null) return ratingsList.size();
        else return 0;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
