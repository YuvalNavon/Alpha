package com.example.lifesworkiguess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.util.ArrayList;

public class CompletedLessonsAdapter extends RecyclerView.Adapter<CompletedLessonsViewHolder>{

    Context context;
    Course pickedCourse;
    User currentUser;
    OnItemClickListener listener;
    FirebaseUser currentlyLoggedInUser;

    public CompletedLessonsAdapter(Context context, User currentUser,  Course pickedCourse, OnItemClickListener listener) {
        this.context = context;
        this.currentUser = currentUser;
        this.pickedCourse = pickedCourse;
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

//        if (currentUser.getLessonsStatus().get(position)==MyConstants.FINISHED_LESSON){
//            String lessonName = pickedCourse.getLessonsList().get(position).getLessonName();
//            holder.lessonNameTV.setText(lessonName);
//            FirebaseStorage fStorage = FirebaseStorage.getInstance();
//            StorageReference dishPhotoRef = fStorage.getReference("Users").child(currentlyLoggedInUser.getUid()).child("Courses").
//                    child(pickedCourse.getCourseName()).child(lessonName);
//            long MAXBYTES = 1024 * 1024;
//            dishPhotoRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                    holder.dishIV.setImageBitmap(bitmap);
//                }
//            });
//            dishPhotoRef.getBytes(MAXBYTES).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    System.out.println("HELL YEAH FAILURE");
//                    holder.dishIV.setImageResource(R.drawable.add_dish);
//                }
//            });
//
////            String lessonRating = currentUser.getLessonsRating().get(position);
////            holder.dishRatingRB.setRating(lessonRating);
//            holder.dishRatingRB.setEnabled(false);
//
//
//
//
//            holder.setOnItemClickListener(new CompletedLessonsViewHolder.OnItemClickListener() {
//                @Override
//                public void onItemClick2(int position) {
//                    if (listener != null) {
//                        listener.onItemClick2(position);
//                    }
//                }
//            });
//        }
        String pickedCourseName = pickedCourse.getCourseName();

        //IF USER PICKED THE CURRENT COURSE
        if (pickedCourseName.equals(currentUser.getSelectedCourse())){
            ArrayList<Integer> lessonStatus = currentUser.getLessonsStatus();
            if (lessonStatus.get(position)==MyConstants.FINISHED_LESSON){

                String finishedLessonName = pickedCourse.getLessonsList().get(position).getLessonName();
                holder.lessonNameTV.setText(finishedLessonName);

                holder.dishRatingRB.setRating(currentUser.getRatingForHistory(pickedCourseName, position));

                FirebaseStorage fStorage = FirebaseStorage.getInstance();
                StorageReference dishPhotoRef = fStorage.getReference("Users").child(currentlyLoggedInUser.getUid()).child("Courses").
                        child(pickedCourse.getCourseName()).child(finishedLessonName);
                long MAXBYTES = 1024 * 1024;
                dishPhotoRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.dishIV.setImageBitmap(bitmap);
                    }
                });
                dishPhotoRef.getBytes(MAXBYTES).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("HELL YEAH FAILURE");
                        holder.dishIV.setImageResource(R.drawable.add_dish);
                    }
                });
            }
        }

        //IF USER PICKED PREVIOUSLY FINISHED COURSE
        else{
            String finishedLessonName = pickedCourse.getLessonsList().get(position).getLessonName();
            holder.lessonNameTV.setText(finishedLessonName);

            holder.dishRatingRB.setRating(currentUser.getRatingForHistory(pickedCourseName, position));

            FirebaseStorage fStorage = FirebaseStorage.getInstance();
            StorageReference dishPhotoRef = fStorage.getReference("Users").child(currentlyLoggedInUser.getUid()).child("Courses").
                    child(pickedCourse.getCourseName()).child(finishedLessonName);
            long MAXBYTES = 1024 * 1024;
            dishPhotoRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.dishIV.setImageBitmap(bitmap);
                }
            });
            dishPhotoRef.getBytes(MAXBYTES).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    System.out.println("HELL YEAH FAILURE");
                    holder.dishIV.setImageResource(R.drawable.add_dish);
                }
            });
        }


    }






    @Override
    public int getItemCount() {
        return pickedCourse.getLessonsList().size();
    }


    public interface OnItemClickListener {
        void onItemClick2(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}
