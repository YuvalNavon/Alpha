package com.example.lifesworkiguess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MadeCommunityLessonsAdapter extends RecyclerView.Adapter<MadeCommunityLessonsViewHolder> {

    Context context;
    ArrayList<CommunityLesson> lessons;
    String userID;
    StorageReference fStorage, fDownRef;
    OnItemClickListener listener;

    public MadeCommunityLessonsAdapter(Context context, ArrayList<CommunityLesson> lessons, String userID, OnItemClickListener listener) {
        this.context = context;
        this.lessons = lessons;
        this.userID = userID;
        this.listener = listener;

        fStorage = FirebaseStorage.getInstance().getReference("Community Recipes").child(userID);
    }

    @NonNull
    @Override
    public MadeCommunityLessonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.made_community_lessons_list_item, parent, false);



        // Set up long-press listener
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                String pickedLessonName = v.getTag().toString();
                // Create a PopupMenu with the item view as an anchor
                PopupMenu popupMenu = new PopupMenu(v.getContext(), v);

                // Inflate the contextual menu resource
                popupMenu.inflate(R.menu.context_menu);

                // Set up click listener for menu items
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        // Handle menu item clicks here
                        switch (item.getItemId()) {
                            case R.id.start_recipe:

                                Intent toLessonIntro = new Intent(context, NewLessonIntro.class);

                                return true;
                            case R.id.view_reviews:
                                // Handle action two
                                return true;
                            case R.id.edit_recipe:
                                // Handle action three
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                // Show the popup menu
                popupMenu.show();
                return true;
            }
        });
        return new MadeCommunityLessonsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MadeCommunityLessonsViewHolder holder, int position) {

        String lessonName = lessons.get(position).getLessonName();
        holder.lessonName.setText(lessonName);

        fDownRef = fStorage.child(lessonName).child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);
        long MAXBYTES = 1024 * 1024;
        fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.lessonImage.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                holder.lessonImage.setImageResource(R.drawable.add_dish_photo);
            }
        });

        holder.setOnItemClickListener(new MadeCommunityLessonsViewHolder.OnItemClickListener() {
            @Override
            public void onItemClickUploadedRecipes(int position) {
                if (listener != null) {
                    listener.onItemClickUploadedRecipes(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public interface OnItemClickListener {
        void onItemClickUploadedRecipes(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
