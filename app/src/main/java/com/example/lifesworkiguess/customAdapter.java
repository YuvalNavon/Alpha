/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class is the Adapter that is used to show PermanentLessons in a Recycler View (In the HomeScreen Activity).
 */

package com.example.lifesworkiguess;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class customAdapter extends RecyclerView.Adapter<CustomViewHolder> {
    Context context;
    StorageReference fStorage, fDownRef;
    Course course;
    OnItemClickListener listener;

    public customAdapter(Context context, Course selectedCourse, OnItemClickListener listener) {

        this.context = context;
        this.listener = listener;
        this.course = selectedCourse;
        this.fStorage = FirebaseStorage.getInstance().getReference("Courses").child(selectedCourse.getCourseName());
    }



    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_design, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        String lessonName = course.getLessonsList().get(position).getLessonName();
        String lessonLogoUri = course.getLessonsList().get(position).getLogoUri();
        holder.lessonName.setText("Make Some " + lessonName + "!");
        fDownRef = fStorage.child(lessonName).child(lessonLogoUri);
        long MAXBYTES = 1024 * 1024 * 5;
        fDownRef.getBytes(MAXBYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                bitmap = myServices.getCircularBitmap(bitmap);
                holder.logo.setImageBitmap(bitmap);

            }
        });

        holder.setOnItemClickListener(new CustomViewHolder.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return course.getLessonsList().size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


}
