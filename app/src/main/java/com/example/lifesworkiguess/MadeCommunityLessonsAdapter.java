package com.example.lifesworkiguess;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MadeCommunityLessonsAdapter extends RecyclerView.Adapter<MadeCommunityLessonsViewHolder> {

    Context context;
    ArrayList<CommunityLesson> lessons;
    String userID, username;
    StorageReference fStorage, fDownRef;

    public MadeCommunityLessonsAdapter(Context context, ArrayList<CommunityLesson> lessons, String userID, String username) {
        this.context = context;
        this.lessons = lessons;
        this.userID = userID;
        this.username = username;

        fStorage = FirebaseStorage.getInstance().getReference("Community Recipes").child(userID);
    }

    @NonNull
    @Override
    public MadeCommunityLessonsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.made_community_lessons_list_item, parent, false);
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

        final CommunityLesson pickedLesson = lessons.get(position);

        // Set the click listeners for imageView1 and textView
        holder.lessonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toLessonIntro = new Intent(context, NewLessonIntro.class);
                toLessonIntro.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.COMMUNITY_LESSON_INTRO);
                toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, userID);
                toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY, username);
                toLessonIntro.putExtra(MyConstants.LESSON_NAME_KEY, pickedLesson.getLessonName());
                toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY, pickedLesson.getDescription());
                toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, pickedLesson.getNumber());

                context.startActivity(toLessonIntro);
            }
        });

        holder.lessonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toLessonIntro = new Intent(context, NewLessonIntro.class);
                toLessonIntro.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.COMMUNITY_LESSON_INTRO);
                toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, userID);
                toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY, username);
                toLessonIntro.putExtra(MyConstants.LESSON_NAME_KEY, pickedLesson.getLessonName());
                toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY, pickedLesson.getDescription());
                toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, pickedLesson.getNumber());

                context.startActivity(toLessonIntro);
            }
        });

        // Set the click listener for imageView2
        holder.lessonOptionsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPopupMenu(holder.LL, pickedLesson, position);

            }
        });

        holder.LL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {             //NOT WORKING FOR SOME REASON

                showPopupMenu(holder.LL, pickedLesson, position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    private void showPopupMenu(View view, CommunityLesson pickedLesson, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Handle the menu item click
                switch (item.getItemId()) {
                    case R.id.view_lesson_details:

                        Intent toLessonIntro = new Intent(context, NewLessonIntro.class);
                        toLessonIntro.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.COMMUNITY_LESSON_INTRO);
                        toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, userID);
                        toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY, username);
                        toLessonIntro.putExtra(MyConstants.LESSON_NAME_KEY, pickedLesson.getLessonName());
                        toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY, pickedLesson.getDescription());
                        toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, pickedLesson.getNumber());

                        context.startActivity(toLessonIntro);

                        return true;


                    case R.id.edit_recipe:



                        return true;

                    case R.id.delete_recipe:

                        AlertDialog.Builder deleteLessonDialogBuilder = new AlertDialog.Builder(context);

                        deleteLessonDialogBuilder.setTitle("Deleting Lesson");
                        deleteLessonDialogBuilder.setMessage("Are you sure you want to Delete Your Recipe?\nThis CANNOT be undone.");

                        deleteLessonDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });


                        deleteLessonDialogBuilder.setPositiveButton("Yes, Delete Recipe", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                               FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
                               //Updating User Info
                               DatabaseReference refUsers=FBDB.getReference("Users").child(userID);
                               ValueEventListener userUpdater = new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        User currentUser = snapshot.getValue(User.class);
                                        currentUser.setLessonAsInactive(pickedLesson.getNumber());
                                        refUsers.setValue(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                                //Updating Community Lessons Branch

                                                DatabaseReference refCommunityLessons=FBDB.getReference("Community Lessons").child(userID + " , " + pickedLesson.getNumber());
                                                ValueEventListener lessonUpdater = new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        CommunityLesson pickedDeletedLesson =  snapshot.getValue(CommunityLesson.class); //Same as just Using pickedLesson
                                                        pickedDeletedLesson.setActive(false);
                                                        refCommunityLessons.setValue(pickedDeletedLesson).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {

                                                                //Updating Community Lessons By User Branch

                                                                DatabaseReference refCommunityLessonsByUser=
                                                                        FBDB.getReference("Community Lessons By User").child(userID)
                                                                                .child(Integer.toString(pickedLesson.getNumber()));
                                                                ValueEventListener lessonUpdaterByUser = new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        CommunityLesson pickedDeletedLesson =  snapshot.getValue(CommunityLesson.class); //Same as just Using pickedLesson
                                                                        pickedDeletedLesson.setActive(false);
                                                                        refCommunityLessonsByUser.setValue(pickedDeletedLesson).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                lessons.remove(position);
                                                                                notifyDataSetChanged();
                                                                            }
                                                                        });
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                };
                                                                refCommunityLessonsByUser.addListenerForSingleValueEvent(lessonUpdaterByUser);

                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                };
                                                refCommunityLessons.addListenerForSingleValueEvent(lessonUpdater);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                };
                                refUsers.addListenerForSingleValueEvent(userUpdater);
                            }
                        });

                        // Create and show the AlertDialog
                        AlertDialog deleteLessonDialog = deleteLessonDialogBuilder.create();
                        deleteLessonDialog.show();

                        return true;

                }
                return false;
            }
        });

        popupMenu.show();
    }
}
