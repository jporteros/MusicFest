package es.upsa.mimo.musicfest.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.util.Calendar;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by Javier on 15/05/2017.
 */

public class OnBootReceiver extends BroadcastReceiver {
    private static final String ALARM_ACTION = "es.upsa.mimo.musicfest.services.ALARM_ACTION";
    private static final String ALARM_CANCEL = "es.upsa.mimo.musicfest.services.ALARM_CANCEL";
    private static final String ALARM_START = "es.upsa.mimo.musicfest.services.ALARM_START";
    private static final String APP_OPENED = "es.upsa.mimo.musicfest.services.APP_OPENED";
    private static Boolean bootComplete=false;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public void onReceive(Context context, Intent intent) {
        Log.e("=== OnBootReceiver ===", "onReceive"+intent.getAction().toString());



        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            bootComplete = true;
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notications", true))
                 setRepeatingAlarm(context);
        }
        else if (intent.getAction().equals(ALARM_ACTION)) {
            context.startService(new Intent(context,SoonEventsNotifications.class));
        }else if(intent.getAction().equals(ALARM_CANCEL)) {
            context.stopService(new Intent(context,SoonEventsNotifications.class));
            if(alarmManager!=null && pendingIntent!=null){
                alarmManager.cancel(pendingIntent);
            }
        }else if (intent.getAction().equals(ALARM_START)) {
           setRepeatingAlarm(context);
        }else if(intent.getAction().equals(APP_OPENED)){
            if ( !bootComplete && PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notications", true))
                setRepeatingAlarm(context);
        }


    }

    public void setRepeatingAlarm (Context context){
        alarmManager=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent = new Intent(context, OnBootReceiver.class);
        mIntent.setAction(ALARM_ACTION);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR,1);
        calendar.set(Calendar.HOUR_OF_DAY, 10); // For 10 AM
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
    }


}
