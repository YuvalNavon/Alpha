package com.example.lifesworkiguess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CompletedCommunityLessonsAdapter  extends RecyclerView.Adapter<CompletedCommunityLessonsViewHolder>{

    Context context;
    ArrayList<CommunityLesson> lessons;
    ArrayList<String> creatorUsernames;
    FirebaseStorage fStorage;

    public CompletedCommunityLessonsAdapter(Context context, ArrayList<CommunityLesson> lessons, ArrayList<String> creatorUsernames) {
        this.context = context;
        this.lessons = lessons;
        this.creatorUsernames = creatorUsernames;
        fStorage = FirebaseStorage.getInstance();

    }

    @NonNull
    @Override
    public CompletedCommunityLessonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.completed_community_lessons_item, parent, false);
        return new CompletedCommunityLessonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompletedCommunityLessonsViewHolder holder, int position) {

        CommunityLesson currentLesson = lessons.get(position);
        String lessonName = currentLesson.getLessonName();
        String creatorID = currentLesson.getUserID();
        String creatorUsername = creatorUsernames.get(position);

        holder.lessonName.setText(lessonName);
        holder.creatorUsername.setText(creatorUsername);

        StorageReference storagePFPRef = fStorage.getReference("Users").child(creatorID).child(MyConstants.PROFILE_PICTURE);
        long MAXBYTES = 1024 * 1024 * 5;
        holder.creatorPFP.setImageResource(R.drawable.default_profile_picture);
        Bitmap bitmap = ((BitmapDrawable) holder.creatorPFP.getDrawable()).getBitmap();
        bitmap = myServices.getCircularBitmap(bitmap);
        holder.creatorPFP.setImageBitmap(bitmap);
        storagePFPRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
                    bitmap = myServices.getCircularBitmap(bitmap);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                holder.creatorPFP.setImageBitmap(bitmap);
            }
        });


        StorageReference storageLessonImageRef =  fStorage.getReference("Community Recipes").child(creatorID)
                .child(Integer.toString(currentLesson.getNumber())).child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);
        holder.lessonImage.setImageResource(R.drawable.add_dish_photo);
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
                holder.lessonImage.setImageBitmap(bitmap);
            }
        });

        // Set the click listeners for imageView1 and textView
        holder.lessonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toLessonIntro = new Intent(context, NewLessonIntro.class);
                toLessonIntro.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.COMMUNITY_LESSON_INTRO);
                toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, creatorID);
                toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY, creatorID);
                toLessonIntro.putExtra(MyConstants.LESSON_NAME_KEY, currentLesson.getLessonName());
                toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY, currentLesson.getDescription());
                toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, currentLesson.getNumber());

                context.startActivity(toLessonIntro);

            }
        });

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }
}
