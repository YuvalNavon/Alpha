/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Class isn't currently used but it can generate timed notifications.
 */


package com.example.lifesworkiguess;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class NotificationScreen extends AppCompatActivity {

    EditText et;
    TextView timeTV;
    String str1;
    Button setTime;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    Calendar c;
    boolean timePicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_screen);

        et = findViewById(R.id.textet);
        timeTV = findViewById(R.id.timepicktext);
        setTime = findViewById(R.id.setTime);
        timePicked = false;

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("My Notification", "HUHHH", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

            CharSequence name = "androidReminderChannel";
            String description = "Channel for Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channelTime = new NotificationChannel("AlarmTest", name, importance);
            channelTime.setDescription(description);

            NotificationManager managerTime = getSystemService(NotificationManager.class);
            managerTime.createNotificationChannel(channelTime);
        }



    }


    public void sendNoti(View view){
        str1 = et.getText().toString();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationScreen.this, "My Notification");
        builder.setContentTitle("Your Notification");
        builder.setContentText(str1);
        builder.setSmallIcon(R.drawable.ic_launcher_background);
        builder.setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1, builder.build());

    }

    public void timePick(View view){
        Calendar calendar = Calendar.getInstance();
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int mins = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(NotificationScreen.this, androidx.appcompat.R.style.Theme_AppCompat_Dialog, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.setTimeZone(TimeZone.getDefault());
                SimpleDateFormat format = new SimpleDateFormat("k: mm a");
                String time = format.format(c.getTime());
                timeTV.setText(time);
                timePicked = true;
                Toast.makeText(NotificationScreen.this, "TIME PICKED", Toast.LENGTH_LONG).show();



            }
        },hours, mins, false);
        timePickerDialog.show();
    }

    public void makeTimedNoti(View view){
        if (timePicked){
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent(this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(this, 0,intent, PendingIntent.FLAG_IMMUTABLE   );



            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
            Toast.makeText(this, "Alarm set!", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(this, "Set the time First!", Toast.LENGTH_LONG).show();

        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.NotificationScreen);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getTitle().toString().equals("User Authentication") ){
            Intent si = new Intent(this, StartScreen.class);
            startActivity(si);
        }


        return true;
    }

}