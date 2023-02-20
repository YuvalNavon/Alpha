package com.example.lifesworkiguess;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LessonScreenFrag#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LessonScreenFrag extends Fragment {

    String lessonName;
    int currStepNumber, lessonPosition;

    TextView stepNumberTV, stepNameTV, stepDescriptionTV;
    ImageView stepImageIV;
    Button nextBtn;

    Recipe recipe;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LessonScreenFrag() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LessonScreenFrag.
     */
    // TODO: Rename and change types and number of parameters
    public static LessonScreenFrag newInstance(String param1, String param2) {
        LessonScreenFrag fragment = new LessonScreenFrag();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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

        Context context = getContext();
        Bundle dataFromIntent =getArguments();
        if (dataFromIntent!= null){
            recipe = myServices.XMLToRecipe(context, MyConstants.CURRENTLY_LEARNED_RECIPE);

            currStepNumber = 0;
            setStepDetails(currStepNumber);


        }

        return view;
    }

    public void setStepDetails(int stepNumber){
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

    public boolean stepIsLast(int stepNumber){
        if (stepNumber==recipe.getSteps().size()-1) return true;
        else return false;
    }

//    public String determineGIF(int stepNumber){
//        if (stepNumber>MyConstants.GIFS_LINKS_LIST.size()){
//            return MyConstants.NO_GIF_ERROR;
//        }
//        else{
//            String stepName = recipe.getSteps().get(stepNumber).getName();
//            String gif = MyConstants.NO_GIF_ERROR;
//            for (int i = 0; i<MyConstants.GIFS_LINKS_LIST.size(); i++){
//                String actionName = formatStepName(stepName);
//            }
//            return gif;
//        }
//
//    }

//    public String formatStepName(String stepName){
//
//        //Find Connector Words to Remove them
//        String beforeConnector, afterConnector;
//        int spaceCount = 0;
//        String tempName = stepName;
//        for (int i = 0; i<stepName.length();i++){
//            if (stepName.charAt(i) == ' ') spaceCount = spaceCount + 1;
//        }
//        for (int i = 0; i<spaceCount; i++){
//            String currWord = tempName.substring(0, tempName.indexOf(' '));
//            for (int j = 0;j<MyConstants.CONNECTOR_WORDS.length;j++){
//                if (currWord.equals(MyConstants.CONNECTOR_WORDS[j])){
//                   stepName = removeFirstWord(stepName, currWord);
//                }
//            }
//            tempName = tempName.substring(tempName.indexOf(' ') + 1);
//        }
//
//        return stepName;
//    }

//    public static String removeFirstWord(String input, String word) {
//        int index = input.indexOf(word);
//        if (index != -1) {
//            String before = input.substring(0, index).trim();
//            String after = input.substring(index + word.length()).trim();
//            return before + " " + after;
//        }
//        return input;
//    }

    public void nextStep(View view){

        Context context = getContext();
        if (stepIsLast(currStepNumber)){
            Toast.makeText(context, "FINISHED", Toast.LENGTH_LONG).show();
            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseUser loggedInUser = fAuth.getCurrentUser();
            FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
            DatabaseReference refUsers=FBDB.getReference("Users").child(loggedInUser.getUid());
            ValueEventListener courseGetter = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User currentlyLoggedUser = snapshot.getValue(User.class);
                    currentlyLoggedUser.setLessonFinished(lessonPosition);
                    FBDB.getReference("Users/" + loggedInUser.getUid()).setValue(currentlyLoggedUser);
                    Intent toLessonFinishedScreen = new Intent(context, LessonFinished.class);
                    toLessonFinishedScreen.putExtra("Lesson Position in List", lessonPosition);
                    toLessonFinishedScreen.putExtra("Lesson Name", lessonName);
                    toLessonFinishedScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(toLessonFinishedScreen);


                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            refUsers.addValueEventListener(courseGetter);


        }
        else{
            currStepNumber+=1;
            setStepDetails(currStepNumber);

        }

//        formatStepName(recipe.getSteps().get(currStepNumber).getName());

    }
}