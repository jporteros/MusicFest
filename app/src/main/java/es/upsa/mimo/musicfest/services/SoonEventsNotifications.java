package es.upsa.mimo.musicfest.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import es.upsa.mimo.musicfest.Activities.EventDetailActivity;
import es.upsa.mimo.musicfest.Activities.MenuActivity;
import es.upsa.mimo.musicfest.Adapters.CardAdapterEvent;
import es.upsa.mimo.musicfest.Model.Event;
import es.upsa.mimo.musicfest.R;

import static android.content.ContentValues.TAG;

/**
 * Created by Javier on 15/05/2017.
 */

public class SoonEventsNotifications extends Service{


    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat dateFormatter2;
    private DatabaseReference mDatabase;
    private Integer id_not=1;

    private ArrayList<Event> events =new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent,  int flags, int startId) {

        mDatabase = FirebaseDatabase.getInstance().getReference();


        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormatter2 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        final Calendar calendar_target = Calendar.getInstance();
        calendar_target.add(Calendar.DAY_OF_MONTH,3);


        Log.d("SOONEVENTS","SOONEVENTS 2 "+calendar_target.getTime().toString());
        final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase.child("user-eventsFollow").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        events.clear();
                        for (DataSnapshot child : dataSnapshot.getChildren()) {

                            Calendar calendar_event = Calendar.getInstance();
                            Event event= child.getValue(Event.class);
                            // e.setImg(R.drawable.sp);
                            try {
                                calendar_event.setTime(dateFormatter.parse(event.getStartDate()));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(calendar_event.compareTo(calendar_target)<0){

                                try {
                                    launchNotification(event.getTitle(),"Evento pronto ",dateFormatter2.format(dateFormatter.parse(event.getStartDate())));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            events.add(event);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // ...
                    }
                });


        return START_STICKY_COMPATIBILITY;
    }


    public void launchNotification (String title, String text, String text2){


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_target)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .setContentTitle(title)
                        .setContentText(text2)
                        .setTicker(title);

        Intent notIntent =
                new Intent(getApplicationContext(), MenuActivity.class);

        PendingIntent contIntent =
                PendingIntent.getActivity(getApplicationContext(), 0, notIntent, 0);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contIntent);


        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id_not, mBuilder.build());
        id_not++;
        if(id_not==3){
            id_not=0;
        }
    }
}
