package com.example.lifesworkiguess;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LessonScreenFrag extends Fragment {

    //For Permenant Lesson
    int lessonPosition;

    //For Community Lesson
    String creatorID;
    ValueEventListener communityLessonGetter,communityLessonGetterByUser ;
    DatabaseReference refCommunityLessons, refCommunityLessonsByUser;
    int lessonNumber;

    //For Both
    int permanentOrCommunity;
    String lessonName;



    //For FromLessonIntro
    int currStepNumber;

    TextView stepNumberTV, stepNameTV, stepDescriptionTV;
    ImageView stepImageIV;
    Button nextBtn, prevBtn;

    Recipe recipe;

    ValueEventListener courseGetter;
    DatabaseReference refUsers;




    public LessonScreenFrag() {
        // Required empty public constructor
    }


    public static LessonScreenFrag newInstance() {
        LessonScreenFrag fragment = new LessonScreenFrag();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null)
        {


                requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true /* enabled by default */) {
                    @Override
                    public void handleOnBackPressed() {
                        // Handle the back button press event here
                        //Setting up Alert Dialogs
                        AlertDialog.Builder exitLessonDialogBuilder = new AlertDialog.Builder(getContext());

                        exitLessonDialogBuilder.setTitle("Quitting Lesson");
                        exitLessonDialogBuilder.setMessage("Are you sure you want to Quit this Lesson?");

                        exitLessonDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Handle click here
                            }
                        });


                        exitLessonDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                getActivity().finish();
                            }
                        });

                        // Create and show the AlertDialog
                        AlertDialog exitLessonDialog = exitLessonDialogBuilder.create();
                        exitLessonDialog.show();
                    }
                });





        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lesson_screen, container, false);

        stepNumberTV = view.findViewById(R.id.stepNumberTV);
        stepNameTV = view.findViewById(R.id.stepNameTV);
        stepDescriptionTV = view.findViewById(R.id.stepDescriptionTV);
        stepImageIV = view.findViewById(R.id.stepImageIV);

        nextBtn = view.findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                nextStep(v);
            }
        });

        prevBtn = view.findViewById(R.id.prevBtn);
        prevBtn.setVisibility(View.GONE);
        prevBtn.setEnabled(false);
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevStep(v);
            }
        });

        Context context = getContext();

        recipe = myServices.XMLToRecipe(context, MyConstants.DOWNLOADED_RECIPE_NAME);

        Bundle dataFromIntent =getArguments();
        permanentOrCommunity = dataFromIntent.getInt(MyConstants.LESSON_INTRO_MODE_KEY);

        if (permanentOrCommunity == MyConstants.PERMENANT_LESSON_INTRO)
        {
            lessonName = dataFromIntent.getString("Lesson Name");
            lessonPosition = dataFromIntent.getInt("Lesson Position in List");
        }

        else if (permanentOrCommunity == MyConstants.COMMUNITY_LESSON_INTRO)
        {
            lessonName = dataFromIntent.getString(MyConstants.LESSON_NAME_KEY);
            creatorID = dataFromIntent.getString(MyConstants.LESSON_CREATOR_ID_KEY);
            lessonNumber = dataFromIntent.getInt(MyConstants.COMMUNITY_LESSON_NUMBER_KEY);
        }


        currStepNumber = 0;
        setStepDetails(currStepNumber);



        return view;
    }

    @Override
    public void onPause() {

        super.onPause();
        if (refUsers!=null && courseGetter!=null) refUsers.removeEventListener(courseGetter);
        if (refCommunityLessons!=null && communityLessonGetter!=null) refCommunityLessons.removeEventListener(communityLessonGetter);
        if (refCommunityLessonsByUser!=null && communityLessonGetterByUser!=null) refCommunityLessonsByUser.removeEventListener(communityLessonGetterByUser);

    }


    @Override
    public void onResume() {

        super.onResume();


    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && courseGetter!=null) refUsers.removeEventListener(courseGetter);
        if (refCommunityLessons!=null && communityLessonGetter!=null) refCommunityLessons.removeEventListener(communityLessonGetter);
        if (refCommunityLessonsByUser!=null && communityLessonGetterByUser!=null) refCommunityLessonsByUser.removeEventListener(communityLessonGetterByUser);


    }



    public void setStepDetails(int stepNumber){
        if (recipe.getSteps()!= null && !recipe.getSteps().isEmpty() )
        {
            Step currStep = recipe.getSteps().get(stepNumber);
            if (stepIsLast(stepNumber)){
                stepNumberTV.setText("Last Step");
                nextBtn.setText("Finish!");
            }
            else{
                stepNumberTV.setText("Step " + Integer.toString(stepNumber+1));

            }
            stepNameTV.setText(currStep.getName());
            stepDescriptionTV.setText(currStep.getDescription());
            //Implement stepAction WITHOUT DELAY
            Glide.with(this)
                    .load(currStep.getAction())
                    .into(stepImageIV);

        }



    }

    public boolean stepIsLast(int stepNumber){
        if (stepNumber==recipe.getSteps().size()-1) return true;
        else return false;
    }


    public void nextStep(View view){

        Context context = getContext();
        if (stepIsLast(currStepNumber)){
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseUser loggedInUser = fAuth.getCurrentUser();

            if (permanentOrCommunity == MyConstants.COMMUNITY_LESSON_INTRO && creatorID.equals(loggedInUser.getUid())) //Cant let user Review their owen lessons
            {
                    Intent toHomeScreen = new Intent(context, HomeScreen.class);
                    getContext().startActivity(toHomeScreen);


            }


            else
            {
                FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
                refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());

                Intent toLessonFinishedScreen = new Intent(context, LessonFinished.class);
                toLessonFinishedScreen.putExtra(MyConstants.LESSON_INTRO_MODE_KEY, permanentOrCommunity);

                if (permanentOrCommunity == MyConstants.PERMENANT_LESSON_INTRO)
                {
                    courseGetter = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            User currentlyLoggedUser = snapshot.getValue(User.class);
                            currentlyLoggedUser.setLessonFinished(lessonPosition);

                            FBDB.getReference("Users/" + loggedInUser.getUid()).setValue(currentlyLoggedUser);

                            toLessonFinishedScreen.putExtra("Lesson Position in List", lessonPosition);
                            toLessonFinishedScreen.putExtra("Lesson Name", lessonName);
                            toLessonFinishedScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(toLessonFinishedScreen);



                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    refUsers.addListenerForSingleValueEvent(courseGetter);
                }

                else if (permanentOrCommunity == MyConstants.COMMUNITY_LESSON_INTRO)
                {
                    courseGetter = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            User currentlyLoggedUser = snapshot.getValue(User.class);
                            currentlyLoggedUser.addFinishedCommunityLesson(creatorID, lessonNumber);
                            FBDB.getReference("Users/" + loggedInUser.getUid()).setValue(currentlyLoggedUser);

                            //Updating Community Lessons branch
                            refCommunityLessons = FBDB.getReference("Community Lessons").child(creatorID + " , " + lessonNumber  );
                            communityLessonGetter = new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {


                                    CommunityLesson finishedLesson = snapshot.getValue(CommunityLesson.class);

                                    finishedLesson.addUserWhoCompleted(loggedInUser.getUid());
                                    refCommunityLessons.setValue(finishedLesson).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {

                                            //And for the second Branch
                                            refCommunityLessonsByUser = FBDB.getReference("Community Lessons By User").child(creatorID).child(Integer.toString(lessonNumber));
                                            communityLessonGetterByUser = new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    CommunityLesson finishedLesson = snapshot.getValue(CommunityLesson.class);

                                                    finishedLesson.addUserWhoCompleted(loggedInUser.getUid());
                                                    refCommunityLessonsByUser.setValue(finishedLesson).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            toLessonFinishedScreen.putExtra(MyConstants.LESSON_CREATOR_ID_KEY, creatorID);
                                                            toLessonFinishedScreen.putExtra(MyConstants.LESSON_NAME_KEY, lessonName);
                                                            toLessonFinishedScreen.putExtra(MyConstants.COMMUNITY_LESSON_NUMBER_KEY, lessonNumber);

                                                            toLessonFinishedScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            context.startActivity(toLessonFinishedScreen);
                                                        }
                                                    });


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            };
                                            refCommunityLessonsByUser.addListenerForSingleValueEvent(communityLessonGetterByUser);
                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            };
                            refCommunityLessons.addListenerForSingleValueEvent(communityLessonGetter);






                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    };
                    refUsers.addListenerForSingleValueEvent(courseGetter);
                }



            }



        }

        else{
            prevBtn.setVisibility(View.VISIBLE);
            prevBtn.setEnabled(true); // For this line and the previous one,
            // I Could add a check that the step is no longer the first one so that this will only be done once but no need
            currStepNumber+=1;
            setStepDetails(currStepNumber);

        }

//        formatStepName(recipe.getSteps().get(currStepNumber).getName());
    }

    public void prevStep(View view){

            currStepNumber-=1;
            nextBtn.setText("Next"); // Could add a check that the step is no longer the last one so that this will only be done once but no need

            if (currStepNumber==0){

                prevBtn.setVisibility(View.GONE);
                prevBtn.setEnabled(false);
            }

        setStepDetails(currStepNumber);

    }
}