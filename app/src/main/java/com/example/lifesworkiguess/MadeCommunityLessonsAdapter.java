package com.example.lifesworkiguess;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

        final CommunityLesson pickedLesson = lessons.get(position);

        String lessonName = pickedLesson.getLessonName();
        holder.lessonName.setText(lessonName);

        fDownRef = fStorage.child(Integer.toString(pickedLesson.getNumber())).child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);
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


        // Set the click listeners for imageView1 and textView
        holder.lessonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLesson(pickedLesson);

            }
        });

        holder.lessonName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLesson(pickedLesson);
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

    public void startLesson(CommunityLesson lesson)
    {
        Intent toLessonIntro = new Intent(context, NewLessonIntro.class);
        toLessonIntro.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, MyConstants.COMMUNITY_LESSON_INTRO);
        toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, userID);
        toLessonIntro.putExtra(MyConstants.LESSON_CREATOR_USERNAME_KEY, username);
        toLessonIntro.putExtra(MyConstants.LESSON_NAME_KEY, lesson.getLessonName());
        toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY, lesson.getDescription());
        toLessonIntro.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, lesson.getNumber());

        context.startActivity(toLessonIntro);
    }

    public void editRecipe(CommunityLesson lesson)
    {
        myServices.downloadXML(context, MyConstants.RECIPE_STORAGE_NAME,
                "Community Recipes/" + userID + "/" + Integer.toString(lesson.getNumber()));


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 3 second (to let Recipe Download)
                Recipe recipe = myServices.XMLToRecipe(context, MyConstants.DOWNLOADED_RECIPE_NAME);

                ArrayList<Ingredient> ingredientsList  = recipe.getIngredients();
                ArrayList<String[]> ingredientsInStringLists = new ArrayList<>();

                for (Ingredient ingredient: ingredientsList)
                {
                    String[] addedIngredientList = new String[]{ingredient.getName(), ingredient.getAmount(), ingredient.getUnits()};
                    ingredientsInStringLists.add(addedIngredientList);

                }
                Gson gson = new Gson();
                String jsonOfIngredients = gson.toJson(ingredientsInStringLists);


                ArrayList<Step> stepsList  = recipe.getSteps();
                ArrayList<String[]>  stepsInStringLists = new ArrayList<>();
                for (Step currStep : stepsList)
                {
                    String stepName = currStep.getName();
                    String stepDescription = currStep.getDescription();
                    String stepTime = currStep.getTime();
                    String stepImageUri = currStep.getAction();

                    //No need for Step Number - the index in the list is the number

                    String[] currStepInString = new String[]{stepName, stepDescription, stepTime, stepImageUri};

                    stepsInStringLists.add(currStepInString);
                }
                String jsonOfSteps = gson.toJson(stepsInStringLists);

                Intent toCreateRecipeFinishScreen = new Intent(context, CreateRecipeFinishScreen.class);
                toCreateRecipeFinishScreen.putExtra("Previous Activity", MyConstants.FROM_PROFILE_AKA_EDIT_MODE);

                SharedPreferences settings= context.getSharedPreferences("PREFS_NAME",MODE_PRIVATE);
                SharedPreferences.Editor editor=settings.edit();

                editor.putInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_NUMBER, lesson.getNumber());
                editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_NAME, lesson.getLessonName());
                editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_DESCRIPTION, lesson.getDescription());
                editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_INGREDIENTS, jsonOfIngredients);
                editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_STEPS, jsonOfSteps);
                editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_TIME, lesson.getTime());
                editor.putString(MyConstants.ORIGINAL_CUSTOM_RECIPE_DIFFICULTY_LEVEL, lesson.getDifficulty());
                editor.putBoolean(MyConstants.ORIGINAL_CUSTOM_RECIPE_KOSHER, lesson.isKosher());
                editor.putInt(MyConstants.ORIGINAL_CUSTOM_RECIPE_SERVE_COUNT, lesson.getServeCount());


                editor.putString(MyConstants.CUSTOM_RECIPE_NAME, lesson.getLessonName());
                editor.putString(MyConstants.CUSTOM_RECIPE_DESCRIPTION, lesson.getDescription());
                editor.putString(MyConstants.CUSTOM_RECIPE_INGREDIENTS, jsonOfIngredients);
                editor.putString(MyConstants.CUSTOM_RECIPE_STEPS, jsonOfSteps);
                editor.putString(MyConstants.CUSTOM_RECIPE_TIME, lesson.getTime());
                editor.putString(MyConstants.CUSTOM_RECIPE_DIFFICULTY_LEVEL, lesson.getDifficulty());
                editor.putBoolean(MyConstants.CUSTOM_RECIPE_KOSHER, lesson.isKosher());
                editor.putInt(MyConstants.CUSTOM_RECIPE_SERVE_COUNT, lesson.getServeCount());

                editor.commit();


                fDownRef = fStorage.child(Integer.toString(lesson.getNumber())).child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);
                long MAXBYTES = 1024 * 1024;
                fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        try {
                            FileOutputStream fos = context.openFileOutput(MyConstants.IMAGE_FILE_NAME, MODE_PRIVATE);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            context.startActivity(toCreateRecipeFinishScreen);



                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Image
                        File recipeImageFile = new File(context.getFilesDir(), MyConstants.IMAGE_FILE_NAME);
                        if (recipeImageFile.exists()) {
                            recipeImageFile.delete();
                        }

                        File recipeNoImageFile = new File(context.getFilesDir(), MyConstants.NO_IMAGE_FILE_NAME);
                        if (recipeNoImageFile.exists()) {
                            recipeNoImageFile.delete();
                        }
                        context.startActivity(toCreateRecipeFinishScreen);
                    }
                });




            }
        }, 3000);

    }

    public void deleteRecipe(CommunityLesson lesson, int position)
    {
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
                        currentUser.setLessonAsInactive(lesson.getNumber());
                        refUsers.setValue(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                                //Updating Community Lessons Branch

                                DatabaseReference refCommunityLessons=FBDB.getReference("Community Lessons").child(userID + " , " + lesson.getNumber());
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
                                                                .child(Integer.toString(lesson.getNumber()));
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

                        startLesson(pickedLesson);


                        return true;


                    case R.id.edit_recipe:

                      editRecipe(pickedLesson);
                        return true;


                    case R.id.delete_recipe:

                        deleteRecipe(pickedLesson, position);

                        return true;

                }
                return false;
            }
        });

        popupMenu.show();
    }
}
