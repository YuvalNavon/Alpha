package com.example.lifesworkiguess;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class NewLessonIntroOverviewFrag extends Fragment {


    //For PermanentLessonSetup
    String courseName, recipeTitle, recipeImageURI;
    int lessonPosition;
    boolean lessonFinal;

    //For CommunityLessonSetup
    String creatorUsername, creatorID, lessonDescription;
    int lessonNumber;
    //Add Rating!!

    //For Both
    int mode;
    String lessonName, lessonTime, lessonDifficulty;
    int lessonServeCount;
    boolean lessonKosher;




    TextView  lessonPositionOrCreatorTV, lessonNameTV,  expectedTimeTV, difficultyTV, kosherTV, serveCountTV, descriptionTV, descriptionTitle;
    ImageView recipeIV, kosherIV;
    Button startBTN;
    ScrollView descriptionSV;
    Space spaceBetweenRecipeIVAndDescription;

    public NewLessonIntroOverviewFrag() {
        // Required empty public constructor
    }

    public static NewLessonIntroOverviewFrag newInstance() {
        NewLessonIntroOverviewFrag fragment = new NewLessonIntroOverviewFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

            mode = getArguments().getInt(MyConstants.LESSON_INTRO_MODE_KEY);

            if (getArguments()!=null)
            {
                if (mode == MyConstants.PERMENANT_LESSON_INTRO)
                {
                    lessonName = getArguments().getString("Lesson Name");
                    courseName = getArguments().getString("Course Name");
                    lessonPosition = getArguments().getInt("Lesson Position - 1", MyConstants.NO_LESSON_POSITION);
                    lessonFinal = getArguments().getBoolean("Is Lesson Final", false);
                    recipeImageURI = getArguments().getString(MyConstants.PERMENANT_LESSON_RECIPE_IMAGE_URI_KEY);
                    recipeTitle = getArguments().getString(MyConstants.PERMENANT_LESSON_RECIPE_TITLE_KEY);
                }

                else if (mode == MyConstants.COMMUNITY_LESSON_INTRO)
                {
                    lessonName = getArguments().getString(MyConstants.LESSON_NAME_KEY);
                    creatorUsername = getArguments().getString(MyConstants.LESSON_CREATOR_USERNAME_KEY);
                    lessonDescription = getArguments().getString(MyConstants.COMMUNITY_LESSON_DESCRIPTION_KEY);
                    creatorID = getArguments().getString(MyConstants.LESSON_CREATOR_ID_KEY);
                    lessonNumber = getArguments().getInt(MyConstants.COMMUNITY_LESSON_NUMBER_KEY);
                }

                lessonTime = getArguments().getString(MyConstants.LESSON_TIME_KEY);
                lessonDifficulty = getArguments().getString(MyConstants.LESSON_DIFFICULTY_KEY);
                lessonKosher = getArguments().getBoolean(MyConstants.LESSON_KOSHER_KEY, false);
                lessonServeCount = getArguments().getInt(MyConstants.LESSON_SERVE_COUNT_KEY, MyConstants.NO_SERVE_COUNT_ERROR);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.activity_lesson_intro, container, false);

        lessonPositionOrCreatorTV = view.findViewById(R.id.lessonTitleTV);
        lessonNameTV = view.findViewById(R.id.lessonNameIntroScreenTV);
        expectedTimeTV = view.findViewById(R.id.expectedTimeTV);
        difficultyTV = view.findViewById(R.id.difficultyTV);
        serveCountTV = view.findViewById(R.id.lessonIntroScreenServeCountTV);
        kosherTV = view.findViewById(R.id.kosherTV);
        kosherIV = view.findViewById(R.id.kosherIV);
        recipeIV = view.findViewById(R.id.LessonIntroRecipeIV);
        descriptionSV = view.findViewById(R.id.LessonIntroDescriptionSV);
        descriptionTV = view.findViewById(R.id.LessonIntroDescriptionTV);
        descriptionTitle = view.findViewById(R.id.LessonIntroDescriptionTitle);
        startBTN = view.findViewById(R.id.startLessonBTN);
        spaceBetweenRecipeIVAndDescription = view.findViewById(R.id.LessonIntro_Space);



        if (mode == MyConstants.PERMENANT_LESSON_INTRO)
        {
            permenantLessonSetup();
        }

        if (mode == MyConstants.COMMUNITY_LESSON_INTRO)
        {
            communityLessonSetup();
        }

        startBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLesson(v);
            }
        });

        return view;
    }


    public void permenantLessonSetup(){
        if (lessonFinal) lessonPositionOrCreatorTV.setText("Final Lesson");
        else lessonPositionOrCreatorTV.setText(MyConstants.LESSON_POSITIONS[lessonPosition] + " Lesson");
        lessonNameTV.setText("Make Some " + recipeTitle + "!");
        expectedTimeTV.setText(lessonTime);
        difficultyTV.setText(lessonDifficulty);
        if (lessonKosher) {
            kosherTV.setText("KOSHER");
            kosherIV.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
        }
        else {
            kosherTV.setText("NOT\nKOSHER");
            kosherIV.setImageResource(android.R.drawable.ic_delete);
        }
        serveCountTV.setText(Integer.toString(lessonServeCount));

        StorageReference fStorage = FirebaseStorage.getInstance().getReference("Courses").child(courseName).child(lessonName);
        StorageReference fDownRef = fStorage.child(recipeImageURI);
        long MAXBYTES = 1024 * 1024;
        fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                recipeIV.setImageBitmap(bitmap);

            }
        });
        fDownRef.getBytes(MAXBYTES).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                recipeIV.setImageResource(R.drawable.add_dish_photo);

            }
        });
        descriptionTitle.setVisibility(View.GONE);
        descriptionSV.setVisibility(View.GONE);
        spaceBetweenRecipeIVAndDescription.setVisibility(View.GONE);

    }

    public void communityLessonSetup(){
        lessonPositionOrCreatorTV.setText(creatorUsername + "'s Recipe" );
        lessonNameTV.setText(lessonName);
        expectedTimeTV.setText(lessonTime);
        difficultyTV.setText(lessonDifficulty);
        if (lessonKosher) {
            kosherTV.setText("KOSHER");
            kosherIV.setImageResource(com.firebase.ui.auth.R.drawable.fui_ic_check_circle_black_128dp);
        }
        else {
            kosherTV.setText("NOT\nKOSHER");
            kosherIV.setImageResource(android.R.drawable.ic_delete);
        }
        serveCountTV.setText(Integer.toString(lessonServeCount));

        descriptionTV.setText(lessonDescription);


        StorageReference fStorage = FirebaseStorage.getInstance().getReference("Community Recipes").child(creatorID).child(lessonName);
        StorageReference fDownRef = fStorage.child(MyConstants.RECIPE_IMAGE_STORAGE_NAME);
        long MAXBYTES = 1024 * 1024;
        fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                recipeIV.setImageBitmap(bitmap);

            }
        });

    }


    public void startLesson(View view){

        Intent toLessonScreen = new Intent(getContext(), newLessonScreen.class);

        toLessonScreen.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, mode);
        toLessonScreen.putExtra(MyConstants.VIEW_STEP_MODE_KEY, MyConstants.FROM_LESSON_INTRO);

        if (mode == MyConstants.PERMENANT_LESSON_INTRO)
        {
            toLessonScreen.putExtra("Lesson Name",  lessonName);
            toLessonScreen.putExtra("Lesson Position in List", lessonPosition);
        }

        else if (mode == MyConstants.COMMUNITY_LESSON_INTRO)
        {
            toLessonScreen.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, creatorID);
            toLessonScreen.putExtra(MyConstants.LESSON_NAME_KEY, lessonName);
            toLessonScreen.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, lessonNumber);
        }


        startActivity(toLessonScreen);
        getActivity().finish();



    }
}