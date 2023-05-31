/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Fragment is where the user can view the Reviews of the CommunityLesson they're about
 * to start.
 */


package com.example.lifesworkiguess;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;


public class ViewRatings extends Fragment { //The Words "Rating" and "Review" are used interchangeably

    int lessonNumber;
    String jsonOfRatings, creatorID, username;
    ArrayList<ArrayList<String>> ratings;
    ArrayList<String> ownReviewList;

    RecyclerView ratingsRV;
    LinearLayout ownReviewLL;
    ScrollView ownReviewSV;
    TextView ownReviewUsernameTV, ownReviewTV, otherReviewsTV;
    ImageView ownReviewPFP, ownReviewUploadedDishIV;
    RatingBar ownReviewRB;
    View divider;

    FirebaseDatabase FBDB;
    DatabaseReference refUsers;
    ValueEventListener getUsername;
    FirebaseStorage fStorage;
    StorageReference fDownRef;

    public ViewRatings() {
        // Required empty public constructor
    }


    public static ViewRatings newInstance() {
        ViewRatings fragment = new ViewRatings();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            jsonOfRatings = getArguments().getString(MyConstants.COMMUNITY_LESSON_RATINGS_KEY);
            creatorID = getArguments().getString(MyConstants.LESSON_CREATOR_ID_KEY);
            lessonNumber = getArguments().getInt(MyConstants.COMMUNITY_LESSON_NUMBER_KEY);
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        if (refUsers!=null && getUsername !=null) refUsers.removeEventListener(getUsername);


    }


    @Override
    public void onResume() {

        super.onResume();
        if (refUsers!=null && getUsername !=null) refUsers.addListenerForSingleValueEvent(getUsername);

    }

    public void onDestroy() {

        super.onDestroy();
        if (refUsers!=null && getUsername !=null) refUsers.removeEventListener(getUsername);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_view_ratings, container, false);

        ratingsRV = view.findViewById(R.id.ReviewsRV);
        ownReviewLL = view.findViewById(R.id.ViewRatings_OwnRatingLL);
        ownReviewSV = view.findViewById(R.id.ViewRatings_OwnReviewSV);
        ownReviewUsernameTV = view.findViewById(R.id.ViewRatings_OwnUsernameTV);
        ownReviewTV = view.findViewById(R.id.ViewRatings_OwnReviewTV);
        otherReviewsTV = view.findViewById(R.id.ViewRatings_OtherReviewsTV);
        ownReviewPFP = view.findViewById(R.id.ViewRatings_OwnIV);
        ownReviewUploadedDishIV = view.findViewById(R.id.ViewRatings_OwnReview_UploadedDishIV);
        ownReviewRB = view.findViewById(R.id.ViewRatings_OwnReviewRB);
        divider = view.findViewById(R.id.ViewRatings_Divider);

        if (jsonOfRatings!=null)
        {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<ArrayList<String>>>(){}.getType();
            ratings = gson.fromJson(jsonOfRatings, type);

            FirebaseAuth fAuth = FirebaseAuth.getInstance();
            FirebaseUser loggedInUser = fAuth.getCurrentUser();

            if (!creatorID.equals(loggedInUser.getUid())) //User Views recipe thats not their own
            {
                int posToRemove = 0;
                for (int i = 0; i<ratings.size(); i ++)
                {
                    ArrayList<String> checkedReviewList = ratings.get(i);
                    if (checkedReviewList.get(0).equals(loggedInUser.getUid()))
                    {
                        ownReviewList = checkedReviewList;
                        posToRemove = i;
                    }
                }
                if (ownReviewList!=null) ratings.remove(posToRemove);
            }

            if (ownReviewList==null) //User picked a recipe they didnt review (also applies to their own recipes)
            {
                ownReviewLL.setVisibility(View.GONE);
                otherReviewsTV.setVisibility(View.GONE);
                divider.setVisibility(View.GONE);
            }

            else //User picked a recipe they did review
            {
                myServices.getProfilePhotoFromFirebase(ownReviewPFP, loggedInUser.getUid());
                ownReviewUsernameTV.setText("");

                FBDB = FirebaseDatabase.getInstance("https://cookproject-ac2c0-default-rtdb.europe-west1.firebasedatabase.app");
                refUsers = FBDB.getReference("Users").child(loggedInUser.getUid());
                getUsername = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        User currentUser = snapshot.getValue(User.class);
                        username = currentUser.getUsername();
                        ownReviewUsernameTV.setText(username);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };
                refUsers.addListenerForSingleValueEvent(getUsername);

                ownReviewRB.setEnabled(false);



                if (!ownReviewList.get(1).equals(MyConstants.NO_RATING_FOR_COMMUNITY_LESSON)) //User rated recipe
                    {
                        float ownRating = Float.parseFloat(ownReviewList.get(1));
                        ownReviewRB.setRating(ownRating);
                    }

                else //User didnt rate recipe
                    {
                        ownReviewRB.setVisibility(View.GONE);
                    }

                if (!ownReviewList.get(2).equals(MyConstants.NO_REVIEW_FOR_COMMUNITY_LESSON)) //User reviews recipe
                    {
                        String review =ownReviewList.get(2);
                        ownReviewTV.setText(review);
                    }

                else //User didnt write a written review for recipe (but could have rated)
                    {
                        ownReviewSV.setVisibility(View.GONE);


                    }


                fStorage = FirebaseStorage.getInstance();
                fDownRef = FirebaseStorage.getInstance().getReference().child("Users").child(loggedInUser.getUid()).
                            child("Finished Community Lessons").child(creatorID).child(Integer.toString(lessonNumber));
                long MAXBYTES = 1024*1024 * 5;

                ownReviewUploadedDishIV.setVisibility(View.GONE);
                fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {

                            ownReviewUploadedDishIV.setVisibility(View.VISIBLE);
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
                            ownReviewUploadedDishIV.setImageBitmap(bitmap);
                        }
                    });

                }


            CustomRatingsAdapter adapter = new CustomRatingsAdapter(getContext(), ratings, creatorID, lessonNumber);

            // Set the layout manager for the RecyclerView
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
            ratingsRV.setLayoutManager(linearLayoutManager);

            // Set the adapter for the RecyclerView
            ratingsRV.setAdapter(adapter);

        }

        else
        {
            ownReviewLL.setVisibility(View.GONE);
            ratingsRV.setVisibility(View.GONE);
            otherReviewsTV.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }



        return  view;
    }






}