package com.example.lifesworkiguess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CommunityLessonCustomAdapter extends RecyclerView.Adapter<CommunityLessonViewHolder>  {

    Context context;
    ArrayList<CommunityLesson> communityLessonsList;
    ArrayList<String> userIDsList, usernamesList;
    OnItemClickListener listener;


    public CommunityLessonCustomAdapter(Context context, ArrayList<CommunityLesson> communityLessonsList,
                                        ArrayList<String> userIDsList, ArrayList<String> usernamesList,  OnItemClickListener listener) {
        this.context = context;
        this.communityLessonsList = communityLessonsList;
        this.userIDsList = userIDsList;
        this.usernamesList = usernamesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommunityLessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.found_community_lesson_item, parent, false);
        return new CommunityLessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityLessonViewHolder holder, int position) {

        CommunityLesson currLesson = communityLessonsList.get(position);
        String lessonName = currLesson.getLessonName();
        String lessonTime = currLesson.getTime();
        String lessonDifficulty = currLesson.getDifficulty();
        boolean lessonKosher = currLesson.isKosher();

        String userID = userIDsList.get(position);
        String username = usernamesList.get(position);

        ArrayList<ArrayList<String>> ratingsList = currLesson.getRatings();
        int ratingCount = 0;
        float averageRating = 2;

        holder.lessonRB.setEnabled(false);
        holder.lessonRB.setNumStars(5);

        if (ratingsList!= null && !ratingsList.isEmpty())
        {

            float sumRating = 0;
            int countOfReviewListsThatMeanUserOnlyUploadedPhotoAndDidntRateAndReview = 0;
            for (int i = 0; i<ratingsList.size(); i++)
            {
                   ArrayList<String> currentRating  = ratingsList.get(i);
                   if (!currentRating.get(1).equals(MyConstants.NO_RATING_FOR_COMMUNITY_LESSON))
                   {
                       sumRating+= Float.parseFloat(currentRating.get(1));
                   }
                   else
                   {
                       countOfReviewListsThatMeanUserOnlyUploadedPhotoAndDidntRateAndReview+=1;

                   }

            }

            ratingCount = ratingsList.size() - countOfReviewListsThatMeanUserOnlyUploadedPhotoAndDidntRateAndReview;
            if (ratingCount!=0)
            {
                averageRating = sumRating/ratingCount;
                holder.lessonRatingCountTV.setText(Integer.toString(ratingCount));
                holder.lessonRB.setRating(averageRating);
            }
            else
            {
                holder.lessonRatingCountTV.setText("No Ratings Yet");
                holder.lessonRB.setVisibility(View.GONE);
            }
        }
        else
        {
            holder.lessonRatingCountTV.setText("No Ratings Yet");
            holder.lessonRB.setVisibility(View.GONE);
        }

        holder.lessonNameTV.setText(lessonName);
        holder.lessonTimeTV.setText(lessonTime);
        holder.lessonDifficultyTV.setText(lessonDifficulty);
        if (lessonKosher) {
            holder.lessonKosherTV.setText("KOSHER");
        }
        else{
            holder.lessonKosherTV.setText("NOT\nKOSHER");
        }
        holder.usernameTV.setText(username);




        FirebaseStorage FBStorage = FirebaseStorage.getInstance();
        StorageReference storagePFPRef = FBStorage.getReference("Users").child(userID).child(MyConstants.PROFILE_PICTURE);
        long MAXBYTES = 1024 * 1024 * 5;
        holder.userIV.setImageResource(R.drawable.default_profile_picture);
        Bitmap bitmap = ((BitmapDrawable) holder.userIV.getDrawable()).getBitmap();
        bitmap = myServices.getCircularBitmap(bitmap);
        holder.userIV.setImageBitmap(bitmap);
        storagePFPRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = myServices.getCircularBitmap(bitmap);
                holder.userIV.setImageBitmap(bitmap);
            }
        });


        StorageReference storageLessonImageRef =  FBStorage.getReference("Community Recipes").child(userID)
                .child(Integer.toString(currLesson.getNumber())).child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);
        holder.lessonIV.setImageResource(R.drawable.add_dish_photo);
        storageLessonImageRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                holder.lessonIV.setImageBitmap(bitmap);
            }
        });


        holder.setOnItemClickListener(new CommunityLessonViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick2(int position) {
                if (listener != null) {
                    listener.onItemClick2(position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return communityLessonsList.size();
    }

    public interface OnItemClickListener {
        void onItemClick2(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
